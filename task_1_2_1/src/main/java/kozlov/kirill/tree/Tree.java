package kozlov.kirill.tree;

import java.util.*;

/**
 * Tree class.
 *
 * @param <T> type of nodes
 */
public class Tree<T> implements Iterable<Tree<T>> {
    private final T node;
    private Tree<T> parent = null;
    private final ArrayList<Tree<T>> children = new ArrayList<>();
    private static final String staples = "()[]{}[]";

    public Tree(T node) {
        this.node = node;
    }

    /**
     * Add child method.
     *
     * @param child child with type Tree
     * @return added child
     */
    public Tree<T> addChild(Tree<T> child) {
        child.parent = this;
        this.children.add(child);
        return child;
    }

    /**
     * Add child method.
     *
     * @param node child of type T
     * @return added child
     */
    public Tree<T> addChild(T node) {
        Tree<T> child = new Tree<>(node);
        return this.addChild(child);
    }

    /**
     * Removes vertex from tree.
     * Removes vertex from parent's children list and clear its own children list,
     * doesn't remove node's value.
     * (children vertices can be accessed from other sources if they was saved)
     */
    public void remove() {
        this.children.clear();
        if (this.parent != null) {
            this.parent.children.remove(this);
        }
    }

    /**
     * DFS iterator.
     *
     * @return iterator DFSTreeIterator
     */
    @Override
    public Iterator<Tree<T>> iterator() {
        return new DfsTreeIterator<>(this);
    }

    /**
     * DFS iterator class.
     *
     * @param <E> must be the same as Tree T
     */
    public static class DfsTreeIterator<E> implements Iterator<Tree<E>> {
        private Tree<E> currentVertex;
        private final HashMap<Tree<E>, Integer> vertexChildIndexMap;
        boolean isFirst = true;

        /**
         * DfsTreeIterator construct.
         *
         * @param currentVertex root vertex
         */
        public DfsTreeIterator(Tree<E> currentVertex) {
            this.currentVertex = new Tree<>(currentVertex.node);
            this.currentVertex.addChild(currentVertex);
            vertexChildIndexMap = new HashMap<>();
            vertexChildIndexMap.put(this.currentVertex, 0);
        }

        /**
         * Overrided hasNext method.
         *
         * @return true when next DFS element exists
         */
        @Override
        public boolean hasNext() {
            while (currentVertex.parent != null) {
                if (vertexChildIndexMap.get(currentVertex) < currentVertex.children.size()) {
                    return true;
                }
                currentVertex = currentVertex.parent;
            }
            return vertexChildIndexMap.get(currentVertex) < currentVertex.children.size();
        }

        /**
         * Overrided next method.
         *
         * @return next element in DFS
         */
        @Override
        public Tree<E> next() throws NoSuchElementException {
            while (true) {
                int childIndex = vertexChildIndexMap.get(currentVertex);
                if (childIndex < currentVertex.children.size()) {
                    vertexChildIndexMap.replace(currentVertex, childIndex + 1);
                    currentVertex = currentVertex.children.get(childIndex);
                    vertexChildIndexMap.put(currentVertex, 0);
                    return currentVertex;
                }
                if (currentVertex.parent == null) {
                    break;
                }
                currentVertex = currentVertex.parent;
            }
            throw new NoSuchElementException("There is no unvisited elements in the tree.");
        }

        /**
         * Removes vertex from collection.
         * Changes hashmap relating to changed parent's children' list
         */
        @Override
        public void remove() {
            currentVertex.remove();
            var parent = currentVertex.parent;
            if (parent == null) {
                return;
            }
            int parentChildIndex = vertexChildIndexMap.get(parent);
            vertexChildIndexMap.replace(parent, parentChildIndex - 1);
        }
    }

    /**
     * BFS iterator class.
     *
     * @param <E> must be the same as Tree T
     */
    public static class BfsTreeIterator<E> implements Iterator<Tree<E>> {
        private Tree<E> currentVertex;
        private final ArrayDeque<Tree<E>> deque;
        private final boolean isFirst = true;

        /**
         * BfsTreeIterator construct.
         *
         * @param currentVertex root vertex
         */
        public BfsTreeIterator(Tree<E> currentVertex) {
            this.currentVertex = new Tree<>(currentVertex.node);
            this.currentVertex.addChild(currentVertex);
            deque = new ArrayDeque<>();
        }

        /**
         * Overrided hasNext method.
         *
         * @return true when next BFS element exists
         */
        @Override
        public boolean hasNext() {
            return !(deque.isEmpty() && currentVertex.children.isEmpty());
        }

        /**
         * Overrided next method.
         *
         * @return next element in BFS
         */
        @Override
        public Tree<E> next() throws NoSuchElementException {
            deque.addAll(currentVertex.children);
            if (deque.isEmpty()) {
                throw new NoSuchElementException("There is no unvisited elements in the tree.");
            }
            currentVertex = deque.pollFirst();
            return currentVertex;
        }


        /**
         * Removes vertex from collection.
         */
        @Override
        public void remove() {
            currentVertex.remove();
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
     * @return Full tree as a string of format "node [children]"
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
            result.delete(result.length()-2, result.length());
            result.append(String.format(" %c", staples.charAt(staplesIndex + 1)));
        }
        return result.toString();
    }

    /**
     * Node getter.
     *
     * @return node
     */
    public T getNode() {
        return node;
    }

    /**
     * Entry point.
     *
     * @param args cmd args.
     */
    public static void main(String[] args) {
        Tree<String> tree = new Tree<>("R1");
        var a = tree.addChild("A");
        var b = a.addChild("B");
        Tree<String> subtree = new Tree<>("R2");
        subtree.addChild("C");
        subtree.addChild("D");
        tree.addChild(subtree);
        tree.addChild("AAA");
        b.addChild("CCC").addChild("DDD").addChild("EEE");
        System.out.println(tree.getStringTree(0));
    }
}
