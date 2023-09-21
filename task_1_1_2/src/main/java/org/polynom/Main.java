package org.polynom;

/**
 * Main class.
 */
public class Main {
    /**
     * Main entry static point.
     *
     * @param args Cmd args
     */
    public static void main(String[] args) {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{});
        p1.mlt(p2);
    }
}