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
        Polynomial p1 = new Polynomial(new int[]{-1, 0, 0});
        System.out.println(p1.differentiate(0).toString());
    }
}