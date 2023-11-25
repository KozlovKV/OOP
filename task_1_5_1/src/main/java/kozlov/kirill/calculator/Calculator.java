package kozlov.kirill.calculator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Calculator {
    Double calculate(String expression) {
        LinkedList<String> tokens = new LinkedList<>(
                Arrays.stream(expression.split(" "))
                        .filter(token -> !token.isEmpty())
                        .collect(Collectors.toList())
        );
        // TODO: отлавливать ноль токенов
        Operand rootOperand = Operand.getOperandsTree(tokens);
        System.out.println(tokens.size()); // TODO: отлавливать ситуацию, когда не все токены были распознаны
        return rootOperand.evaluate();
    }

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        System.out.println(calculator.calculate("sin + - 1 2 1"));
    }
}