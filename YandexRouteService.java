package ru.moscow.wifi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с Яндекс Маршрутизатором API
 */
@Service
public class YandexRouteService {
    
    @Value("${yandex.api.key:}")
    private String yandexApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String YANDEX_ROUTER_URL = "https://api.routing.yandex.net/v2/route";
    
    /**
     * Получить базовый маршрут от точки A до точки B
     * @param fromLat широта точки A
     * @param fromLng долгота точки A
     * @param toLat широта точки B
     * @param toLng долгота точки B
     * @return список координат маршрута
     */
    public List<double[]> getBaseRoute(double fromLat, double fromLng, double toLat, double toLng) {
        try {
            String url = String.format("%s?apikey=%s&waypoints=%f,%f;%f,%f&mode=driving&format=json",
                    YANDEX_ROUTER_URL, yandexApiKey, fromLat, fromLng, toLat, toLng);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                // TODO: Парсинг ответа от Яндекс API
                // Пока возвращаем простой маршрут
                return getSimpleRoute(fromLat, fromLng, toLat, toLng);
            }
        } catch (Exception e) {
            System.err.println("Ошибка получения маршрута от Яндекс: " + e.getMessage());
        }
        
        // Fallback: простой маршрут
        return getSimpleRoute(fromLat, fromLng, toLat, toLng);
    }
    
    /**
     * Простой маршрут (прямая линия) - для тестирования
     */
    private List<double[]> getSimpleRoute(double fromLat, double fromLng, double toLat, double toLng) {
        List<double[]> route = new ArrayList<>();
        route.add(new double[]{fromLat, fromLng});
        route.add(new double[]{toLat, toLng});
        return route;
    }
}
