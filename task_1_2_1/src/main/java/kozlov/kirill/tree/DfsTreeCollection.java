package kozlov.kirill.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Collection of tree with DFS iteration.
 *
 * @param <T> nodes' values
 */
public class DfsTreeCollection<T> implements Iterable<T> {
    private final Tree<T> root;

    /**
     * Collection constructor by root. We cannot work with vertices upper its.
     *
     * @param root root of iteration
     */
    public DfsTreeCollection(Tree<T> root) {
        this.root = root;
    }

    /**
     * DFS iterator.
     *
     * @return iterator DFSTreeIterator
     */
    @Override
    public Iterator<T> iterator() {
        return new DfsTreeIterator<>(root);
    }

    /**
     * DFS iterator class.
     *
     * @param <E> must be the same as Tree T
     */
    public static class DfsTreeIterator<E> implements Iterator<E> {
        private final Tree<E> dfsRoot;
        private boolean isFirst = true;
        private Tree<E> currentVertex;
        private final HashMap<Tree<E>, Integer> vertexChildIndexMap = new HashMap<>();

        /**
         * DfsTreeIterator construct.
         *
         * @param currentVertex root vertex
         */
        public DfsTreeIterator(Tree<E> currentVertex) {
            this.currentVertex = currentVertex;
            dfsRoot = currentVertex;
            dfsRoot.block();
            vertexChildIndexMap.put(this.currentVertex, 0);
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
            while (!dfsRoot.equals(currentVertex)) {
                if (vertexChildIndexMap.get(currentVertex) < currentVertex.children.size()) {
                    return true;
                }
                currentVertex = currentVertex.parent;
            }
            if (!(vertexChildIndexMap.get(currentVertex) < currentVertex.children.size())) {
                dfsRoot.unblock();
                return false;
            }
            return true;
        }

        /**
         * Overrided next method.
         *
         * @return next element in DFS
         */
        @Override
        public E next() throws NoSuchElementException {
            if (isFirst) {
                isFirst = false;
                return currentVertex.node;
            }
            if (hasNext()) {
                int childIndex = vertexChildIndexMap.get(currentVertex);
                vertexChildIndexMap.replace(currentVertex, childIndex + 1);
                currentVertex = currentVertex.children.get(childIndex);
                vertexChildIndexMap.put(currentVertex, 0);
                return currentVertex.node;
            }
            throw new NoSuchElementException("There is no unvisited elements in the tree.");
        }

        /**
         * Removes vertex from collection.
         * Changes hashmap relating to changed parent's children' list
         */
        @Override
        public void remove() {
            boolean isDfsRoot = dfsRoot.equals(currentVertex);
            currentVertex.forceRemove();
            var parent = currentVertex.parent;
            if (parent == null || isDfsRoot) {
                return;
            }
            int parentChildIndex = vertexChildIndexMap.get(parent);
            vertexChildIndexMap.replace(parent, parentChildIndex - 1);
        }
    }
}
