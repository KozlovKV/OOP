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
    protected Map<T, Vertex<T>> vertexMap = new HashMap<>();
    protected boolean directed = false;

    /**
     * Constructor for directed version.
     *
     * @param directed flag for setting direction type
     */
    public AbstractGraph(boolean directed) {
        this.directed = directed;
    }

    /**
     * Directed flag setter.
     * We can change direction type for graph without edges.
     *
     * @param newOrientation new value for `directed` field
     */
    public void changeDirectionType(boolean newOrientation) {
        if (getEdgeSet().isEmpty()) {
            directed = newOrientation;
        }
    }

    /**
     * Vertex getter.
     *
     * @param value value of vertex
     * @return `Vertex` object if it exists otherwise `null`
     */
    public Vertex<T> getVertex(T value) {
        return vertexMap.getOrDefault(value, null);
    }

    /**
     * Vertex creation.
     * Add vertex with specified value if vertex with it doesn't exist.
     *
     * @param value value of vertex
     * @return created or got vertex
     */
    public Vertex<T> addVertex(T value) {
        if (vertexMap.containsKey(value)) {
            return vertexMap.get(value);
        }
        var newVertex = new Vertex<>(value);
        vertexMap.putIfAbsent(value, newVertex);
        return newVertex;
    }

    /**
     * Vertex removing.
     * Removes vertex and all incident edges if vertex exists.
     * Edges deletion MUST BE SPECIFIED in child-classes!
     *
     * @param value value of vertex
     * @return deleted vertex or null if it doesn't exist
     */
    public Vertex<T> removeVertex(T value) {
        if (!vertexMap.containsKey(value)) {
            return null;
        }
        var vertex = vertexMap.get(value);
        vertexMap.remove(value);
        return vertex;
    }

    /**
     * Edge creation.
     * Creates edge (a,b) with specified weight if edge (a,b) doesn't exist.
     * Creates vertices `a` and `b` if they don't exist
     * If `directed == false` also creates edge (b,a) with specified weight
     *
     * @param a first vertex
     * @param b second vertex
     * @param weight double value
     * @return `true` if edge was created
     */
    public abstract boolean addEdge(T a, T b, double weight);

    /**
     * Edge removing.
     * Removes edge (a,b) if it exists and (b,a) if graph is undirected and this edge exists.
     *
     * @param a first vertex value
     * @param b second vertex value
     * @return `true` if all deletions were successful
     */
    public abstract boolean removeEdge(T a, T b);

    /**
     * Edge getter
     *
     * @param a first vertex value
     * @param b second vertex value
     * @return edge (a, b) if it exists and `null` otherwise
     */
    public abstract Edge<T> getEdge(T a, T b);

    /**
     * Getter for edges from specified vertex.
     *
     * @param vertex object
     * @return list of edges with `vertex`'s value on the first position
     */
    public abstract List<Edge<T>> getEdgesFromVertex(Vertex<T> vertex);

    /**
     * Getter for edges to specified vertex.
     *
     * @param vertex object
     * @return list of edges with `vertex`'s value on the second position
     */

    public abstract List<Edge<T>> getEdgesToVertex(Vertex<T> vertex);

    /**
     * Edges set getter.
     *
     * @return set of graph's edges
     */
    public Set<Edge<T>> getEdgeSet() {
        var edgesSet = new HashSet<Edge<T>>();
        for (var vertex : vertexMap.values()) {
            edgesSet.addAll(getEdgesFromVertex(vertex));
            edgesSet.addAll(getEdgesToVertex(vertex));
        }
        return edgesSet;
    }

    /**
     * Method-flag for negative edges.
     *
     * @return `true` if there is at least one edge with negative weight
     */
    public boolean hasNegativeEdges() {
        var edgesSet = getEdgeSet();
        return edgesSet.stream().anyMatch(edge -> edge.getWeight() < 0);
    }

    /**
     * Shortest paths finder.
     * Calls dijkstra algorithm for non-negative graphs and bellman-ford otherwise
     *
     * @param start value of start vertex
     * @return map with vertices and path lengths or `null` if negative cycle was detected
     */
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

    /**
     * Edge relaxation.
     *
     * @param edge edge for relaxing
     * @return `true` if edge was relaxed so that second vertex distance was reduced
     */
    protected boolean relaxEdge(Edge<T> edge) {
        var fromVertex = getVertex(edge.getFrom());
        var toVertex = getVertex(edge.getTo());
        if (toVertex.getDistance() > fromVertex.getDistance() + edge.getWeight()) {
            toVertex.setDistance(fromVertex.getDistance() + edge.getWeight());
            return true;
        }
        return false;
    }

    /**
     * Dijkstra pathfinder.
     *
     * @return map with vertices and path lengths
     */
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

    /**
     * Bellman-ford pathfinder.
     *
     * @return map with vertices and path lengths or `null` if negative cycle was detected
     */
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
