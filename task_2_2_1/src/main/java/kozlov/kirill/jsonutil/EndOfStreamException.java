package kozlov.kirill.jsonutil;

import kozlov.kirill.ExcludeConstructorFromJacocoGeneratedReport;

/**
 * Exception for comfortable indicating about EOF in socket stream using parser.
 */
public class EndOfStreamException extends Exception {
    /**
     * Unused default constructor.
     */
    @ExcludeConstructorFromJacocoGeneratedReport
    public EndOfStreamException() {
        super();
    }

    /**
     * Cause taking constructor.
     *
     * @param cause cause of an exception. Practically it's MismatchedInputException
     *     produced by JsonParser
     */
    public EndOfStreamException(Throwable cause) {
        super(cause);
    }
}
