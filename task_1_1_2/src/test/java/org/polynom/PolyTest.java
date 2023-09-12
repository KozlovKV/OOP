package org.polynom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for Polynomial.
 */
public class PolyTest {
    @Test
    void testMainRunning() {
        Main.main(new String[]{});
        Assertions.assertTrue(true);
    }

    @Test
    void simpleSum1() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 1});
        Assertions.assertEquals(
            p1.plus(p2).toString(), "4x^2 + 4x + 4"
        );
    }

    @Test
    void simpleSum2() {
        Polynomial p1 = new Polynomial(new int[]{1, 2});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 1});
        Assertions.assertEquals(
                p1.plus(p2).toString(), "1x^2 + 4x + 4"
        );
    }

    @Test
    void voidSum() {
        Polynomial p1 = new Polynomial(new int[]{});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 1});
        Assertions.assertEquals(
                p1.plus(p2).toString(), "1x^2 + 2x + 3"
        );
    }

    @Test
    void zeroSum() {
        Polynomial p1 = new Polynomial(new int[]{0, 0, 0, 5});
        Polynomial p2 = new Polynomial(new int[]{3, -2, 1});
        Assertions.assertEquals(
                p1.plus(p2).toString(), "5x^3 + 1x^2 - 2x + 3"
        );
    }

    @Test
    void simpleSub1() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 1});
        Assertions.assertEquals(
                p1.minus(p2).toString(), "2x^2 - 2"
        );
    }

    @Test
    void simpleSub2() {
        Polynomial p1 = new Polynomial(new int[]{0, 0, 0, 5});
        Polynomial p2 = new Polynomial(new int[]{3, -2, 1});
        Assertions.assertEquals(
                p1.minus(p2).toString(), "5x^3 - 1x^2 + 2x - 3"
        );
    }

    @Test
    void simpleMlt1() {
        Polynomial p1 = new Polynomial(new int[]{0, 2});
        Polynomial p2 = new Polynomial(new int[]{3, -2, 1});
        Assertions.assertEquals(
                p1.mlt(p2).toString(), "2x^3 - 4x^2 + 6x"
        );
    }

    @Test
    void simpleMlt2() {
        Polynomial p1 = new Polynomial(new int[]{0, 0, 0, 5});
        Polynomial p2 = new Polynomial(new int[]{3, -2, 1});
        Assertions.assertEquals(
                p1.mlt(p2).toString(), "5x^5 - 10x^4 + 15x^3"
        );
    }

    @Test
    void voidMlt1() {
        Polynomial p1 = new Polynomial(new int[]{});
        Polynomial p2 = new Polynomial(new int[]{3, -2, 1});
        Assertions.assertEquals(
                p1.mlt(p2).toString(), "0"
        );
    }

    @Test
    void voidMlt2() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{});
        Assertions.assertEquals(
                p1.mlt(p2).toString(), "0"
        );
    }

    @Test
    void simpleEval1() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Assertions.assertEquals(
                p1.evaluate(5), 5 * 5 * 3 + 2 * 5 + 1
        );
    }

    @Test
    void simpleEval2() {
        Polynomial p1 = new Polynomial(new int[]{0, 0, 0, 5});
        Assertions.assertEquals(
                p1.evaluate(4), 4 * 4 * 4 * 5
        );
    }

    @Test
    void voidEval() {
        Polynomial p1 = new Polynomial(new int[]{});
        Assertions.assertEquals(
                p1.evaluate(5), 0
        );
    }

    @Test
    void simpleDiff() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Assertions.assertEquals(
                p1.differentiate(1).toString(), "6x + 2"
        );
    }

    @Test
    void highPowerDiff1() {
        Polynomial p1 = new Polynomial(new int[]{7, 6, 5, 4, 3, 2, 1});
        Assertions.assertEquals(
                p1.differentiate(5).toString(), "720x + 240"
        );
    }

    @Test
    void highPowerDiff2() {
        Polynomial p1 = new Polynomial(new int[]{7, 6, 5, 4, 3, 2, 1});
        Assertions.assertEquals(
                p1.differentiate(100).toString(), "0"
        );
    }

    @Test
    void nonDiff() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Assertions.assertEquals(
                p1.differentiate(0).toString(), "3x^2 + 2x + 1"
        );
    }

    @Test
    void voidDiff() {
        Polynomial p1 = new Polynomial(new int[]{});
        Assertions.assertEquals(
                p1.differentiate(5).toString(), "0"
        );
    }

    @Test
    void simpleEqual1() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 1});
        Assertions.assertFalse(p1.isEqual(p2));
    }

    @Test
    void simpleEqual2() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{1, 2, 3});
        Assertions.assertTrue(p1.isEqual(p2));
    }

    @Test
    void voidEqual1() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{});
        Assertions.assertFalse(p1.isEqual(p2));
    }

    @Test
    void voidEqual2() {
        Polynomial p1 = new Polynomial(new int[]{});
        Polynomial p2 = new Polynomial(new int[]{});
        Assertions.assertTrue(p1.isEqual(p2));
    }
}
