package kozlov.kirill.calculator;

import java.util.ArrayList;
import java.util.Arrays;

public class Calculator {
    double calculate(String expression) {
        ArrayList<String> tokens = new ArrayList<>(
            Arrays.stream(expression.split(" "))
                    .filter(token -> !token.isEmpty())
                    .toList()
        );
        System.out.println(tokens.stream().map(Operand::new).toList());
        double res = 0;
        return res;
    }

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        calculator.calculate("sin    + - 1 2 1");
    }
}