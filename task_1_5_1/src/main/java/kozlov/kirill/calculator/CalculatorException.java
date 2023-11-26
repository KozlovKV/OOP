package kozlov.kirill.calculator;

/**
 * Calculator exception base class.
 */
public class CalculatorException extends Exception {
    private static final String DEFAULT_MESSAGE = "Calculator exception";
    private String prefix = "Exception in calculator: ";

    /**
     * Base constructor.
     */
    public CalculatorException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructor for specified message and child exception prefix.
     *
     * @param message error message
     * @param prefix prefix from child class
     */
    public CalculatorException(String message, String prefix) {
        super(message);
        this.prefix = prefix;
    }

    /**
     * Override getMessage method.
     *
     * @return error message with added prefix
     */
    @Override
    public String getMessage() {
        return prefix + super.getMessage();
    }
}
