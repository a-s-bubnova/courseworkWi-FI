package ru.moscow.wifi.dto;

public class RouteRequest {
    private Double fromLat;
    private Double fromLng;
    private Double toLat;
    private Double toLng;
    private String optimizeFor; // "coverage", "distance", "balanced"
    private Integer maxDeviation; // максимальное отклонение в метрах

    public RouteRequest() {}

    // Getters and Setters
    public Double getFromLat() {
        return fromLat;
    }

    public void setFromLat(Double fromLat) {
        this.fromLat = fromLat;
    }

    public Double getFromLng() {
        return fromLng;
    }

    public void setFromLng(Double fromLng) {
        this.fromLng = fromLng;
    }

    public Double getToLat() {
        return toLat;
    }

    public void setToLat(Double toLat) {
        this.toLat = toLat;
    }

    public Double getToLng() {
        return toLng;
    }

    public void setToLng(Double toLng) {
        this.toLng = toLng;
    }

    public String getOptimizeFor() {
        return optimizeFor;
    }

    public void setOptimizeFor(String optimizeFor) {
        this.optimizeFor = optimizeFor;
    }

    public Integer getMaxDeviation() {
        return maxDeviation;
    }

    public void setMaxDeviation(Integer maxDeviation) {
        this.maxDeviation = maxDeviation;
    }
}
