package org.example;

public class Main {
    public static void main(String[] args) {
        HeapSorter hs = new HeapSorter();
        hs.heapsort(new int[] {4, 8, 1, -5, 6});
        for (int elem : hs.getArr()) {
            System.out.printf("%d ", elem);
        }
    }
}