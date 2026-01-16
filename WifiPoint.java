package ru.moscow.wifi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "wifi_points")
public class WifiPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;
    private String type;
    private Double latitude;
    private Double longitude;
    private String address;
    private Integer coverageRadius;
    private String sourceDataset;
    
    @Column(name = "created_at", updatable = false, insertable = false)
    private java.sql.Timestamp createdAt;
    
    public WifiPoint() {}
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Integer getCoverageRadius() {
        return coverageRadius;
    }
    
    public void setCoverageRadius(Integer coverageRadius) {
        this.coverageRadius = coverageRadius;
    }
    
    public String getSourceDataset() {
        return sourceDataset;
    }
    
    public void setSourceDataset(String sourceDataset) {
        this.sourceDataset = sourceDataset;
    }
    
    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
