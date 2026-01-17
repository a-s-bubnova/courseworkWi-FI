package ru.moscow.wifi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.moscow.wifi.util.JsonDataImporter;

import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для импорта данных из JSON файлов
 */
@RestController
@RequestMapping("/api/import")
@CrossOrigin(origins = "*")
public class ImportController {
    
    @Autowired
    private JsonDataImporter jsonDataImporter;
    
    /**
     * Импортировать все JSON файлы из папки data/raw/
     * Можно вызвать через GET для удобства (из браузера)
     */
    @GetMapping("/json")
    @PostMapping("/json")
    public ResponseEntity<Map<String, Object>> importJsonFiles() {
        try {
            System.out.println("=== НАЧАЛО ИМПОРТА ===");
            jsonDataImporter.importAllFiles();
            
            // Получаем количество записей в БД
            long totalPoints = jsonDataImporter.getTotalPointsCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Импорт завершён");
            response.put("total_points", totalPoints);
            
            System.out.println("=== ИМПОРТ ЗАВЕРШЁН. Всего точек: " + totalPoints + " ===");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== ОШИБКА ИМПОРТА ===");
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Ошибка импорта");
            error.put("message", e.getMessage());
            error.put("details", e.getClass().getName());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Импортировать один файл
     */
    @PostMapping("/json/{type}")
    public ResponseEntity<Map<String, Object>> importFile(
            @PathVariable String type) {
        try {
            String fileName = getFileNameByType(type);
            int imported = jsonDataImporter.importFile("data/raw/" + fileName, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imported", imported);
            response.put("type", type);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    private String getFileNameByType(String type) {
        switch (type) {
            case "library": return "libraries.json";
            case "cinema": return "cinemas.json";
            case "cultural_center": return "cultural_centers.json";
            case "park": return "parks.json";
            case "city_wifi": return "city_wifi.json";
            default: return type + ".json";
        }
    }
}
