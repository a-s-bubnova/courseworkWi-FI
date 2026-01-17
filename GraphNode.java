package ru.moscow.wifi.graph;

import ru.moscow.wifi.model.WifiPoint;

/**
 * Узел графа, представляющий точку Wi-Fi
 */
public class GraphNode {
    private final int id;
    private final WifiPoint wifiPoint;
    private final double lat;
    private final double lng;
    
    // Для алгоритма Dijkstra
    private double distanceFromStart = Double.MAX_VALUE;
    private GraphNode previousNode = null;
    private boolean visited = false;
    
    public GraphNode(int id, WifiPoint wifiPoint) {
        this.id = id;
        this.wifiPoint = wifiPoint;
        this.lat = wifiPoint.getLatitude();
        this.lng = wifiPoint.getLongitude();
    }
    
    public GraphNode(int id, double lat, double lng) {
        this.id = id;
        this.wifiPoint = null;
        this.lat = lat;
        this.lng = lng;
    }
    
    public int getId() {
        return id;
    }
    
    public WifiPoint getWifiPoint() {
        return wifiPoint;
    }
    
    public double getLat() {
        return lat;
    }
    
    public double getLng() {
        return lng;
    }
    
    public double getDistanceFromStart() {
        return distanceFromStart;
    }
    
    public void setDistanceFromStart(double distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }
    
    public GraphNode getPreviousNode() {
        return previousNode;
    }
    
    public void setPreviousNode(GraphNode previousNode) {
        this.previousNode = previousNode;
    }
    
    public boolean isVisited() {
        return visited;
    }
    
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    
    public void reset() {
        this.distanceFromStart = Double.MAX_VALUE;
        this.previousNode = null;
        this.visited = false;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return id == graphNode.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "GraphNode{id=" + id + ", lat=" + lat + ", lng=" + lng + "}";
    }
}
