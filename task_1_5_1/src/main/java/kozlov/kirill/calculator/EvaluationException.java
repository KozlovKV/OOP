package kozlov.kirill.calculator;

/**
 * Exception class for any error in evaluation (now caused only by validators).
 */
public class EvaluationException extends CalculatorException {
    private static final String DEFAULT_MESSAGE = "Exception in evaluation";
    private static final String PREFIX = "Exception in evaluation: ";

    /**
     * Base constructor.
     */
    public EvaluationException() {
        super(DEFAULT_MESSAGE, "");
    }

    /**
     * Constructor with message.
     *
     * @param message error message
     */
    public EvaluationException(String message) {
        super(message, PREFIX);
    }
}
