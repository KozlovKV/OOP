package org.heapsort;

/**
 * Main sorter class.
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
        int leftChildIndex = i * 2 + 1;
        int rightChildIndex = i * 2 + 2;
        int swapCandidateIndex;
        while (leftChildIndex < this.heapSize) {
            swapCandidateIndex = index;
            if (this.arr[leftChildIndex] > this.arr[index]) {
                swapCandidateIndex = leftChildIndex;
            }
            if (rightChildIndex < this.heapSize
                    && this.arr[rightChildIndex] > this.arr[swapCandidateIndex]) {
                swapCandidateIndex = leftChildIndex + 1;
            }
            if (swapCandidateIndex != index) {
                swap(swapCandidateIndex, index);
                index = swapCandidateIndex;
                leftChildIndex = index * 2 + 1;
                rightChildIndex = index * 2 + 2;
            } else {
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

    /**
     * Main sorter's method.
     *
     * @param arr Unsorted ints' array.
     *
     * @return sorted arr.
     */
    public int[] heapsort(int[] arr) {
        this.arr = arr.clone();
        this.heapSize = this.arr.length;
        this.heapify();
        this.constructSortedArr();
        return this.arr;
    }

    public int[] getArr() {
        return this.arr;
    }
}
