package ru.moscow.wifi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.moscow.wifi.dto.RouteResponse;
import ru.moscow.wifi.service.RouteOptimizationService;

@RestController
@RequestMapping("/api/route")
@CrossOrigin(origins = "http://localhost:3000")
public class RouteController {
    
    @Autowired
    private RouteOptimizationService routeOptimizationService;
    
    // Построить оптимизированный маршрут
    @GetMapping
    public ResponseEntity<RouteResponse> buildRoute(
            @RequestParam Double from_lat,
            @RequestParam Double from_lng,
            @RequestParam Double to_lat,
            @RequestParam Double to_lng,
            @RequestParam(required = false) String optimize_for,
            @RequestParam(required = false) Integer max_deviation) {
        
        RouteResponse response = routeOptimizationService.buildOptimizedRoute(
                from_lat, from_lng, to_lat, to_lng, max_deviation);
        
        return ResponseEntity.ok(response);
    }
}
