package kozlov.kirill.calculator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Calculator test class.
 */
public class CalculatorTest {
    @Test
    void checkBaseExample() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("sin + - 1 2 1");
        Assertions.assertEquals(0, res);
    }

    @Test
    void checkAdditionalSpaces() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("   sin  +  -  1  2  1   ");
        Assertions.assertEquals(0, res);
    }

    @Test
    void checkNotEmptyInput() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(TokensException.class, () ->
                calculator.calculate("")
        );
    }

    @Test
    void checkNotEnoughTokens() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(TokensException.class, () ->
                calculator.calculate("+ 1")
        );
    }

    @Test
    void checkNotTooMuchTokens() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(TokensException.class, () ->
                calculator.calculate("+ 1 2 3")
        );
    }

    @Test
    void checkInvalidToken() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(TokensException.class, () ->
                calculator.calculate("paw 2 2")
        );
    }

    @Test
    void checkInvalidNumberToken() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(TokensException.class, () ->
                calculator.calculate("+ a1 2")
        );
    }

    @Test
    void checkSingleNumber() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("1.35");
        Assertions.assertEquals(1.35, res);
    }

    @Test
    void checkCos() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("cos + - 1 2 1");
        Assertions.assertEquals(1, res);
    }

    @Test
    void checkMltDiv() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("/ * 2 5 + 2 2");
        Assertions.assertEquals(2.5, res);
    }

    @Test
    void checkDivZero() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("/ 1 0");
        Assertions.assertTrue(res.isInfinite());
    }

    @Test
    void checkLog() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("log 2 8");
        Assertions.assertEquals(3, res);
    }

    @Test
    void checkLogLess1Base() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("log 0.5 8");
        Assertions.assertEquals(-3, res);
    }

    @Test
    void checkLogLess1Number() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("log 2 0.25");
        Assertions.assertEquals(-2, res);
    }

    @Test
    void checkLogLess1Both() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("log 0.5 0.25");
        Assertions.assertEquals(2, res);
    }

    @Test
    void checkLogNonPositive1() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(EvaluationException.class, () ->
                calculator.calculate("log -2 8")
        );
    }

    @Test
    void checkLogNonPositive2() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(EvaluationException.class, () ->
                calculator.calculate("log 2 -8")
        );
    }

    @Test
    void checkLogNonPositiveBoth() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(EvaluationException.class, () ->
                calculator.calculate("log -2 -8")
        );
    }

    @Test
    void checkLogZero() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(EvaluationException.class, () ->
                calculator.calculate("log 2 0")
        );
    }

    @Test
    void checkSqrt() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("sqrt 9");
        Assertions.assertEquals(3, res);
    }

    @Test
    void checkSqrtLess1() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("sqrt 0.25");
        Assertions.assertEquals(0.5, res);
    }

    @Test
    void checkSqrtZero() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("sqrt 0");
        Assertions.assertEquals(0, res);
    }

    @Test
    void checkSqrtNegative() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(EvaluationException.class, () ->
                calculator.calculate("sqrt -4")
        );
    }

    @Test
    void checkPow() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("pow 2 5");
        Assertions.assertEquals(32, res);
    }

    @Test
    void checkPowNegativeNumber() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("pow -2 5");
        Assertions.assertEquals(-32, res);
    }

    @Test
    void checkPowNegativePower() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("pow 2 -5");
        Assertions.assertEquals((double) 1/32, res);
    }

    @Test
    void checkPowNegativeBoth() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("pow -2 -5");
        Assertions.assertEquals((double) -1/32, res);
    }

    @Test
    void checkPowLess1Number() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("pow 0.3 2");
        Assertions.assertEquals(0.09, res);
    }

    @Test
    void checkPowLess1Power() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("pow 4 0.5");
        Assertions.assertEquals(2, res);
    }

    @Test
    void checkPow1Power() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("pow 4 1");
        Assertions.assertEquals(4, res);
    }

    @Test
    void checkPow0Power() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculateWithErrorPrinting("pow 4 0");
        Assertions.assertEquals(1, res);
    }
}
