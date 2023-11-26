package kozlov.kirill.calculator;

/**
 * Calculator exception base class.
 */
public class CalculatorException extends Exception {
    protected String prefix = "Exception in calculator: ";

    /**
     * Base constructor.
     */
    public CalculatorException() {
        super("Calculator exception");
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
