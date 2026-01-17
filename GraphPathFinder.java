package ru.moscow.wifi.graph;

import java.util.*;

/**
 * Алгоритм Dijkstra для поиска кратчайшего пути в графе
 */
public class GraphPathFinder {
    
    /**
     * Найти кратчайший путь от начального узла до конечного
     * Использует алгоритм Dijkstra
     * 
     * @param graph граф для поиска пути
     * @return список узлов, представляющих путь от A до B, или null если путь не найден
     */
    public List<GraphNode> findShortestPath(WifiGraph graph) {
        GraphNode start = graph.getStartNode();
        GraphNode end = graph.getEndNode();
        
        if (start == null || end == null) {
            return null;
        }
        
        // Сбрасываем состояние всех узлов
        graph.resetNodes();
        
        // Инициализация
        start.setDistanceFromStart(0.0);
        
        // Приоритетная очередь для узлов (сортировка по расстоянию)
        PriorityQueue<GraphNode> queue = new PriorityQueue<>(
                Comparator.comparingDouble(GraphNode::getDistanceFromStart)
        );
        queue.add(start);
        
        while (!queue.isEmpty()) {
            GraphNode current = queue.poll();
            
            // Если достигли конечной точки, строим путь
            if (current.equals(end)) {
                return buildPath(end);
            }
            
            // Если узел уже посещён, пропускаем
            if (current.isVisited()) {
                continue;
            }
            
            current.setVisited(true);
            
            // Обрабатываем всех соседей
            List<GraphEdge> edges = graph.getEdges(current.getId());
            for (GraphEdge edge : edges) {
                GraphNode neighbor = edge.getTo();
                
                if (neighbor.isVisited()) {
                    continue;
                }
                
                // Вычисляем новое расстояние
                double newDistance = current.getDistanceFromStart() + edge.getWeight();
                
                // Если нашли более короткий путь, обновляем
                if (newDistance < neighbor.getDistanceFromStart()) {
                    neighbor.setDistanceFromStart(newDistance);
                    neighbor.setPreviousNode(current);
                    queue.add(neighbor);
                }
            }
        }
        
        // Путь не найден
        return null;
    }
    
    /**
     * Построить путь от конечного узла до начального
     * (идём по ссылкам previousNode)
     */
    private List<GraphNode> buildPath(GraphNode endNode) {
        List<GraphNode> path = new ArrayList<>();
        GraphNode current = endNode;
        
        while (current != null) {
            path.add(current);
            current = current.getPreviousNode();
        }
        
        // Разворачиваем путь (от начала к концу)
        Collections.reverse(path);
        
        return path;
    }
    
    /**
     * Найти путь с максимальным покрытием Wi-Fi
     * Использует модифицированный алгоритм Dijkstra с приоритетом на точки Wi-Fi
     * 
     * @param graph граф для поиска пути
     * @param wifiPriority приоритет точек Wi-Fi (1.0 = без приоритета, < 1.0 = приоритет выше)
     * @return список узлов, представляющих путь от A до B
     */
    public List<GraphNode> findPathWithMaxWifiCoverage(WifiGraph graph, double wifiPriority) {
        GraphNode start = graph.getStartNode();
        GraphNode end = graph.getEndNode();
        
        if (start == null || end == null) {
            return null;
        }
        
        graph.resetNodes();
        start.setDistanceFromStart(0.0);
        
        PriorityQueue<GraphNode> queue = new PriorityQueue<>(
                Comparator.comparingDouble(GraphNode::getDistanceFromStart)
        );
        queue.add(start);
        
        while (!queue.isEmpty()) {
            GraphNode current = queue.poll();
            
            if (current.equals(end)) {
                return buildPath(end);
            }
            
            if (current.isVisited()) {
                continue;
            }
            
            current.setVisited(true);
            
            List<GraphEdge> edges = graph.getEdges(current.getId());
            for (GraphEdge edge : edges) {
                GraphNode neighbor = edge.getTo();
                
                if (neighbor.isVisited()) {
                    continue;
                }
                
                // Модифицируем вес с учётом приоритета Wi-Fi
                double edgeWeight = edge.getWeight();
                
                // Если сосед - точка Wi-Fi, уменьшаем вес (приоритет выше)
                if (neighbor.getWifiPoint() != null) {
                    edgeWeight *= wifiPriority;
                }
                
                double newDistance = current.getDistanceFromStart() + edgeWeight;
                
                if (newDistance < neighbor.getDistanceFromStart()) {
                    neighbor.setDistanceFromStart(newDistance);
                    neighbor.setPreviousNode(current);
                    queue.add(neighbor);
                }
            }
        }
        
        return null;
    }
}
