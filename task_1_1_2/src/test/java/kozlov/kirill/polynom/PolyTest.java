package kozlov.kirill.polynom;

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
    void simpleSum() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 1});
        Assertions.assertEquals(
            p1.plus(p2), new Polynomial(new int[]{4, 4, 4})
        );
    }

    @Test
    void differentLengthSum() {
        Polynomial p1 = new Polynomial(new int[]{1, 2});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 1});
        Assertions.assertEquals(
                p1.plus(p2), new Polynomial(new int[]{4, 4, 1})
        );
    }

    @Test
    void voidSum() {
        Polynomial p1 = new Polynomial(new int[]{});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 1});
        Assertions.assertEquals(p1.plus(p2), p2);
    }

    @Test
    void lowerZerosSum() {
        Polynomial p1 = new Polynomial(new int[]{0, 0, 0, 5});
        Polynomial p2 = new Polynomial(new int[]{3, -2, 1});
        Assertions.assertEquals(
                p1.plus(p2), new Polynomial(new int[]{3, -2, 1, 5})
        );
    }

    @Test
    void simpleSub() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 1});
        Assertions.assertEquals(
                p1.minus(p2), new Polynomial(new int[]{-2, 0, 2})
        );
    }

    @Test
    void lowerZerosSub() {
        Polynomial p1 = new Polynomial(new int[]{0, 0, 0, 5});
        Polynomial p2 = new Polynomial(new int[]{3, -2, 1});
        Assertions.assertEquals(
                p1.minus(p2), new Polynomial(new int[]{-3, 2, -1, 5})
        );
    }

    @Test
    void selfSub() {
        Polynomial p1 = new Polynomial(new int[]{3, -2, 1});
        Assertions.assertEquals(
                p1.minus(p1), new Polynomial(new int[]{0})
        );
    }

    @Test
    void simpleMlt() {
        Polynomial p1 = new Polynomial(new int[]{0, 0, 0, 5});
        Polynomial p2 = new Polynomial(new int[]{3, -2, 1});
        Assertions.assertEquals(
                p1.mlt(p2), new Polynomial(new int[]{0, 0, 0, 15, -10, 5})
        );
    }

    @Test
    void mltVoidFirst() {
        Polynomial p1 = new Polynomial(new int[]{});
        Polynomial p2 = new Polynomial(new int[]{3, -2, 1});
        Assertions.assertEquals(
                p1.mlt(p2), new Polynomial(new int[]{0})
        );
    }

    @Test
    void mltVoidSecond() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{});
        Assertions.assertEquals(
                p1.mlt(p2), new Polynomial(new int[]{0})
        );
    }

    @Test
    void simpleEval() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Assertions.assertEquals(
                p1.evaluate(5), 5 * 5 * 3 + 2 * 5 + 1
        );
    }

    @Test
    void lowerZerosEval() {
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
                p1.differentiate(1), new Polynomial(new int[]{2, 6})
        );
    }

    @Test
    void highPowerDiff() {
        Polynomial p1 = new Polynomial(new int[]{7, 6, 5, 4, 3, 2, 1});
        Assertions.assertEquals(
                p1.differentiate(5), new Polynomial(new int[]{240, 720})
        );
    }

    @Test
    void highPowerZeroDiff() {
        Polynomial p1 = new Polynomial(new int[]{7, 6, 5, 4, 3, 2, 1});
        Assertions.assertEquals(
                p1.differentiate(100), new Polynomial(new int[]{0})
        );
    }

    @Test
    void nonDiff() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Assertions.assertEquals(
                p1.differentiate(0), p1
        );
    }

    @Test
    void voidDiff() {
        Polynomial p1 = new Polynomial(new int[]{});
        Assertions.assertEquals(
                p1.differentiate(5), new Polynomial(new int[]{0})
        );
    }

    @Test
    void simpleNotEqual() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{3, 2, 1});
        Assertions.assertNotEquals(p1, p2);
    }

    @Test
    void simpleEqual() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{1, 2, 3});
        Assertions.assertEquals(p1, p2);
    }

    @Test
    void voidSecond() {
        Polynomial p1 = new Polynomial(new int[]{1, 2, 3});
        Polynomial p2 = new Polynomial(new int[]{});
        Assertions.assertNotEquals(p1, p2);
    }

    @Test
    void voidBoth() {
        Polynomial p1 = new Polynomial(new int[]{});
        Polynomial p2 = new Polynomial(new int[]{});
        Assertions.assertEquals(p1, p2);
    }

    @Test
    void stringTest() {
        Polynomial p1 = new Polynomial(new int[]{1, -2, 3, -4, 5, -6});
        Assertions.assertEquals(
                p1.toString(), " - 6x^5 + 5x^4 - 4x^3 + 3x^2 - 2x + 1"
        );
    }
}
