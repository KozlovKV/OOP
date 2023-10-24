package kozlov.kirill.graph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Abstract graph's class.
 *
 * @param <T> type of graph's vertices
 */
abstract class AbstractGraph<T> {
    Map<T, Vertex<T>> vertexMap = new HashMap<>();
    boolean directed = false;

    public AbstractGraph(boolean directed) {
        this.directed = directed;
    }

    public Vertex<T> getVertex(T value) {
        return vertexMap.getOrDefault(value, null);
    }

    public void changeDirectionType(boolean newOrientation) {
        if (getEdgeSet().isEmpty()) {
            directed = newOrientation;
        }
    }

    public Vertex<T> addVertex(T value) {
        if (vertexMap.containsKey(value)) {
            return vertexMap.get(value);
        }
        var newVertex = new Vertex<>(value);
        vertexMap.putIfAbsent(value, newVertex);
        return newVertex;
    }

    public Vertex<T> removeVertex(T value) {
        if (!vertexMap.containsKey(value)) {
            return null;
        }
        var vertex = vertexMap.get(value);
        vertexMap.remove(value);
        return vertex;
    }

    public abstract boolean addEdge(T a, T b, double weight);

    public abstract boolean removeEdge(T a, T b);

    public abstract Edge<T> getEdge(T a, T b);

    public abstract List<Edge<T>> getEdgesFromVertex(Vertex<T> vertex);

    public abstract List<Edge<T>> getEdgesToVertex(Vertex<T> vertex);

    public Set<Edge<T>> getEdgeSet() {
        var edgesSet = new HashSet<Edge<T>>();
        for (var vertex : vertexMap.values()) {
            edgesSet.addAll(getEdgesFromVertex(vertex));
            edgesSet.addAll(getEdgesToVertex(vertex));
        }
        return edgesSet;
    }

    public boolean hasNegativeEdges() {
        var edgesSet = getEdgeSet();
        return edgesSet.stream().anyMatch(edge -> edge.getWeight() < 0);
    }

    public Map<T, Double> constructShortestDistances(T start) {
        // Initialization
        vertexMap.values().forEach(Vertex::resetDistance);
        var startVertex = getVertex(start);
        if (startVertex == null) {
            return null;
        }
        startVertex.setDistance(0);
        // Choose algorithm
        if (hasNegativeEdges()) {
            return bellmanFord();
        }
        return dijkstra();
    }

    protected boolean relaxEdge(Edge<T> edge) {
        var fromVertex = getVertex(edge.getFrom());
        var toVertex = getVertex(edge.getTo());
        if (toVertex.getDistance() > fromVertex.getDistance() + edge.getWeight()) {
            toVertex.setDistance(fromVertex.getDistance() + edge.getWeight());
            return true;
        }
        return false;
    }

    protected Map<T, Double> dijkstra() {
        var resultDistances = new HashMap<T, Double>();
        var minHeap = new PriorityQueue<Vertex<T>>(Comparator.comparingDouble(Vertex::getDistance));
        minHeap.addAll(vertexMap.values());
        while (!minHeap.isEmpty()) {
            var currentVertex = minHeap.poll();
            resultDistances.put(currentVertex.getValue(), currentVertex.getDistance());
            for (var edge : getEdgesFromVertex(currentVertex)) {
                if (relaxEdge(edge)) {
                    var toVertex = getVertex(edge.getTo());
                    minHeap.remove(toVertex);
                    minHeap.add(toVertex);
                }
            }
        }
        return resultDistances;
    }

    protected Map<T, Double> bellmanFord() {
        var resultDistances = new HashMap<T, Double>();
        var edges = getEdgeSet();
        for (int i = 0; i < vertexMap.size(); ++i) {
            for (var edge : edges) {
                if (relaxEdge(edge) && i == vertexMap.size() - 1) {
                    return null;
                }
            }
        }
        for (var vertex : vertexMap.values()) {
            resultDistances.put(vertex.getValue(), vertex.getDistance());
        }
        return resultDistances;
    }
}
