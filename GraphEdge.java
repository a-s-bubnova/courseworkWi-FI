package ru.moscow.wifi.graph;

/**
 * Ребро графа, связывающее два узла
 */
public class GraphEdge {
    private final GraphNode from;
    private final GraphNode to;
    private final double weight; // расстояние в метрах
    
    public GraphEdge(GraphNode from, GraphNode to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
    
    public GraphNode getFrom() {
        return from;
    }
    
    public GraphNode getTo() {
        return to;
    }
    
    public double getWeight() {
        return weight;
    }
    
    @Override
    public String toString() {
        return "GraphEdge{from=" + from.getId() + ", to=" + to.getId() + ", weight=" + weight + "}";
    }
}
