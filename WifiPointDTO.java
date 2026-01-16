package ru.moscow.wifi.dto;

public class WifiPointDTO {
    private Integer id;
    private String name;
    private String type;
    private Double lat;
    private Double lng;
    private String address;
    private Integer coverageRadius;
    private Double distance; // расстояние в метрах (для ближайших точек)

    public WifiPointDTO() {}

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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
