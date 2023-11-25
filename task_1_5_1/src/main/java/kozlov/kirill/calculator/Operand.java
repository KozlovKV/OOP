package kozlov.kirill.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Operand {
    private OperandEnum operandEnum;
    private final int argsCount;
    private ArrayList<Operand> arguments = new ArrayList<>();
    private Double value = null;
    private Function<double[], Double> function = null;

    public enum OperandEnum {
        NUM("", 0),

        PLUS("+", 2, doubles -> doubles[0] + doubles[1]),
        MINUS("-", 2, doubles -> doubles[0] - doubles[1]),
        MLT("*", 2, doubles -> doubles[0] * doubles[1]),
        DIV("/", 2, doubles -> doubles[0] / doubles[1]),

        // TODO: проверять на положительность аргументы логарифма
        LOG("log", 2, doubles -> Math.log(doubles[1]) /Math.log(doubles[0])),
        POW("pow", 2, doubles -> Math.pow(doubles[0], doubles[1])),
        SQRT("sqrt", 1, doubles -> Math.sqrt(doubles[0])),
        SIN("sin", 1, doubles -> Math.sin(doubles[0])),
        COS("cos", 1, doubles -> Math.cos(doubles[0]));

        private static final HashMap<String, OperandEnum> tokenToOperand = new HashMap<>();

        static {
            for (OperandEnum op : OperandEnum.values()) {
                tokenToOperand.put(op.operandToken, op);
            }
        }

        public static OperandEnum getOperand(String token) {
            return tokenToOperand.getOrDefault(token, OperandEnum.NUM);
        }

        private final String operandToken;
        private final int argsCount;
        private Function<double[], Double> function = null;

        OperandEnum(String token, int argsCount) {
            this.operandToken = token;
            this.argsCount = argsCount;
        }

        OperandEnum(String token, int argsCount, Function<double[], Double> function) {
            this.operandToken = token;
            this.argsCount = argsCount;
            this.function = function;
        }

        @Override
        public String toString() {
            return String.format("%s:%d", name(), argsCount);
        }
    }

    public static Operand getOperandsTree(LinkedList<String> tokens) {
        Operand rootOperand = new Operand(tokens.poll());
        for (int i = 0; i < rootOperand.argsCount; ++i) {
            rootOperand.arguments.add(getOperandsTree(tokens));
        }
        return rootOperand;
    }

    public Operand(String token) {
        this.operandEnum = OperandEnum.getOperand(token);
        argsCount = operandEnum.argsCount;
        if (operandEnum == OperandEnum.NUM) {
            value = Double.parseDouble(token); // TODO: catch exceptions
        } else {
            function = operandEnum.function;
        }
    }

    public Double evaluate() {
        if (value != null) {
            return value;
        }
        double[] argsValues = new double[argsCount];
        int i = 0;
        for (var argumentOperand : arguments) {
            argsValues[i++] = argumentOperand.evaluate();
        }
        return function.apply(argsValues);
    }

    @Override
    public String toString() {
        if (operandEnum == OperandEnum.NUM)
            return value.toString();
        StringBuilder sb = new StringBuilder(operandEnum.toString());
        if (value != null) {
            sb.append("(").append(value).append(")");
        } else {
            sb.append(" [ ").append(
                    arguments.stream()
                            .map(Operand::toString)
                            .collect(Collectors.joining("; "))
            ).append(" ] ");
        }
        return sb.toString();
    }
}
