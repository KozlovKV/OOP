package kozlov.kirill.calculator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Main calculator class.
 */
public class Calculator {
    /**
     * Calculation method.
     *
     * @param expression string with operands and numbers separated by space
     * @return calculated value
     * @throws CalculatorException throws up any calculation's exceptions
     *     caused by errors in parsing tokens or evaluation
     */
    Double calculate(String expression) throws CalculatorException {
        LinkedList<String> tokens = new LinkedList<>(
                Arrays.stream(expression.split(" "))
                        .filter(token -> !token.isEmpty())
                        .collect(Collectors.toList())
        );
        if (tokens.isEmpty()) {
            throw new TokensException("Empty expression can't be evaluated");
        }
        Operand rootOperand = Operand.getOperandsTree(tokens);
        if (!tokens.isEmpty()) {
            throw new TokensException("Invalid tokens count");
        }
        return rootOperand.evaluate();
    }

    /**
     * Calculation wrapper with exception printing.
     * Calls method calculate and handles any calculation exception
     *
     * @param expression string with operands and numbers separated by space
     * @return calculated value or if calculation exception was handled
     */
    Double calculateWithErrorPrinting(String expression) {
        Double res = null;
        try {
            res = calculate(expression);
        } catch (CalculatorException e) {
            System.err.println(e.getMessage());
        }
        return res;
    }

    /**
     * Calculator entry point.
     *
     * @param args cmds' args
     */
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        System.out.println(calculator.calculateWithErrorPrinting("paw 2 8"));
    }
}