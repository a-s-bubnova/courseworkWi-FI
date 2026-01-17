package ru.moscow.wifi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.moscow.wifi.model.WifiPoint;

import java.util.List;

@Repository
public interface WifiPointRepository extends JpaRepository<WifiPoint, Integer> {
    
    // Найти все точки
    List<WifiPoint> findAll();
    
    // Найти по типу
    List<WifiPoint> findByType(String type);
    
    // Найти точки в радиусе (используя SQL функцию ST_Distance_Sphere)
    @Query(value = "SELECT * FROM wifi_points WHERE " +
            "ST_Distance_Sphere(location, ST_GeomFromText(CONCAT('POINT(', :lng, ' ', :lat, ')'), 4326)) <= :radius " +
            "ORDER BY ST_Distance_Sphere(location, ST_GeomFromText(CONCAT('POINT(', :lng, ' ', :lat, ')'), 4326)) " +
            "LIMIT 50", nativeQuery = true)
    List<WifiPoint> findNearLocation(@Param("lat") Double lat, 
                                     @Param("lng") Double lng, 
                                     @Param("radius") Integer radius);
}
