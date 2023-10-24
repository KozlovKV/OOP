package kozlov.kirill.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

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

    static class testArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new ListGraph<String>()),
                    Arguments.of(new IncidentMatrixGraph<String>()),
                    Arguments.of(new AdjacencyMatrixGraph<String>())
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testGraph(AbstractGraph<String> graph) {
        Main.readDataForGraphFromFile(graph, "./input.txt");
        var result = graph.constructShortestDistances("C");
        Assertions.assertEquals(result, getPredictedMap());
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testNegative(AbstractGraph<String> graph) {
        graph.changeDirectionType(true);
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 5);
        graph.addEdge("B", "C", -5);
        HashMap<String, Double> predicted = new HashMap<>();
        predicted.put("A", 0.0);
        predicted.put("B", 4.0);
        predicted.put("C", -1.0);
        var result = graph.constructShortestDistances("A");
        Assertions.assertEquals(predicted, result);
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testNegativeCycle(AbstractGraph<String> graph) {
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 5);
        graph.addEdge("B", "C", -5);
        var result = graph.constructShortestDistances("A");
        Assertions.assertNull(result);
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testNonReachableVertex(AbstractGraph<String> graph) {
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 5);
        graph.addEdge("B", "C", 5);
        graph.addVertex("N");
        HashMap<String, Double> predicted = new HashMap<>();
        predicted.put("A", 0.0);
        predicted.put("B", 4.0);
        predicted.put("C", 5.0);
        predicted.put("N", Double.MAX_VALUE);
        var result = graph.constructShortestDistances("A");
        Assertions.assertEquals(predicted, result);
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testNonReachableVertexWithNegative(AbstractGraph<String> graph) {
        graph.changeDirectionType(true);
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 5);
        graph.addEdge("B", "C", -5);
        graph.addVertex("N");
        HashMap<String, Double> predicted = new HashMap<>();
        predicted.put("A", 0.0);
        predicted.put("B", 4.0);
        predicted.put("C", -1.0);
        predicted.put("N", Double.MAX_VALUE);
        var result = graph.constructShortestDistances("A");
        Assertions.assertEquals(predicted, result);
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testDifferentStartPoints(AbstractGraph<String> graph) {
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 5);
        graph.addEdge("B", "C", 5);

        HashMap<String, Double> predicted1 = new HashMap<>();
        predicted1.put("A", 0.0);
        predicted1.put("B", 4.0);
        predicted1.put("C", 5.0);
        var result = graph.constructShortestDistances("A");
        Assertions.assertEquals(predicted1, result);

        HashMap<String, Double> predicted2 = new HashMap<>();
        predicted2.put("A", 4.0);
        predicted2.put("B", 0.0);
        predicted2.put("C", 5.0);
        var result2 = graph.constructShortestDistances("B");
        Assertions.assertEquals(predicted2, result2);
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testDifferentStartPointsWithNegative(AbstractGraph<String> graph) {
        graph.changeDirectionType(true);
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 5);
        graph.addEdge("B", "C", -5);

        HashMap<String, Double> predicted1 = new HashMap<>();
        predicted1.put("A", 0.0);
        predicted1.put("B", 4.0);
        predicted1.put("C", -1.0);
        var result = graph.constructShortestDistances("A");
        Assertions.assertEquals(predicted1, result);

        HashMap<String, Double> predicted2 = new HashMap<>();
        predicted2.put("A", Double.MAX_VALUE);
        predicted2.put("B", 0.0);
        predicted2.put("C", -5.0);
        var result2 = graph.constructShortestDistances("B");
        Assertions.assertEquals(predicted2, result2);
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testVertexDeletion(AbstractGraph<String> graph) {
        graph.addEdge("C", "A", 1);
        graph.addEdge("C", "B", 1);
        graph.addEdge("C", "D", 1);

        HashMap<String, Double> predicted1 = new HashMap<>();
        predicted1.put("C", 0.0);
        predicted1.put("A", 1.0);
        predicted1.put("B", 1.0);
        predicted1.put("D", 1.0);
        var result = graph.constructShortestDistances("C");
        Assertions.assertEquals(predicted1, result);

        graph.removeVertex("C");
        var edges = graph.getEdgeSet();
        Assertions.assertTrue(edges.isEmpty());
        var result2 = graph.constructShortestDistances("C");
        Assertions.assertNull(result2);

        HashMap<String, Double> predicted3 = new HashMap<>();
        predicted3.put("A", 0.0);
        predicted3.put("B", Double.MAX_VALUE);
        predicted3.put("D", Double.MAX_VALUE);
        var result3 = graph.constructShortestDistances("A");
        Assertions.assertEquals(predicted3, result3);
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testEdgeDeletionUndirected(AbstractGraph<String> graph) {
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 1);
        graph.addEdge("B", "D", 1);
        graph.addEdge("D", "E", 1);

        HashMap<String, Double> predicted1 = new HashMap<>();
        predicted1.put("C", 1.0);
        predicted1.put("A", 1.0);
        predicted1.put("B", 0.0);
        predicted1.put("D", 1.0);
        predicted1.put("E", 2.0);
        var result = graph.constructShortestDistances("B");
        Assertions.assertEquals(predicted1, result);

        HashMap<String, Double> predicted2 = new HashMap<>();
        predicted2.put("C", 1.0);
        predicted2.put("A", 1.0);
        predicted2.put("B", 0.0);
        predicted2.put("D", Double.MAX_VALUE);
        predicted2.put("E", Double.MAX_VALUE);
        graph.removeEdge("B", "D");
        var result2 = graph.constructShortestDistances("B");
        Assertions.assertEquals(predicted2, result2);
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testEdgeDeletionDirected(AbstractGraph<String> graph) {
        graph.changeDirectionType(true);
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 1);
        graph.addEdge("B", "D", 1);
        graph.addEdge("D", "E", 1);

        HashMap<String, Double> predicted1 = new HashMap<>();
        predicted1.put("C", 1.0);
        predicted1.put("A", Double.MAX_VALUE);
        predicted1.put("B", 0.0);
        predicted1.put("D", 1.0);
        predicted1.put("E", 2.0);
        var result = graph.constructShortestDistances("B");
        Assertions.assertEquals(predicted1, result);

        graph.removeEdge("D", "B");
        var result2 = graph.constructShortestDistances("B");
        Assertions.assertEquals(predicted1, result2);

        HashMap<String, Double> predicted3 = new HashMap<>();
        predicted3.put("C", 1.0);
        predicted3.put("A", Double.MAX_VALUE);
        predicted3.put("B", 0.0);
        predicted3.put("D", Double.MAX_VALUE);
        predicted3.put("E", Double.MAX_VALUE);

        graph.removeEdge("B", "D");
        var result3 = graph.constructShortestDistances("B");
        Assertions.assertEquals(predicted3, result3);
    }

    @ParameterizedTest
    @ArgumentsSource(testArgumentsProvider.class)
    void testEdgeWeightChanging(AbstractGraph<String> graph) {
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 1);
        graph.addEdge("B", "D", 1);
        graph.addEdge("D", "E", 1);

        HashMap<String, Double> predicted1 = new HashMap<>();
        predicted1.put("C", 1.0);
        predicted1.put("A", 1.0);
        predicted1.put("B", 0.0);
        predicted1.put("D", 1.0);
        predicted1.put("E", 2.0);
        var result = graph.constructShortestDistances("B");
        Assertions.assertEquals(predicted1, result);

        HashMap<String, Double> predicted2 = new HashMap<>();
        predicted2.put("C", 1.0);
        predicted2.put("A", 1.0);
        predicted2.put("B", 0.0);
        predicted2.put("D", -1.0);
        predicted2.put("E", 0.0);
        graph.getEdge("B", "D").setWeight(-1);
        var result2 = graph.constructShortestDistances("B");
        Assertions.assertEquals(predicted2, result2);
    }
}
