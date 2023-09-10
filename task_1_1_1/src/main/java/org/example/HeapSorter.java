package org.example;

/**
 * Main sorter class
 */
public class HeapSorter {
    private int[] arr;
    private int heapSize;

    private void swap(int a, int b) {
        int tmp = this.arr[a];
        this.arr[a] = this.arr[b];
        this.arr[b] = tmp;
    }

    private void siftDown(int i) {
        int index = i;
        int left = i * 2 + 1;
        int swapCandidateIndex;
        while (left < this.heapSize) {
            swapCandidateIndex = index;
            if (this.arr[left] > this.arr[index]) {
                swapCandidateIndex = left;
            }
            if (left + 1 < this.heapSize &&
                    this.arr[left + 1] > this.arr[swapCandidateIndex]) {
                swapCandidateIndex = left + 1;
            }
            if (swapCandidateIndex != index) {
                swap(swapCandidateIndex, index);
                index = swapCandidateIndex;
                left = index * 2 + 1;
            }
            else {
                break;
            }
        }
    }

    private void heapify() {
        for (int i = this.arr.length / 2 - 1; i >= 0; --i) {
            this.siftDown(i);
        }
    }

    private void constructSortedArr() {
        for (int i = 0; i < this.arr.length - 1; ++i) {
            swap(0, this.heapSize - 1);
            this.heapSize--;
            siftDown(0);
        }
    }

    public void heapsort(int[] arr) {
        this.arr = arr.clone();
        this.heapSize = this.arr.length;
        this.heapify();
        this.constructSortedArr();
    }

    public int[] getArr() {
        return this.arr;
    }
}
