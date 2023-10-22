package kozlov.kirill.graph;

import org.junit.jupiter.api.Test;

public class ListGraphTest extends GraphTest {
    @Test
    void testGraph() {
        super.testGraph(new ListGraph<String>());
    }
}
