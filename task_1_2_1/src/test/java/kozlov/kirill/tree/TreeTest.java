package kozlov.kirill.tree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Polynomial.
 */
public class TreeTest {

    @Test
    void treeConstructingTestWithDFSIteration() {
        Tree<String> tree = new Tree<>("R1");
        var a = tree.addChild("A");
        a.addChild("B");
        Tree<String> subtree = new Tree<>("R2");
        subtree.addChild("C");
        subtree.addChild("D");
        tree.addChild(subtree);

        ArrayList<String> crawlList = new ArrayList<>();
        for (var vertex : tree) {
            crawlList.add(vertex.getNode());
        }
        Assertions.assertEquals(crawlList, List.of(new String[]{"R1", "A", "B", "R2", "C", "D"}));
    }

    @Test
    void treeConstructingTestWithBFSIteration() {
        Tree<String> tree = new Tree<>("R1");
        var a = tree.addChild("A");
        a.addChild("B");
        Tree<String> subtree = new Tree<>("R2");
        subtree.addChild("C");
        subtree.addChild("D");
        tree.addChild(subtree);

        ArrayList<String> crawlList = new ArrayList<>();
        var bfsIterator = new Tree.BFSTreeIterator<>(tree);
        while (bfsIterator.hasNext()) {
            var vertex = bfsIterator.next();
            crawlList.add(vertex.getNode());
        }
        Assertions.assertEquals(crawlList, List.of(new String[]{"R1", "A", "R2", "B", "C", "D"}));
    }

    @Test
    void testRemoving() {
        Tree<Integer> root = new Tree<>(4);
        var oneVertex = root.addChild(1);
        oneVertex.addChild(0);
        root.addChild(5);
        oneVertex.remove();

        ArrayList<Integer> crawlList = new ArrayList<>();
        for (var vertex : root) {
            crawlList.add(vertex.getNode());
        }
        Assertions.assertEquals(crawlList, List.of(new Integer[]{4, 5}));
    }
}