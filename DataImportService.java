package ru.moscow.wifi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.moscow.wifi.model.WifiPoint;
import ru.moscow.wifi.repository.WifiPointRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Сервис для загрузки и импорта данных с портала data.mos.ru
 */
@Service
public class DataImportService {
    
    @Autowired
    private WifiPointRepository wifiPointRepository;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // URL датасетов с портала data.mos.ru
    private static final String DATASET_LIBRARIES = "https://apidata.mos.ru/v1/datasets/60788/rows";
    private static final String DATASET_CINEMAS = "https://apidata.mos.ru/v1/datasets/60789/rows";
    private static final String DATASET_CULTURAL_CENTERS = "https://apidata.mos.ru/v1/datasets/60790/rows";
    private static final String DATASET_PARKS = "https://apidata.mos.ru/v1/datasets/861/rows";
    private static final String DATASET_CITY_WIFI = "https://apidata.mos.ru/v1/datasets/2756/rows";
    
    /**
     * Загрузить все датасеты и импортировать в БД
     */
    public int importAllDatasets(String apiKey) {
        int totalImported = 0;
        
        try {
            // Загружаем каждый датасет
            totalImported += importDataset(DATASET_LIBRARIES, "library", apiKey);
            totalImported += importDataset(DATASET_CINEMAS, "cinema", apiKey);
            totalImported += importDataset(DATASET_CULTURAL_CENTERS, "cultural_center", apiKey);
            totalImported += importDataset(DATASET_PARKS, "park", apiKey);
            totalImported += importDataset(DATASET_CITY_WIFI, "city_wifi", apiKey);
            
        } catch (Exception e) {
            System.err.println("Ошибка импорта данных: " + e.getMessage());
            e.printStackTrace();
        }
        
        return totalImported;
    }
    
    /**
     * Импортировать один датасет
     */
    private int importDataset(String url, String type, String apiKey) {
        try {
            System.out.println("Загрузка датасета: " + type);
            
            // Формируем URL с API ключом
            String fullUrl = url + "?api_key=" + apiKey + "&$top=10000";
            
            // Загружаем данные
            ResponseEntity<Map> response = restTemplate.getForEntity(fullUrl, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> rows = (List<Map<String, Object>>) body.get("data");
                
                if (rows == null) {
                    rows = (List<Map<String, Object>>) body.get("value");
                }
                
                if (rows != null) {
                    int imported = parseAndSave(rows, type);
                    System.out.println("Импортировано " + imported + " точек типа " + type);
                    return imported;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки датасета " + type + ": " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Парсить и сохранить точки Wi-Fi
     */
    private int parseAndSave(List<Map<String, Object>> rows, String type) {
        int imported = 0;
        List<WifiPoint> pointsToSave = new ArrayList<>();
        
        for (Map<String, Object> row : rows) {
            try {
                WifiPoint point = parseRow(row, type);
                if (point != null && point.getLatitude() != null && point.getLongitude() != null) {
                    pointsToSave.add(point);
                }
            } catch (Exception e) {
                System.err.println("Ошибка парсинга строки: " + e.getMessage());
            }
        }
        
        // Сохраняем все точки
        if (!pointsToSave.isEmpty()) {
            wifiPointRepository.saveAll(pointsToSave);
            imported = pointsToSave.size();
        }
        
        return imported;
    }
    
    /**
     * Парсить одну строку данных
     */
    private WifiPoint parseRow(Map<String, Object> row, String type) {
        WifiPoint point = new WifiPoint();
        
        // Структура данных может отличаться, пробуем разные варианты
        Map<String, Object> cells = (Map<String, Object>) row.get("Cells");
        if (cells == null) {
            cells = row; // Если данных нет в Cells, используем сам row
        }
        
        // Извлекаем название
        String name = extractString(cells, "Name", "name", "Наименование", "Название");
        point.setName(name != null ? name : "Безымянная точка");
        
        // Извлекаем координаты
        Double lat = extractDouble(cells, "Latitude", "latitude", "lat", "Широта");
        Double lng = extractDouble(cells, "Longitude", "longitude", "lng", "Долгота");
        
        if (lat == null || lng == null) {
            // Пробуем извлечь из поля Coordinates
            String coordinates = extractString(cells, "Coordinates", "coordinates", "Координаты");
            if (coordinates != null) {
                String[] coords = coordinates.split(",");
                if (coords.length >= 2) {
                    try {
                        lat = Double.parseDouble(coords[0].trim());
                        lng = Double.parseDouble(coords[1].trim());
                    } catch (NumberFormatException e) {
                        // Игнорируем
                    }
                }
            }
        }
        
        point.setLatitude(lat);
        point.setLongitude(lng);
        point.setType(type);
        
        // Извлекаем адрес
        String address = extractString(cells, "Address", "address", "Адрес");
        point.setAddress(address);
        
        // Радиус покрытия по умолчанию
        point.setCoverageRadius(50);
        
        // Источник данных
        point.setSourceDataset(type);
        
        return point;
    }
    
    /**
     * Извлечь строку из Map по разным возможным ключам
     */
    private String extractString(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
    
    /**
     * Извлечь число из Map по разным возможным ключам
     */
    private Double extractDouble(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                try {
                    if (value instanceof Number) {
                        return ((Number) value).doubleValue();
                    } else {
                        return Double.parseDouble(value.toString());
                    }
                } catch (NumberFormatException e) {
                    // Игнорируем
                }
            }
        }
        return null;
    }
    
    /**
     * Импортировать данные из JSON файла (если загрузили вручную)
     */
    public int importFromJson(List<Map<String, Object>> data, String type) {
        return parseAndSave(data, type);
    }
    
    /**
     * Получить общее количество точек в БД
     */
    public long getTotalCount() {
        return wifiPointRepository.count();
    }
}
