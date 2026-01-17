package ru.moscow.wifi.graph;

import ru.moscow.wifi.model.WifiPoint;
import ru.moscow.wifi.util.GeometryUtil;

import java.util.*;

/**
 * Граф точек Wi-Fi для построения оптимального маршрута
 */
public class WifiGraph {
    private final Map<Integer, GraphNode> nodes;
    private final Map<Integer, List<GraphEdge>> adjacencyList;
    private static final double MAX_EDGE_DISTANCE = 500; // максимальное расстояние между узлами в метрах
    private static final double COVERAGE_RADIUS = 50; // радиус покрытия Wi-Fi в метрах
    
    public WifiGraph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }
    
    /**
     * Добавить узел в граф
     */
    public void addNode(GraphNode node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }
    
    /**
     * Построить граф из списка точек Wi-Fi
     * Создаёт рёбра между точками, которые находятся в пределах MAX_EDGE_DISTANCE
     */
    public void buildGraphFromWifiPoints(List<WifiPoint> wifiPoints, 
                                         double fromLat, double fromLng,
                                         double toLat, double toLng) {
        // Очищаем граф
        nodes.clear();
        adjacencyList.clear();
        
        // Добавляем начальную точку (A)
        GraphNode startNode = new GraphNode(-1, fromLat, fromLng);
        addNode(startNode);
        
        // Добавляем конечную точку (B)
        GraphNode endNode = new GraphNode(-2, toLat, toLng);
        addNode(endNode);
        
        // Добавляем точки Wi-Fi как узлы
        int nodeId = 0;
        for (WifiPoint point : wifiPoints) {
            GraphNode node = new GraphNode(nodeId++, point);
            addNode(node);
        }
        
        // Строим рёбра между узлами
        List<GraphNode> nodeList = new ArrayList<>(nodes.values());
        
        for (int i = 0; i < nodeList.size(); i++) {
            GraphNode from = nodeList.get(i);
            
            for (int j = i + 1; j < nodeList.size(); j++) {
                GraphNode to = nodeList.get(j);
                
                double distance = GeometryUtil.calculateDistance(
                        from.getLat(), from.getLng(),
                        to.getLat(), to.getLng());
                
                // Создаём ребро, если расстояние не превышает максимум
                if (distance <= MAX_EDGE_DISTANCE) {
                    // Вес ребра = расстояние + штраф за отсутствие Wi-Fi покрытия
                    double weight = calculateEdgeWeight(from, to, distance);
                    
                    addEdge(from, to, weight);
                }
            }
        }
    }
    
    /**
     * Вычислить вес ребра
     * Учитывает расстояние и покрытие Wi-Fi
     */
    private double calculateEdgeWeight(GraphNode from, GraphNode to, double distance) {
        double weight = distance;
        
        // Штраф, если на ребре нет покрытия Wi-Fi
        // Если хотя бы один из узлов - точка Wi-Fi, покрытие есть
        boolean hasCoverage = (from.getWifiPoint() != null || to.getWifiPoint() != null);
        
        if (!hasCoverage) {
            // Штраф за отсутствие покрытия (увеличиваем вес)
            weight *= 1.5;
        } else {
            // Бонус за наличие покрытия (уменьшаем вес)
            weight *= 0.9;
        }
        
        return weight;
    }
    
    /**
     * Добавить ребро в граф (двунаправленное)
     */
    private void addEdge(GraphNode from, GraphNode to, double weight) {
        adjacencyList.get(from.getId()).add(new GraphEdge(from, to, weight));
        adjacencyList.get(to.getId()).add(new GraphEdge(to, from, weight));
    }
    
    /**
     * Получить узел по ID
     */
    public GraphNode getNode(int id) {
        return nodes.get(id);
    }
    
    /**
     * Получить начальный узел (точка A)
     */
    public GraphNode getStartNode() {
        return nodes.get(-1);
    }
    
    /**
     * Получить конечный узел (точка B)
     */
    public GraphNode getEndNode() {
        return nodes.get(-2);
    }
    
    /**
     * Получить список рёбер, исходящих из узла
     */
    public List<GraphEdge> getEdges(int nodeId) {
        return adjacencyList.getOrDefault(nodeId, new ArrayList<>());
    }
    
    /**
     * Получить все узлы графа
     */
    public Collection<GraphNode> getAllNodes() {
        return nodes.values();
    }
    
    /**
     * Сбросить состояние всех узлов (для повторного использования алгоритма)
     */
    public void resetNodes() {
        for (GraphNode node : nodes.values()) {
            node.reset();
        }
    }
    
    /**
     * Получить количество узлов
     */
    public int getNodeCount() {
        return nodes.size();
    }
    
    /**
     * Получить количество рёбер
     */
    public int getEdgeCount() {
        int count = 0;
        for (List<GraphEdge> edges : adjacencyList.values()) {
            count += edges.size();
        }
        return count / 2; // делим на 2, т.к. рёбра двунаправленные
    }
}
