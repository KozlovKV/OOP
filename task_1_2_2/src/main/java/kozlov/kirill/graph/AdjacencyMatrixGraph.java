package kozlov.kirill.graph;

import java.util.*;

public class AdjacencyMatrixGraph<T> extends Graph<T> {
    Map<Vertex<T>, Map<Vertex<T>, Edge<T>>> adjacencyMatrix = new HashMap<>();

    public AdjacencyMatrixGraph() {
        super(false);
    }

    public AdjacencyMatrixGraph(boolean oriented) {
        super(oriented);
    }

    @Override
    public Vertex<T> addVertex(T value) {
        var vertex = super.addVertex(value);
        adjacencyMatrix.putIfAbsent(vertex, new HashMap<>());
        for (var vertexFromMap : adjacencyMatrix.keySet()) {
            adjacencyMatrix.get(vertexFromMap).putIfAbsent(vertex, null);
            adjacencyMatrix.get(vertex).putIfAbsent(vertexFromMap, null);
        }
        return vertex;
    }

    @Override
    public Vertex<T> removeVertex(T value) {
        var vertex = super.removeVertex(value);
        adjacencyMatrix.remove(vertex);
        for (var row : adjacencyMatrix.values()) {
            row.remove(vertex);
        }
        return vertex;
    }

    @Override
    public boolean addEdge(T a, T b, double weight) {
        var vertexFrom = addVertex(a);
        var vertexTo = addVertex(b);
        if (adjacencyMatrix.get(vertexFrom).getOrDefault(vertexTo, null) != null) {
            return false;
        }
        var edgeFromTo = new Edge<>(a, b, weight);
        adjacencyMatrix.get(vertexFrom).put(vertexTo, edgeFromTo);
        if (!oriented) {
            var edgeToFrom = new Edge<>(b, a, weight);
            adjacencyMatrix.get(vertexTo).put(vertexFrom, edgeToFrom);
        }
        return true;
    }

    @Override
    boolean removeEdge(T a, T b) {
        var vertexFrom = getVertex(a);
        var vertexTo = getVertex(b);
        if (vertexFrom == null || vertexTo == null) {
            return false;
        }
        var res = adjacencyMatrix.get(vertexFrom).put(vertexTo, null);
        if (!oriented) {
            adjacencyMatrix.get(vertexTo).put(vertexFrom, null);
        }
        return res != null;
    }

    @Override
    Edge<T> getEdge(T a, T b) {
        var vertexFrom = getVertex(a);
        var vertexTo = getVertex(b);
        return adjacencyMatrix.get(vertexFrom).getOrDefault(vertexTo, null);
    }

    @Override
    List<Edge<T>> getVertexEdges(Vertex<T> vertex) {
        var edgesList = new ArrayList<Edge<T>>();
        for (var vertexTo : adjacencyMatrix.get(vertex).keySet()) {
            var edge = adjacencyMatrix.get(vertex).get(vertexTo);
            if (edge == null) {
                continue;
            }
            edgesList.add(edge);
        }
        return edgesList;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder("    | ");
        for (var vertex : adjacencyMatrix.keySet()) {
            builder.append(vertex.getValue().toString());
            builder.append(" | ");
        }
        for (var vertex : adjacencyMatrix.keySet()) {
            builder.append("\n");
            builder.append(vertex.getValue().toString());
            builder.append("   | ");
            for (var vertexTo : adjacencyMatrix.get(vertex).keySet()) {
                var value = adjacencyMatrix.get(vertex).getOrDefault(vertexTo, null);
                String valueString = value == null ? "_" : String.valueOf(value.getWeight());
                builder.append(valueString);
                builder.append(" | ");
            }
        }
        return builder.toString();
    }
}
