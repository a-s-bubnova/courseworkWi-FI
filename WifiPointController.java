package ru.moscow.wifi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.moscow.wifi.dto.WifiPointDTO;
import ru.moscow.wifi.service.WifiPointService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wifi-points")
@CrossOrigin(origins = "*")
public class WifiPointController {
    
    @Autowired
    private WifiPointService wifiPointService;
    
    // Получить все точки Wi-Fi
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPoints(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false, defaultValue = "500") Integer radius) {
        
        try {
            List<WifiPointDTO> points;
            
            // Если указаны координаты - ищем в радиусе
            if (lat != null && lng != null) {
                points = wifiPointService.getPointsNearLocation(lat, lng, radius);
            } else {
                // Иначе все точки
                points = wifiPointService.getAllPoints();
            }
            
            // Фильтр по типу, если указан
            if (type != null && !type.isEmpty()) {
                points = points.stream()
                        .filter(p -> p.getType().equals(type))
                        .toList();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", points.size());
            response.put("data", points);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Ошибка получения точек Wi-Fi");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    // Получить ближайшие точки
    @GetMapping("/nearby")
    public ResponseEntity<Map<String, Object>> getNearby(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false, defaultValue = "500") Integer radius) {
        
        try {
            List<WifiPointDTO> points = wifiPointService.getPointsNearLocation(lat, lng, radius);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", points.size());
            response.put("data", points);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Ошибка поиска ближайших точек");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
