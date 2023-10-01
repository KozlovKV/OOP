package kozlov.kirill.tree;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for Polynomial.
 */
public class TreeTest {

    @Test
    void treeConstructingTestWithDfsIteration() {
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
    void treeConstructingTestWithBfsIteration() {
        Tree<String> tree = new Tree<>("R1");
        var a = tree.addChild("A");
        a.addChild("B");
        Tree<String> subtree = new Tree<>("R2");
        subtree.addChild("C");
        subtree.addChild("D");
        tree.addChild(subtree);

        ArrayList<String> crawlList = new ArrayList<>();
        var bfsIterator = new Tree.BfsTreeIterator<>(tree);
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

    @Test
    void testEqualsTrue() {
        Tree<Integer> root = new Tree<>(4);
        root.addChild(1);
        root.addChild(5);
        Tree<Integer> root2 = new Tree<>(4);
        root2.addChild(5);
        root2.addChild(1);
        Assertions.assertEquals(root, root2);
    }

    @Test
    void testEqualsWithSubtree() {
        Tree<Integer> root = new Tree<>(4);
        var subtree = root.addChild(4);
        root.addChild(5);
        subtree.addChild(4);
        subtree.addChild(5);
        Assertions.assertEquals(root, subtree);
    }

    @Test
    void testToString() {
        Tree<Integer> integerTree = new Tree<>(4);
        Tree<String> stringTree = new Tree<>("test");
        Assertions.assertTrue(
                integerTree.toString().equals("4") && stringTree.toString().equals("test")
        );
    }
}