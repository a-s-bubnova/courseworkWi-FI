package ru.moscow.wifi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.moscow.wifi.dto.WifiPointDTO;
import ru.moscow.wifi.model.WifiPoint;
import ru.moscow.wifi.repository.WifiPointRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WifiPointService {
    
    @Autowired
    private WifiPointRepository wifiPointRepository;
    
    /**
     * Получить все точки Wi-Fi
     */
    public List<WifiPointDTO> getAllPoints() {
        List<WifiPoint> points = wifiPointRepository.findAll();
        return convertToDTO(points);
    }
    
    /**
     * Получить точки по типу
     */
    public List<WifiPointDTO> getPointsByType(String type) {
        List<WifiPoint> points = wifiPointRepository.findByType(type);
        return convertToDTO(points);
    }
    
    /**
     * Получить точки в радиусе от координаты
     */
    public List<WifiPointDTO> getPointsNearLocation(Double lat, Double lng, Integer radius) {
        List<WifiPoint> points = wifiPointRepository.findNearLocation(lat, lng, radius);
        return convertToDTO(points);
    }
    
    /**
     * Конвертировать модель в DTO
     */
    private List<WifiPointDTO> convertToDTO(List<WifiPoint> points) {
        return points.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private WifiPointDTO convertToDTO(WifiPoint point) {
        WifiPointDTO dto = new WifiPointDTO();
        dto.setId(point.getId());
        dto.setName(point.getName());
        dto.setType(point.getType());
        dto.setLat(point.getLatitude());
        dto.setLng(point.getLongitude());
        dto.setAddress(point.getAddress());
        dto.setCoverageRadius(point.getCoverageRadius());
        return dto;
    }
}
