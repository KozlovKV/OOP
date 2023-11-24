package kozlov.kirill.calculator;

import java.util.HashMap;

public class Operand {
    private OperandEnum operandEnum;
    private final int argsCount;
    private Double value = null;

    public enum OperandEnum {
        NUM("", 0),

        PLUS("+", 2),
        MINUS("-", 2),
        MLT("*", 2),
        DIV("/", 2),

        LOG("log", 2),
        POW("pow", 2),
        SIN("sin", 1),
        COS("cos", 1);

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

        OperandEnum(String token, int argsCount) {
            this.operandToken = token;
            this.argsCount = argsCount;
        }

        @Override
        public String toString() {
            return String.format("%s:%d", name(), argsCount);
        }
    }

    public Operand(String token) {
        this.operandEnum = OperandEnum.getOperand(token);
        argsCount = operandEnum.argsCount;
        if (operandEnum == OperandEnum.NUM) {
            value = Double.parseDouble(token); // TODO: catch exceptions
        }
    }

    @Override
    public String toString() {
        if (operandEnum == OperandEnum.NUM)
            return value.toString();
        StringBuilder sb = new StringBuilder(operandEnum.toString());
        if (value != null)
            sb.append("(").append(value).append(")");
        return sb.toString();
    }
}
