package ru.moscow.wifi.dto;

import java.util.List;

public class RouteResponse {
    private Boolean success;
    private List<Coordinate> route;
    private List<CoverageZone> coverageZones;
    private RouteStatistics statistics;

    public RouteResponse() {}

    // Getters and Setters
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Coordinate> getRoute() {
        return route;
    }

    public void setRoute(List<Coordinate> route) {
        this.route = route;
    }

    public List<CoverageZone> getCoverageZones() {
        return coverageZones;
    }

    public void setCoverageZones(List<CoverageZone> coverageZones) {
        this.coverageZones = coverageZones;
    }

    public RouteStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(RouteStatistics statistics) {
        this.statistics = statistics;
    }

    // Вложенные классы
    public static class Coordinate {
        private Double lat;
        private Double lng;

        public Coordinate() {}

        public Coordinate(Double lat, Double lng) {
            this.lat = lat;
            this.lng = lng;
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
    }

    public static class CoverageZone {
        private Integer wifiPointId;
        private Double lat;
        private Double lng;
        private Integer radius;

        public CoverageZone() {}

        public Integer getWifiPointId() {
            return wifiPointId;
        }

        public void setWifiPointId(Integer wifiPointId) {
            this.wifiPointId = wifiPointId;
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

        public Integer getRadius() {
            return radius;
        }

        public void setRadius(Integer radius) {
            this.radius = radius;
        }
    }

    public static class RouteStatistics {
        private Integer totalDistanceMeters;
        private Integer coveredDistanceMeters;
        private Double coveragePercentage;
        private Integer wifiPointsAlongRoute;

        public RouteStatistics() {}

        public Integer getTotalDistanceMeters() {
            return totalDistanceMeters;
        }

        public void setTotalDistanceMeters(Integer totalDistanceMeters) {
            this.totalDistanceMeters = totalDistanceMeters;
        }

        public Integer getCoveredDistanceMeters() {
            return coveredDistanceMeters;
        }

        public void setCoveredDistanceMeters(Integer coveredDistanceMeters) {
            this.coveredDistanceMeters = coveredDistanceMeters;
        }

        public Double getCoveragePercentage() {
            return coveragePercentage;
        }

        public void setCoveragePercentage(Double coveragePercentage) {
            this.coveragePercentage = coveragePercentage;
        }

        public Integer getWifiPointsAlongRoute() {
            return wifiPointsAlongRoute;
        }

        public void setWifiPointsAlongRoute(Integer wifiPointsAlongRoute) {
            this.wifiPointsAlongRoute = wifiPointsAlongRoute;
        }
    }
}
