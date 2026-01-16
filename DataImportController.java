package ru.moscow.wifi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.moscow.wifi.service.DataImportService;

import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для импорта данных с портала data.mos.ru
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class DataImportController {
    
    @Autowired
    private DataImportService dataImportService;
    
    /**
     * Импортировать все датасеты с портала data.mos.ru
     * 
     * Параметры:
     * - api_key (опционально) - API ключ для доступа к порталу
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importDatasets(
            @RequestParam(required = false) String api_key) {
        
        try {
            // Если API ключ не указан, можно попробовать без него
            // (некоторые датасеты доступны публично)
            String apiKey = api_key != null ? api_key : "";
            
            int imported = dataImportService.importAllDatasets(apiKey);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Импорт завершён");
            response.put("imported", imported);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Ошибка импорта данных");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Проверить количество точек в БД
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            long totalCount = dataImportService.getTotalCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total_points", totalCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
