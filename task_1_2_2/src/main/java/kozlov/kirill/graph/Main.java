package kozlov.kirill.graph;

import java.io.FileReader;
import java.io.IOException;

/**
 * Main class
 */
public class Main {
    /**
     * Method for reading graph's data from file.
     *
     * @param graph graph upcasted to Graph abstract class.
     * @param filename file with data.
     */
    public static void readDataForGraphFromFile(Graph<String> graph, String filename) {
        try (FileReader reader = new FileReader(filename)) {
            char[] buf = new char[65536];
            int len = reader.read(buf);
            var strings = String.copyValueOf(buf).split("[\r]\n");
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
        var graph = new IncidentMatrixGraph<String>();
        readDataForGraphFromFile(graph, "./input.txt");
        var result = graph.constructShortestDistances("C");
        System.out.println(result);
        System.out.println(graph);
    }
}