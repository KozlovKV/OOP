package kozlov.kirill.util;

import kozlov.kirill.snake.ExcludeConstructorFromJacocoGeneratedReport;

import java.io.IOException;

/**
 * Exception for any parsing error.
 */
public class ParsingException extends IOException {
    /**
     * Unused default constructor.
     */
    @ExcludeConstructorFromJacocoGeneratedReport
    public ParsingException() {
        super();
    }

    /**
     * Cause taking constructor.
     *
     * @param cause cause of an exception. Practically it's MismatchedInputException
     *     produced by JsonParser
     */
    public ParsingException(Throwable cause) {
        super(cause);
    }
}
