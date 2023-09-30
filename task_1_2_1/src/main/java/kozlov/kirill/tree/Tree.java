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
    private ArrayList<Tree<T>> children = new ArrayList<>();

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
     * Removes vertex.
     *
     * Removes vertex from parent's children list and clear its own children list
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
        return new DFSTreeIterator<>(this);
    }

    /**
     * DFS iterator class.
     *
     * @param <E> must be the same as Tree T
     */
    public static class DFSTreeIterator<E> implements Iterator<Tree<E>> {

        private Tree<E> currentVertex;
        private HashMap<Tree<E>, Integer> vertexChildIndexMap;
        boolean isFirst = true;

        public DFSTreeIterator(Tree<E> currentVertex) {
            this.currentVertex = currentVertex;
            vertexChildIndexMap = new HashMap<>();
            vertexChildIndexMap.put(currentVertex, 0);
        }

        /**
         * Overrided hasNext method.
         *
         * @return true when next DFS element exists
         */
        @Override
        public boolean hasNext() {
            if (isFirst) {
                return true;
            }
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
            if (isFirst) {
                isFirst = false;
                return this.currentVertex;
            }
            while (true) {
                int childIndex = vertexChildIndexMap.get(currentVertex);
                if (childIndex < currentVertex.children.size()) {
                    vertexChildIndexMap.replace(currentVertex, childIndex+1);
                    currentVertex = currentVertex.children.get(childIndex);
                    vertexChildIndexMap.put(currentVertex, 0);
                    return currentVertex;
                }
                if (currentVertex.parent == null)
                    break;
                currentVertex = currentVertex.parent;
            }
            throw new NoSuchElementException("There is no unvisited elements in the tree.");
        }
    }

    public static class BFSTreeIterator<E> implements Iterator<Tree<E>> {
        private ArrayDeque<Tree<E>> deque;

        public BFSTreeIterator(Tree<E> currentVertex) {
            deque = new ArrayDeque<>();
            deque.add(currentVertex);
        }

        /**
         * Overrided hasNext method.
         *
         * @return true when next BFS element exists
         */
        @Override
        public boolean hasNext() {
            return !deque.isEmpty();
        }

        /**
         * Overrided next method.
         *
         * @return next element in BFS
         */
        @Override
        public Tree<E> next() throws NoSuchElementException {
            if (deque.isEmpty()) {
                throw new NoSuchElementException("There is no unvisited elements in the tree.");
            }
            var node = deque.pollFirst();
            deque.addAll(node.children);
            return node;
        }
    }

    /**
     * Overrided equals method.
     *
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
                || otherTree.children.stream().sorted()
                .toList().equals(this.children.stream().sorted().toList());
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

    // TODO: добавить вывод всего дерева в более-менее приличном виде

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
        Tree<Integer> root = new Tree<>(4);
        root.addChild(1).addChild(0);
        root.addChild(5);
        for (var elem : root) {
            if (elem.node == 1)
                elem.addChild(555);
            System.out.println(elem);
        }
    }
}
