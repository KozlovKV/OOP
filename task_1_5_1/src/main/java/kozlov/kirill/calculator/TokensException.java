package kozlov.kirill.calculator;

/**
 * Exception class for eny error in input tokens.
 */
public class TokensException extends CalculatorException {
    private static final String DEFAULT_MESSAGE = "Exception in input expression";
    private static final String PREFIX = "Exception in input string: ";

    /**
     * Base constructor.
     */
    public TokensException() {
        super(DEFAULT_MESSAGE, "");
    }

    /**
     * Constructor with message.
     *
     * @param message error message
     */
    public TokensException(String message) {
        super(message, PREFIX);
    }
}
