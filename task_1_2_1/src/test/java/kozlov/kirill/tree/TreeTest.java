package kozlov.kirill.tree;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
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
        for (var value : new DfsTreeCollection<>(tree)) {
            crawlList.add(value);
        }
        Assertions.assertEquals(crawlList, List.of(new String[]{"R1", "A", "B", "R2", "C", "D"}));
    }

    @Test
    void testDfsIteratorWithChildRemoving() {
        Tree<Integer> root = new Tree<>(4);
        root.addChild(1).addChild(8);
        root.addChild(5);
        var iterator = new DfsTreeCollection.DfsTreeIterator<Integer>(root);

        ArrayList<Integer> crawlList = new ArrayList<>();
        while (iterator.hasNext()) {
            var elem = iterator.next();
            if (elem == 1) {
                iterator.remove();
            } else {
                crawlList.add(elem);
            }
        }
        Assertions.assertEquals(crawlList, List.of(new Integer[]{4, 5}));
    }

    @Test
    void testDfsIteratorWithRootOutsideRemoving() {
        Tree<Integer> root = new Tree<>(4);
        root.addChild(1).addChild(8);
        root.addChild(5);
        root.remove();
        var iterator = new DfsTreeCollection.DfsTreeIterator<Integer>(root);

        ArrayList<Integer> crawlList = new ArrayList<>();
        while (iterator.hasNext()) {
            crawlList.add(iterator.next());
        }
        Assertions.assertEquals(crawlList, List.of(new Integer[]{4}));
    }

    @Test
    void testDfsIteratorWithRootInsideRemoving() {
        Tree<Integer> root = new Tree<>(4);
        root.addChild(1).addChild(8);
        root.addChild(5);
        var iterator = new DfsTreeCollection.DfsTreeIterator<Integer>(root);

        ArrayList<Integer> crawlList = new ArrayList<>();
        while (iterator.hasNext()) {
            var elem = iterator.next();
            if (elem.equals(root.node)) {
                iterator.remove();
            } else {
                crawlList.add(elem);
            }
        }
        Assertions.assertEquals(crawlList, List.of(new Integer[]{}));
    }

    @Test
    void testDfsIterationConcurrentException() {
        Tree<Integer> root = new Tree<>(4);
        var one = root.addChild(1);
        var three = root.addChild(3);
        var five = root.addChild(5);
        var eight = three.addChild(8);
        three.addChild(9);

        var crawlList = new ArrayList<Integer>();
        for (var value : new DfsTreeCollection<>(root)) {
            if (root.node.equals(value)) {
                Assertions.assertThrows(ConcurrentModificationException.class, three::remove);
            }
            crawlList.add(value);
        }
    }

    @Test
    void testRemovingNotIterableVertexWhileIteratingDfs() {
        Tree<Integer> root = new Tree<>(4);
        var one = root.addChild(1);
        var three = root.addChild(3);
        var five = root.addChild(5);
        var eight = three.addChild(8);
        three.addChild(9);
        eight.addChild(10);

        var crawlList = new ArrayList<Integer>();
        for (var value : new DfsTreeCollection<>(three)) {
            if (three.node.equals(value)) {
                one.remove();
            }
            crawlList.add(value);
        }
        Assertions.assertEquals(crawlList, List.of(new Integer[]{3, 8, 10, 9}));
    }

    @Test
    void testTwoDfsIterationsWithUnblockedDeletionAfterFirst() {
        Tree<Integer> root = new Tree<>(4);
        var one = root.addChild(1);
        var three = root.addChild(3);
        var five = root.addChild(5);
        var eight = three.addChild(8);
        three.addChild(9);
        eight.addChild(10);

        var crawlList = new ArrayList<Integer>();
        new DfsTreeCollection<>(root).forEach(crawlList::add);
        Assertions.assertEquals(crawlList, List.of(new Integer[]{4, 1, 3, 8, 10, 9, 5}));
        root.remove();
        crawlList.clear();
        new DfsTreeCollection<>(three).forEach(crawlList::add);
        Assertions.assertEquals(crawlList, List.of(new Integer[]{3, 8, 10, 9}));
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
        for (var value : new BfsTreeCollection<>(tree)) {
            crawlList.add(value);
        }
        Assertions.assertEquals(crawlList, List.of(new String[]{"R1", "A", "R2", "B", "C", "D"}));
    }

    @Test
    void testBfsIteratorWithChildRemoving() {
        Tree<Integer> root = new Tree<>(4);
        root.addChild(1).addChild(8);
        root.addChild(5);
        var iterator = new BfsTreeCollection.BfsTreeIterator<Integer>(root);

        ArrayList<Integer> crawlList = new ArrayList<>();
        while (iterator.hasNext()) {
            var elem = iterator.next();
            if (elem == 1) {
                iterator.remove();
            } else {
                crawlList.add(elem);
            }
        }
        Assertions.assertEquals(crawlList, List.of(new Integer[]{4, 5}));
    }

    @Test
    void testBfsIteratorWithRootOutsideRemoving() {
        Tree<Integer> root = new Tree<>(4);
        root.addChild(1).addChild(8);
        root.addChild(5);
        root.remove();
        var iterator = new BfsTreeCollection.BfsTreeIterator<Integer>(root);

        ArrayList<Integer> crawlList = new ArrayList<>();
        while (iterator.hasNext()) {
            crawlList.add(iterator.next());
        }
        Assertions.assertEquals(crawlList, List.of(new Integer[]{4}));
    }

    @Test
    void testBfsIteratorWithRottInsideRemoving() {
        Tree<Integer> root = new Tree<>(4);
        root.addChild(1).addChild(8);
        root.addChild(5);
        var iterator = new BfsTreeCollection.BfsTreeIterator<Integer>(root);

        ArrayList<Integer> crawlList = new ArrayList<>();
        while (iterator.hasNext()) {
            var elem = iterator.next();
            if (elem.equals(root.node)) {
                iterator.remove();
            } else {
                crawlList.add(elem);
            }
        }
        Assertions.assertEquals(crawlList, List.of(new Integer[]{}));
    }

    @Test
    void testBfsIterationConcurrentException() {
        Tree<Integer> root = new Tree<>(4);
        var one = root.addChild(1);
        var three = root.addChild(3);
        var five = root.addChild(5);
        var eight = three.addChild(8);
        three.addChild(9);

        var crawlList = new ArrayList<Integer>();
        for (var value : new BfsTreeCollection<>(root)) {
            if (root.node.equals(value)) {
                Assertions.assertThrows(ConcurrentModificationException.class, three::remove);
            }
            crawlList.add(value);
        }
    }

    @Test
    void testRemovingNotIterableVertexWhileIteratingBfs() {
        Tree<Integer> root = new Tree<>(4);
        var one = root.addChild(1);
        var three = root.addChild(3);
        var eight = three.addChild(8);
        eight.addChild(10);
        three.addChild(9);
        var five = root.addChild(5);

        var crawlList = new ArrayList<Integer>();
        for (var value : new DfsTreeCollection<>(three)) {
            if (three.node.equals(value)) {
                one.remove();
            }
            crawlList.add(value);
        }
        Assertions.assertEquals(crawlList, List.of(new Integer[]{3, 8, 10, 9}));
    }

    @Test
    void testTwoBfsIterationsWithUnblockedDeletionAfterFirst() {
        Tree<Integer> root = new Tree<>(4);
        var one = root.addChild(1);
        var three = root.addChild(3);
        var five = root.addChild(5);
        var eight = three.addChild(8);
        three.addChild(9);
        eight.addChild(10);

        var crawlList = new ArrayList<Integer>();
        new BfsTreeCollection<>(root).forEach(crawlList::add);
        Assertions.assertEquals(crawlList, List.of(new Integer[]{4, 1, 3, 5, 8, 9, 10}));
        root.remove();
        crawlList.clear();
        new BfsTreeCollection<>(three).forEach(crawlList::add);
        Assertions.assertEquals(crawlList, List.of(new Integer[]{3, 8, 9, 10}));
    }

    @Test
    void testRemoving() {
        Tree<Integer> root = new Tree<>(4);
        var oneVertex = root.addChild(1);
        oneVertex.addChild(0);
        root.addChild(5);
        oneVertex.remove();

        ArrayList<Integer> crawlList = new ArrayList<>();
        for (var vertex : new DfsTreeCollection<>(root)) {
            crawlList.add(vertex);
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

    @Test
    void testStringFullTree() {
        Tree<String> tree = new Tree<>("R1");
        var a = tree.addChild("A");
        a.addChild("B").addChild("CCC").addChild("DDD").addChild("EEE");
        Tree<String> subtree = new Tree<>("R2");
        subtree.addChild("C");
        subtree.addChild("D");
        tree.addChild(subtree);
        tree.addChild("AAA");
        Assertions.assertEquals(tree.getStringTree(0),
                "R1 ( A [ B { CCC [ DDD ( EEE ) ] } ], R2 [ C, D ], AAA )");
    }
}