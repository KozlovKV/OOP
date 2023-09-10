package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for sorter.
 */
public class MainTest {
    @Test
    void callMain() {
        Main.main(new String[] {});
        assertTrue(true);
    }

    @Test
    void simpleSort() {
        HeapSorter hs = new HeapSorter();
        hs.heapsort(new int[] {4, 8, 1, -5, 6});
        assertArrayEquals(
                new int[] {-5, 1, 4, 6, 8},
                hs.getArr()
        );
    }

    @Test
    void voidTest() {
        HeapSorter hs = new HeapSorter();
        hs.heapsort(new int[] {});
        assertArrayEquals(new int[] {}, hs.getArr());
    }

    @Test
    void intMaxMin() {
        HeapSorter hs = new HeapSorter();
        hs.heapsort(new int[] {2147483647, -2147483648});
        assertArrayEquals(new int[] {-2147483648, 2147483647}, hs.getArr());
    }

    @Test
    void largeTest() {
        HeapSorter hs = new HeapSorter();
        hs.heapsort(new int[] {1287215632, -141299706, -1008088036, 116536236,
                -1589796938, -1660231475, -1032147032, -1073373868, 67869047,
                657504910});
        assertArrayEquals(new int[] {-1660231475, -1589796938, -1073373868,
                -1032147032, -1008088036, -141299706, 67869047, 116536236,
                657504910, 1287215632}, hs.getArr());
    }
}
