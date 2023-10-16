package kozlov.kirill.tree;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Collection of tree with BFS iteration.
 *
 * @param <T> nodes' values
 */
public class BfsTreeCollection<T> implements Iterable<T> {
    private final Tree<T> root;

    /**
     * Collection constructor by root. We cannot work with vertices upper its.
     *
     * @param root root of iteration
     */
    public BfsTreeCollection(Tree<T> root) {
        this.root = root;
    }

    /**
     * DFS iterator.
     *
     * @return iterator DFSTreeIterator
     */
    @Override
    public Iterator<T> iterator() {
        return new BfsTreeIterator<>(root);
    }

    /**
     * BFS iterator class.
     *
     * @param <E> must be the same as Tree T
     */
    public static class BfsTreeIterator<E> implements Iterator<E> {
        private final Tree<E> bfsRoot;
        private boolean isFirst = true;
        private Tree<E> currentVertex;
        private final ArrayDeque<Tree<E>> deque;

        /**
         * BfsTreeIterator construct.
         *
         * @param currentVertex root vertex
         */
        public BfsTreeIterator(Tree<E> currentVertex) {
            this.currentVertex = currentVertex;
            bfsRoot = currentVertex;
            bfsRoot.block();
            deque = new ArrayDeque<>();
        }

        /**
         * Overrided hasNext method.
         *
         * @return true when next BFS element exists
         */
        @Override
        public boolean hasNext() {
            if (isFirst || !(deque.isEmpty() && currentVertex.children.isEmpty())) {
                return true;
            }
            bfsRoot.unblock();
            return false;
        }

        /**
         * Overrided next method.
         *
         * @return next element in BFS
         */
        @Override
        public E next() throws NoSuchElementException {
            if (isFirst) {
                isFirst = false;
                return currentVertex.node;
            }
            deque.addAll(currentVertex.children);
            if (deque.isEmpty()) {
                bfsRoot.unblock();
                throw new NoSuchElementException("There is no unvisited elements in the tree.");
            }
            currentVertex = deque.pollFirst();
            return currentVertex.node;
        }


        /**
         * Removes vertex from collection.
         */
        @Override
        public void remove() {
            currentVertex.forceRemove();
        }
    }
}
