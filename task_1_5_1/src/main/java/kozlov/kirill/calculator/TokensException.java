package kozlov.kirill.calculator;

/**
 * Exception class for eny error in input tokens.
 */
public class TokensException extends CalculatorException {
    private static final String prefix = "Exception in input string: ";

    /**
     * Base constructor.
     */
    public TokensException() {
        super("Exception in input expression", "");
    }

    /**
     * Constructor with message.
     *
     * @param message error message
     */
    public TokensException(String message) {
        super(message, prefix);
    }
}
