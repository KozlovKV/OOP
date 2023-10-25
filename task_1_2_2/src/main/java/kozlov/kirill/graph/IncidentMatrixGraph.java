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
    /**
     * Enumeration describing incident-status between vertex and edge.
     */
    private enum IncidentDirection {
        FROM, ///< Vertex is on the first position in edge
        NONE, ///< There is no relation
        TO ///< Vertex is on the second position in edge
    }

    Map<Vertex<T>, Map<Edge<T>, IncidentDirection>> incidentMatrix = new HashMap<>();

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
        var newVertexMap = new HashMap<Edge<T>, IncidentDirection>();
        for (var edge : getEdgeSet()) {
            newVertexMap.put(edge, IncidentDirection.NONE);
        }
        incidentMatrix.putIfAbsent(vertex, newVertexMap);
        return vertex;
    }

    private IncidentDirection chooseValueForMatrixCell(
            Vertex<T> currentVertex, Vertex<T> fromVertex, Vertex<T> toVertex) {
        if (currentVertex.equals(fromVertex)) {
            return IncidentDirection.FROM;
        }
        if (currentVertex.equals(toVertex)) {
            return IncidentDirection.TO;
        }
        return IncidentDirection.NONE;
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
            if (!directed) {
                vertexMap.put(toFromEdge, chooseValueForMatrixCell(vertex, vertexTo, vertexFrom));
            }
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
        boolean flag = true;
        for (var edgesMap : incidentMatrix.values()) {
            flag = flag && edgesMap.remove(new Edge<>(a, b)) != null;
            if (!directed) {
                flag = flag && edgesMap.remove(new Edge<>(b, a)) != null;
            }
        }
        return flag;
    }

    @Override
    public void removeIncidentEdges(Vertex<T> vertex) {
        var edgesForDeletion = getEdgesFromVertex(vertex);
        edgesForDeletion.addAll(getEdgesToVertex(vertex));
        for (var edgesMaps : incidentMatrix.values()) {
            for (var edge : edgesForDeletion) {
                edgesMaps.remove(edge);
            }
        }
        incidentMatrix.remove(vertex);
    }

    @Override
    public Edge<T> getEdge(T a, T b) {
        var vertexFrom = getVertex(a);
        var vertexTo = getVertex(b);
        if (vertexFrom == null || vertexTo == null) {
            return null;
        }
        var fromEdgesMap = incidentMatrix.get(vertexFrom);
        var tmpEdge = new Edge<>(a, b);
        for (var edge : fromEdgesMap.keySet()) {
            if (edge.equals(tmpEdge) && fromEdgesMap.get(edge) == IncidentDirection.FROM) {
                return edge;
            }
        }
        return null;
    }

    @Override
    public List<Edge<T>> getEdgesFromVertex(Vertex<T> vertex) {
        var edgesList = new ArrayList<Edge<T>>();
        var edgesMap = incidentMatrix.get(vertex);
        for (var edge : edgesMap.keySet()) {
            if (edgesMap.get(edge) == IncidentDirection.FROM) {
                edgesList.add(edge);
            }
        }
        return edgesList;
    }

    @Override
    public List<Edge<T>> getEdgesToVertex(Vertex<T> vertex) {
        var edgesList = new ArrayList<Edge<T>>();
        var edgesMap = incidentMatrix.get(vertex);
        for (var edge : edgesMap.keySet()) {
            if (edgesMap.get(edge) == IncidentDirection.TO) {
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

    @ExcludeFromJacocoGeneratedReport
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
