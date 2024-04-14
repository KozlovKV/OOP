package kozlov.kirill.pizzeria;

import kozlov.kirill.ExcludeConstructorFromJacocoGeneratedReport;

/**
 * Exception for invalid setup for RunnablePizzeria.
 */
public class IllegalSetupDataException extends Exception {
    /**
     * Unused default constructor.
     */
    @ExcludeConstructorFromJacocoGeneratedReport
    public IllegalSetupDataException() {
        super();
    }

    /**
     * Message taking constructor.
     *
     * @param message exception message
     */
    public IllegalSetupDataException(String message) {
        super(message);
    }
}
