package kozlov.kirill.tree;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Objects;

/**
 * Tree class.
 *
 * @param <T> type of nodes
 */
public class Tree<T> {
    final T node;
    Tree<T> parent = null;
    final ArrayList<Tree<T>> children = new ArrayList<>();
    private static final String staples = "()[]{}[]";
    private boolean isChangesBlocked = false;

    public Tree(T node) {
        this.node = node;
    }

    /**
     * Add child method with block flag checking.
     *
     * @param child child with type Tree
     * @return added child
     */
    public Tree<T> addChild(Tree<T> child) {
        testBlocking();
        child.parent = this;
        this.children.add(child);
        return child;
    }

    /**
     * Add child method with block flag checking.
     *
     * @param node child of type T
     * @return added child
     */
    public Tree<T> addChild(T node) {
        testBlocking();
        Tree<T> child = new Tree<>(node);
        return this.addChild(child);
    }

    /**
     * Removes vertex from tree with block flag checking.
     */
    public void remove() {
        testBlocking();
        forceRemove();
    }

    /**
     * Removes vertex from tree without block flag checking.
     * Removes vertex from parent's children list and clear its own children list,
     * doesn't remove node's value.
     * (children vertices can be accessed from other sources if they was saved)
     */
    void forceRemove() {
        children.clear();
        if (parent != null) {
            parent.children.remove(this);
        }
    }

    /**
     * Overrided equals method.
     * We assume that 2 vertices are equal when their nodes and their children
     * (order is ignored) are equal.
     * Thus equals method work recursively from root to leafs like DFS
     *
     * @param obj object for comparing
     * @return result of comparing
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tree<?>)) {
            return false;
        }
        Tree<T> otherTree = (Tree<T>) obj;
        if (!otherTree.node.equals(this.node)) {
            return false;
        }
        return otherTree.children.size() == this.children.size()
                || otherTree.children.stream().sorted().equals(this.children.stream().sorted());
    }

    /**
     * Overrided hashcode method.
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.node);
    }

    /**
     * Overrided toString method.
     *
     * @return node string
     */
    @Override
    public String toString() {
        return this.node.toString();
    }

    /**
     * Returns full tree as a string.
     *
     * @return Full tree as a string of format "node [ children ]"
     */
    public String getStringTree(int staplesIndex) {
        StringBuilder result = new StringBuilder();
        result.append(node.toString());
        if (!this.children.isEmpty()) {
            result.append(String.format(" %c ", staples.charAt(staplesIndex)));
            for (var childStr : this.children.stream().map(child -> child.getStringTree(
                    (staplesIndex + 2) % staples.length()
            )).toArray()) {
                result.append(String.format("%s, ", childStr));
            }
            result.delete(result.length() - 2, result.length());
            result.append(String.format(" %c", staples.charAt(staplesIndex + 1)));
        }
        return result.toString();
    }

    /**
     * Checks is it possible to change vertex.
     * We can change vertex if there are no blocked vertices in their ancestors.
     *
     * @throws ConcurrentModificationException if any ancestor is blocked.
     */
    private void testBlocking() throws ConcurrentModificationException {
        var vertex = this;
        while (vertex != null) {
            if (vertex.isChangesBlocked) {
                throw new ConcurrentModificationException("Vertex is below iteration root");
            }
            vertex = vertex.parent;
        }
    }

    /**
     * Blocks vertex-tree for changing.
     */
    void block() {
        isChangesBlocked = true;
    }

    /**
     * Unblocks vertex-tree for changing.
     */
    void unblock() {
        isChangesBlocked = false;
    }

    /**
     * Entry point.
     *
     * @param args cmd args.
     */
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        Tree<Integer> root = new Tree<>(4);
        var one = root.addChild(1);
        var three = root.addChild(3);
        var eight = three.addChild(8);
        three.addChild(9);
        var five = root.addChild(5);

        var crawlList = new ArrayList<Integer>();
        for (var value : new DfsTreeCollection<>(three)) {
            if (three.node.equals(value)) {
                one.remove();
            }
            crawlList.add(value);
        }
        System.out.println(crawlList);
    }
}
