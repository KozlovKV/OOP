package org.heapsort;

/**
 * Entry-point class.
 */
public class Main {
    /**
     * Static entry method.
     *
     * @param args Cmd args.
     */
    public static void main(String[] args) {
        HeapSorter hs = new HeapSorter();
        for (int elem : hs.heapsort(new int[] {4, 8, 1, -5, 6})) {
            System.out.printf("%d ", elem);
        }
    }
}