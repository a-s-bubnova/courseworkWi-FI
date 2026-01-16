package ru.moscow.wifi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.moscow.wifi.dto.RouteResponse;
import ru.moscow.wifi.dto.WifiPointDTO;
import ru.moscow.wifi.model.WifiPoint;
import ru.moscow.wifi.repository.WifiPointRepository;
import ru.moscow.wifi.util.GeometryUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сервис для оптимизации маршрута с максимальным покрытием Wi-Fi
 */
@Service
public class RouteOptimizationService {
    
    @Autowired
    private WifiPointRepository wifiPointRepository;
    
    @Autowired
    private YandexRouteService yandexRouteService;
    
    private static final int COVERAGE_RADIUS = 50; // радиус покрытия Wi-Fi в метрах
    private static final int MAX_DEVIATION = 200; // максимальное отклонение в метрах
    
    /**
     * Построить оптимизированный маршрут
     */
    public RouteResponse buildOptimizedRoute(double fromLat, double fromLng, 
                                            double toLat, double toLng,
                                            Integer maxDeviation) {
        RouteResponse response = new RouteResponse();
        response.setSuccess(true);
        
        try {
            // 1. Получить базовый маршрут
            List<double[]> baseRoute = yandexRouteService.getBaseRoute(fromLat, fromLng, toLat, toLng);
            
            // 2. Найти точки Wi-Fi в радиусе от маршрута
            List<WifiPoint> wifiPoints = findWifiPointsNearRoute(baseRoute, 200);
            
            // 3. Оптимизировать маршрут
            List<RouteResponse.Coordinate> optimizedRoute = optimizeRoute(
                    baseRoute, wifiPoints, maxDeviation != null ? maxDeviation : MAX_DEVIATION);
            
            // 4. Найти зоны покрытия
            List<RouteResponse.CoverageZone> coverageZones = findCoverageZones(
                    optimizedRoute, wifiPoints);
            
            // 5. Вычислить статистику
            RouteResponse.RouteStatistics statistics = calculateStatistics(
                    optimizedRoute, coverageZones, wifiPoints);
            
            response.setRoute(optimizedRoute);
            response.setCoverageZones(coverageZones);
            response.setStatistics(statistics);
            
        } catch (Exception e) {
            response.setSuccess(false);
            System.err.println("Ошибка построения маршрута: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    
    /**
     * Найти точки Wi-Fi в радиусе от маршрута
     */
    private List<WifiPoint> findWifiPointsNearRoute(List<double[]> route, int searchRadius) {
        Set<Integer> foundIds = new HashSet<>();
        List<WifiPoint> result = new ArrayList<>();
        
        for (double[] point : route) {
            List<WifiPoint> nearby = wifiPointRepository.findNearLocation(
                    point[0], point[1], searchRadius);
            
            for (WifiPoint wifiPoint : nearby) {
                if (!foundIds.contains(wifiPoint.getId())) {
                    foundIds.add(wifiPoint.getId());
                    result.add(wifiPoint);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Оптимизировать маршрут для максимального покрытия
     */
    private List<RouteResponse.Coordinate> optimizeRoute(List<double[]> baseRoute, 
                                                         List<WifiPoint> wifiPoints,
                                                         int maxDeviation) {
        List<RouteResponse.Coordinate> optimized = new ArrayList<>();
        
        for (int i = 0; i < baseRoute.size(); i++) {
            double[] point = baseRoute.get(i);
            optimized.add(new RouteResponse.Coordinate(point[0], point[1]));
            
            // Если не последняя точка, проверяем покрытие сегмента
            if (i < baseRoute.size() - 1) {
                double[] nextPoint = baseRoute.get(i + 1);
                
                // Проверяем, покрыт ли сегмент Wi-Fi
                boolean isCovered = isSegmentCovered(point[0], point[1], 
                                                     nextPoint[0], nextPoint[1], 
                                                     wifiPoints);
                
                // Если не покрыт, ищем ближайшую точку Wi-Fi
                if (!isCovered) {
                    WifiPoint nearest = findNearestWifiPoint(
                            (point[0] + nextPoint[0]) / 2,
                            (point[1] + nextPoint[1]) / 2,
                            wifiPoints, maxDeviation);
                    
                    if (nearest != null) {
                        optimized.add(new RouteResponse.Coordinate(
                                nearest.getLatitude(), nearest.getLongitude()));
                    }
                }
            }
        }
        
        return optimized;
    }
    
    /**
     * Проверить, покрыт ли сегмент маршрута Wi-Fi
     */
    private boolean isSegmentCovered(double lat1, double lng1, double lat2, double lng2,
                                    List<WifiPoint> wifiPoints) {
        // Упрощённая проверка: есть ли точка Wi-Fi, которая покрывает середину сегмента
        double midLat = (lat1 + lat2) / 2;
        double midLng = (lng1 + lng2) / 2;
        
        for (WifiPoint point : wifiPoints) {
            if (GeometryUtil.isPointInRadius(point.getLatitude(), point.getLongitude(),
                    midLat, midLng, COVERAGE_RADIUS)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Найти ближайшую точку Wi-Fi
     */
    private WifiPoint findNearestWifiPoint(double lat, double lng, 
                                          List<WifiPoint> wifiPoints, 
                                          int maxDistance) {
        WifiPoint nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (WifiPoint point : wifiPoints) {
            double distance = GeometryUtil.calculateDistance(
                    lat, lng, point.getLatitude(), point.getLongitude());
            
            if (distance < minDistance && distance <= maxDistance) {
                minDistance = distance;
                nearest = point;
            }
        }
        
        return nearest;
    }
    
    /**
     * Найти зоны покрытия вдоль маршрута
     */
    private List<RouteResponse.CoverageZone> findCoverageZones(
            List<RouteResponse.Coordinate> route, List<WifiPoint> wifiPoints) {
        List<RouteResponse.CoverageZone> zones = new ArrayList<>();
        Set<Integer> addedIds = new HashSet<>();
        
        for (RouteResponse.Coordinate coord : route) {
            for (WifiPoint point : wifiPoints) {
                if (!addedIds.contains(point.getId())) {
                    double distance = GeometryUtil.calculateDistance(
                            coord.getLat(), coord.getLng(),
                            point.getLatitude(), point.getLongitude());
                    
                    if (distance <= COVERAGE_RADIUS) {
                        RouteResponse.CoverageZone zone = new RouteResponse.CoverageZone();
                        zone.setWifiPointId(point.getId());
                        zone.setLat(point.getLatitude());
                        zone.setLng(point.getLongitude());
                        zone.setRadius(COVERAGE_RADIUS);
                        zones.add(zone);
                        addedIds.add(point.getId());
                    }
                }
            }
        }
        
        return zones;
    }
    
    /**
     * Вычислить статистику маршрута
     */
    private RouteResponse.RouteStatistics calculateStatistics(
            List<RouteResponse.Coordinate> route,
            List<RouteResponse.CoverageZone> coverageZones,
            List<WifiPoint> wifiPoints) {
        
        RouteResponse.RouteStatistics stats = new RouteResponse.RouteStatistics();
        
        // Вычисляем общую длину маршрута
        double totalDistance = 0;
        double coveredDistance = 0;
        
        for (int i = 0; i < route.size() - 1; i++) {
            RouteResponse.Coordinate start = route.get(i);
            RouteResponse.Coordinate end = route.get(i + 1);
            
            double segmentDistance = GeometryUtil.calculateDistance(
                    start.getLat(), start.getLng(),
                    end.getLat(), end.getLng());
            
            totalDistance += segmentDistance;
            
            // Проверяем покрытие сегмента
            if (isSegmentCovered(start.getLat(), start.getLng(),
                    end.getLat(), end.getLng(), wifiPoints)) {
                coveredDistance += segmentDistance;
            }
        }
        
        stats.setTotalDistanceMeters((int) Math.round(totalDistance));
        stats.setCoveredDistanceMeters((int) Math.round(coveredDistance));
        stats.setCoveragePercentage(totalDistance > 0 ? 
                (coveredDistance / totalDistance) * 100 : 0);
        stats.setWifiPointsAlongRoute(coverageZones.size());
        
        return stats;
    }
}
