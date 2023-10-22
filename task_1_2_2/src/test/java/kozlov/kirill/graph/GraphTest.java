package kozlov.kirill.graph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Graph test class.
 */
public class GraphTest {
    private Map<String, Double> getPredictedMap() {
        var predicted = new HashMap<String, Double>();
        predicted.put("C", 0.0);
        predicted.put("D", 2.0);
        predicted.put("E", 4.0);
        predicted.put("F", 5.0);
        predicted.put("G", 9.0);
        predicted.put("B", 10.0);
        predicted.put("A", 14.0);
        return predicted;
    }

    @Test
    void testListGraph() {
        var graph = new ListGraph<String>();
        Main.readDataForGraphFromFile(graph, "./input.txt");
        var result = graph.constructShortestDistances("C");
        Assertions.assertEquals(result, getPredictedMap());
    }

    @Test
    void testAdjacencyMatrixGraph() {
        var graph = new AdjacencyMatrixGraph<String>();
        Main.readDataForGraphFromFile(graph, "./input.txt");
        var result = graph.constructShortestDistances("C");
        Assertions.assertEquals(result, getPredictedMap());
    }

    @Test
    void testIncidentMatrixGraph() {
        var graph = new IncidentMatrixGraph<String>();
        Main.readDataForGraphFromFile(graph, "./input.txt");
        var result = graph.constructShortestDistances("C");
        Assertions.assertEquals(result, getPredictedMap());
    }

    // TODO: Добавить тесты для изменения веса вершины, удаления вершины и/или ребра
}
