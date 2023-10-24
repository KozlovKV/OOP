package kozlov.kirill.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Graph implementation with adjacency lists.
 *
 * @param <T> type of graph's vertices.
 */
public class ListGraph<T> extends AbstractGraph<T> {
    Map<Vertex<T>, List<Edge<T>>> adjacencyLists = new HashMap<>();

    public ListGraph() {
        super(false);
    }

    public ListGraph(boolean oriented) {
        super(oriented);
    }

    @Override
    public Vertex<T> addVertex(T value) {
        var vertex = super.addVertex(value);
        adjacencyLists.putIfAbsent(vertex, new ArrayList<>());
        return vertex;
    }

    @Override
    public Vertex<T> removeVertex(T value) {
        var vertex = super.removeVertex(value);
        for (var edge : getEdgesToVertex(vertex)) {
            var fromVertex = getVertex(edge.getFrom());
            adjacencyLists.get(fromVertex).remove(edge);
        }
        adjacencyLists.remove(vertex);
        return vertex;
    }

    @Override
    public boolean addEdge(T a, T b, double weight) {
        var vertexFrom = addVertex(a);
        var vertexTo = addVertex(b);
        var edgeFromTo = new Edge<>(a, b, weight);
        if (adjacencyLists.get(vertexFrom).contains(edgeFromTo)) {
            return false;
        }
        adjacencyLists.get(vertexFrom).add(edgeFromTo);
        if (!directed) {
            var edgeToFrom = new Edge<>(b, a, weight);
            adjacencyLists.get(vertexTo).add(edgeToFrom);
        }
        return true;
    }

    @Override
    public boolean removeEdge(T a, T b) {
        var vertexFrom = getVertex(a);
        var vertexTo = getVertex(b);
        if (vertexFrom == null || vertexTo == null) {
            return false;
        }
        var res = adjacencyLists.get(vertexFrom).remove(new Edge<>(a, b));
        if (!directed) {
            return adjacencyLists.get(vertexTo).remove(new Edge<>(b, a));
        }
        return res;
    }

    @Override
    public Edge<T> getEdge(T a, T b) {
        var vertexFrom = getVertex(a);
        var vertexTo = getVertex(b);
        if (vertexFrom == null || vertexTo == null) {
            return null;
        }
        var tmpEdge = new Edge<>(a, b);
        int index = adjacencyLists.get(vertexFrom).indexOf(tmpEdge);
        if (index == -1) {
            return null;
        }
        return adjacencyLists.get(vertexFrom).get(index);
    }

    @Override
    public List<Edge<T>> getEdgesFromVertex(Vertex<T> vertex) {
        return adjacencyLists.get(vertex);
    }

    @Override
    public List<Edge<T>> getEdgesToVertex(Vertex<T> vertex) {
        ArrayList<Edge<T>> edges = new ArrayList<>();
        for (var edgesLists : adjacencyLists.values()) {
            for (var edge : edgesLists) {
                if (edge.getTo().equals(vertex.getValue())) {
                    edges.add(edge);
                }
            }
        }
        return edges;
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (var vertex : adjacencyLists.keySet()) {
            builder.append(vertex.toString());
            builder.append(": ");
            builder.append(adjacencyLists.get(vertex).toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
