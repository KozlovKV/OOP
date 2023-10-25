package kozlov.kirill.graph;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Main class.
 */
public class Main {
    /**
     * Method for reading graph's data from file.
     *
     * @param graph graph upcasted to Graph abstract class.
     * @param filename file with data.
     */
    public static void readDataForGraphFromFile(AbstractGraph<String> graph, String filename) {
        try (FileReader reader = new FileReader(filename)) {
            char[] buf = new char[65536];
            int len = reader.read(buf);
            var strings = String.copyValueOf(buf).split("\r?\n");
            for (var string : strings) {
                var edgeData = string.split(" ");
                graph.addEdge(edgeData[0], edgeData[1], Double.parseDouble(edgeData[2]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Entry point.
     *
     * @param args cmd's args.
     */
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        var graph = new AdjacencyMatrixGraph<String>(true);
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 1);
        graph.addEdge("B", "D", 1);
        graph.addEdge("D", "E", 1);

        var result = graph.constructShortestDistances("B");
        System.out.println(result);
        graph.removeEdge("B", "D");
        var result2 = graph.constructShortestDistances("B");
        System.out.println(result2);
    }
}