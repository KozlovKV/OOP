package kozlov.kirill.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Graph implementation with incident matrix.
 *
 * @param <T> type of graph's vertices.
 */
public class IncidentMatrixGraph<T> extends AbstractGraph<T> {
    Map<Vertex<T>, Map<Edge<T>, Integer>> incidentMatrix = new HashMap<>();

    public IncidentMatrixGraph() {
        super(false);
    }

    public IncidentMatrixGraph(boolean oriented) {
        super(oriented);
    }

    @Override
    public Vertex<T> addVertex(T value) {
        var vertex = super.addVertex(value);
        if (incidentMatrix.containsKey(vertex)) {
            return vertex;
        }
        var newVertexMap = new HashMap<Edge<T>, Integer>();
        for (var edge : getEdgeSet()) {
            newVertexMap.put(edge, 0);
        }
        incidentMatrix.putIfAbsent(vertex, newVertexMap);
        return vertex;
    }

    @Override
    public Vertex<T> removeVertex(T value) {
        var vertex = super.removeVertex(value);
        incidentMatrix.remove(vertex);
        return vertex;
    }

    private int chooseValueForMatrixCell(
            Vertex<T> currentVertex, Vertex<T> fromVertex, Vertex<T> toVertex) {
        if (currentVertex.equals(fromVertex)) {
            return -1;
        }
        if (currentVertex.equals(toVertex)) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean addEdge(T a, T b, double weight) {
        var vertexFrom = addVertex(a);
        var vertexTo = addVertex(b);
        var fromToEdge = new Edge<>(a, b, weight);
        var toFromEdge = new Edge<>(b, a, weight);
        for (var vertex : incidentMatrix.keySet()) {
            var vertexMap = incidentMatrix.get(vertex);
            if (vertexMap.containsKey(fromToEdge)) {
                return false;
            }
            vertexMap.put(fromToEdge, chooseValueForMatrixCell(vertex, vertexFrom, vertexTo));
            if (!oriented) {
                vertexMap.put(toFromEdge, chooseValueForMatrixCell(vertex, vertexTo, vertexFrom));
            }
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
        for (var edgesMap : incidentMatrix.values()) {
            edgesMap.remove(new Edge<>(a, b));
            if (!oriented) {
                edgesMap.remove(new Edge<>(b, a));
            }
        }
        return true;
    }

    @Override
    Edge<T> getEdge(T a, T b) {
        var vertexFrom = getVertex(a);
        var vertexTo = getVertex(b);
        if (vertexFrom == null || vertexTo == null) {
            return null;
        }
        var fromEdgesMap = incidentMatrix.get(vertexFrom);
        var tmpEdge = new Edge<>(a, b);
        for (var edge : fromEdgesMap.keySet()) {
            if (edge.equals(tmpEdge) && fromEdgesMap.get(edge) == -1) {
                return edge;
            }
        }
        return null;
    }

    @Override
    List<Edge<T>> getVertexEdges(Vertex<T> vertex) {
        var edgesList = new ArrayList<Edge<T>>();
        var edgesMap = incidentMatrix.get(vertex);
        for (var edge : edgesMap.keySet()) {
            if (edgesMap.get(edge) == -1) {
                edgesList.add(edge);
            }
        }
        return edgesList;
    }

    @Override
    public Set<Edge<T>> getEdgeSet() {
        for (var edgesMap : incidentMatrix.values()) {
            return edgesMap.keySet();
        }
        return new HashSet<>();
    }

    @Override
    public String toString() {
        var builder = new StringBuilder("  | ");
        for (var edge : getEdgeSet()) {
            builder.append(edge.toString());
            builder.append(" | ");
        }
        for (var vertex : incidentMatrix.keySet()) {
            builder.append("\n");
            builder.append(vertex.getValue().toString());
            builder.append(" | ");
            for (var edgeIncident : incidentMatrix.get(vertex).values()) {
                builder.append(String.format("      %d       | ", edgeIncident));
            }
        }
        return builder.toString();
    }
}
