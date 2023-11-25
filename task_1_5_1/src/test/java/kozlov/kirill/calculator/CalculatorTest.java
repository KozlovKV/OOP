package kozlov.kirill.calculator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CalculatorTest {
    @Test
    void checkBaseExample() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("sin + - 1 2 1");
        Assertions.assertEquals(0, res);
    }

    @Test
    void checkAdditionalSpaces() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("   sin  +  -  1  2  1   ");
        Assertions.assertEquals(0, res);
    }

    // TODO: добавить тесты на специфические случаи строки (пустая, неверные токены, неверное количество токенов)

    @Test
    void checkCos() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("cos + - 1 2 1");
        Assertions.assertEquals(1, res);
    }

    @Test
    void checkMltDiv() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("/ * 2 5 + 2 2");
        Assertions.assertEquals(2.5, res);
    }

    @Test
    void checkDivZero() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("/ 1 0");
        Assertions.assertTrue(res.isInfinite());
    }

    @Test
    void checkLog() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("log 2 8");
        Assertions.assertEquals(3, res);
    }

    @Test
    void checkLogLess1Base() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("log 0.5 8");
        Assertions.assertEquals(-3, res);
    }

    @Test
    void checkLogLess1Number() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("log 2 0.25");
        Assertions.assertEquals(-2, res);
    }

    @Test
    void checkLogLess1Both() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("log 0.5 0.25");
        Assertions.assertEquals(2, res);
    }

    // TODO: добавить тесты на отрицательные значение

    @Test
    void checkSqrt() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("sqrt 9");
        Assertions.assertEquals(3, res);
    }

    @Test
    void checkSqrtLess1() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("sqrt 0.25");
        Assertions.assertEquals(0.5, res);
    }

    // TODO: добавить тесты на отрицательное значение

    @Test
    void checkPow() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("pow 2 5");
        Assertions.assertEquals(32, res);
    }

    @Test
    void checkPowNegativeNumber() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("pow -2 5");
        Assertions.assertEquals(-32, res);
    }

    @Test
    void checkPowNegativePower() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("pow 2 -5");
        Assertions.assertEquals((double) 1/32, res);
    }

    @Test
    void checkPowNegativeBoth() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("pow -2 -5");
        Assertions.assertEquals((double) -1/32, res);
    }

    @Test
    void checkPowLess1Number() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("pow 0.3 2");
        Assertions.assertEquals(0.09, res);
    }

    @Test
    void checkPowLess1Power() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("pow 4 0.5");
        Assertions.assertEquals(2, res);
    }

    @Test
    void checkPow1Power() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("pow 4 1");
        Assertions.assertEquals(4, res);
    }

    @Test
    void checkPow0Power() {
        Calculator calculator = new Calculator();
        Double res = calculator.calculate("pow 4 0");
        Assertions.assertEquals(1, res);
    }
}
