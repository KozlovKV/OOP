package kozlov.kirill.calculator;

/**
 * Exception class for any error in evaluation (now caused only by validators).
 */
public class EvaluationException extends CalculatorException {
    protected static final String prefix = "Exception in evaluation: ";

    /**
     * Base constructor.
     */
    public EvaluationException() {
        super("Exception in evaluation", "");
    }

    /**
     * Constructor with message.
     *
     * @param message error message
     */
    public EvaluationException(String message) {
        super(message, prefix);
    }
}
