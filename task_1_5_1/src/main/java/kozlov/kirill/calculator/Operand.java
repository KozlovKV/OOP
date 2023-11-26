package kozlov.kirill.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Operand class.
 * This is common class for any operand
 *
 * <p>Number is assumed as zero-argument operand with defined value field
 *
 * <p>For non-zero-argument operand class stores its children operands
 */
public class Operand {
    private OperandEnum operandEnum;
    private final int argsCount;
    private ArrayList<Operand> arguments = new ArrayList<>();
    private Double value = null;

    /**
     * Enum class for operands.
     * Implements comfortable interface for configuring any function using lambdas
     */
    public enum OperandEnum {
        NUM("", 0),

        PLUS("+", 2, doubles -> doubles[0] + doubles[1]),
        MINUS("-", 2, doubles -> doubles[0] - doubles[1]),
        MLT("*", 2, doubles -> doubles[0] * doubles[1]),
        DIV("/", 2, doubles -> doubles[0] / doubles[1]),

        LOG("log", 2,
            doubles -> Math.log(doubles[1]) / Math.log(doubles[0]),
            doubles ->
                doubles[0] <= 0 || doubles[1] <= 0
                        ? "Logarithm arguments must be positive. Got " + doubles[0] + ", " + doubles[1]
                        : null
            ),
        POW("pow", 2, doubles -> Math.pow(doubles[0], doubles[1])),
        SQRT("sqrt", 1,
                doubles -> Math.sqrt(doubles[0]),
                doubles ->
                        doubles[0] < 0
                                ? "Sqrt argument must be >= 0. Got " + doubles[0]
                                : null
        ),
        SIN("sin", 1, doubles -> Math.sin(doubles[0])),
        COS("cos", 1, doubles -> Math.cos(doubles[0]));

        private static final HashMap<String, OperandEnum> tokenToOperand = new HashMap<>();

        static {
            for (OperandEnum op : OperandEnum.values()) {
                tokenToOperand.put(op.operandToken, op);
            }
        }

        /**
         * Enums' getter.
         * Uses constructed in static constructor hashmap which gives OperandEnum by it's token
         *
         * @param token operand's string token
         * @return OperandEnum instance for function token or NUM for any other token
         *     (invalid tokens are detected below)
         */
        public static OperandEnum getOperand(String token) {
            return tokenToOperand.getOrDefault(token, OperandEnum.NUM);
        }

        private final String operandToken;
        private final int argsCount;
        private Function<double[], Double> function = null;
        private Validator validator = null;

        /**
         * Basic constructor.
         * Used for NUM
         *
         * @param token string token
         * @param argsCount arguments count for this operand
         */
        OperandEnum(String token, int argsCount) {
            this.operandToken = token;
            this.argsCount = argsCount;
        }

        /**
         * Constructor for defined everywhere function.
         *
         * @param token string token
         * @param argsCount arguments count for this operand
         * @param function lambda interface Function for evaluating operand's value
         */
        OperandEnum(String token, int argsCount, Function<double[], Double> function) {
            this.operandToken = token;
            this.argsCount = argsCount;
            this.function = function;
        }

        /**
         * Constructor for function with additional validation.
         *
         * @param token string token
         * @param argsCount arguments count for this operand
         * @param function lambda interface Function for evaluating operand's value
         * @param validator lambda Validator which checks arguments
         *     and returns error message or null if they're valid
         */
        OperandEnum(String token, int argsCount,
                    Function<double[], Double> function,
                    Validator validator) {
            this.operandToken = token;
            this.argsCount = argsCount;
            this.function = function;
            this.validator = validator;
        }

        /**
         * Override toString method.
         *
         * @return string for operand enum
         */
        @ExcludeFromJacocoGeneratedReport
        @Override
        public String toString() {
            return String.format("%s:%d", name(), argsCount);
        }
    }

    /**
     * Operands tree recursive constructor.
     *
     * @param tokens remaining tokens
     * @return create operand with children for non-zero-arguments operands
     * @throws TokensException throws weather there are not enough tokens
     *     for constructing operands tree
     */
    public static Operand getOperandsTree(LinkedList<String> tokens) throws TokensException {
        if (tokens.isEmpty()) {
            throw new TokensException("Not enough tokens for operands");
        }
        Operand rootOperand = new Operand(tokens.poll());
        for (int i = 0; i < rootOperand.argsCount; ++i) {
            rootOperand.arguments.add(getOperandsTree(tokens));
        }
        return rootOperand;
    }

    /**
     * Operand constructor.
     * Gets operand's configuration from OperandEnum and initialized necessary fields
     *
     * @param token operand's string token
     * @throws TokensException throws exception weather number operand can't be parsed from token
     */
    public Operand(String token) throws TokensException {
        this.operandEnum = OperandEnum.getOperand(token);
        argsCount = operandEnum.argsCount;
        if (operandEnum == OperandEnum.NUM) {
            try {
                value = Double.parseDouble(token);
            } catch (NumberFormatException e) {
                TokensException tokensException = new TokensException("Can't parse token " + token);
                tokensException.initCause(e);
                throw tokensException;
            }
        }
    }

    /**
     * Operands tree recursive evaluation.
     *
     * <p>For number operand returns its value
     *
     * <p>For other operands:
     * <ol>
     *     <li>Evaluate their arguments using children operands evaluate method
     *     <li>Validate arguments if validation is defined in OperandEnum
     *     <li>Evaluate function using Function lambda in OperandEnum
     * </ol>
     *
     * @return evaluated value
     * @throws EvaluationException throws evaluation exception
     *     weather validation returned non-null error message
     */
    public Double evaluate() throws EvaluationException {
        if (value != null) {
            return value;
        }
        double[] argsValues = new double[argsCount];
        int i = 0;
        for (var argumentOperand : arguments) {
            argsValues[i++] = argumentOperand.evaluate();
        }
        String errorMessage = null;
        if (operandEnum.validator != null) {
            errorMessage = operandEnum.validator.validate(argsValues);
        }
        if (errorMessage != null) {
            throw new EvaluationException(errorMessage);
        }
        return operandEnum.function.apply(argsValues);
    }

    /**
     * Override toString method.
     *
     * @return string for recursive operands tree
     */
    @ExcludeFromJacocoGeneratedReport
    @Override
    public String toString() {
        if (operandEnum == OperandEnum.NUM) {
            return value.toString();
        }
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
