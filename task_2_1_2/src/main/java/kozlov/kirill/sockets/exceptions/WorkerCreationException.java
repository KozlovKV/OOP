package kozlov.kirill.sockets.exceptions;

import kozlov.kirill.ExcludeConstructorFromJacocoGeneratedReport;

/**
 * Exception for case of error in worker's creation.
 */
public class WorkerCreationException extends Exception {
    /**
     * Unused default constructor.
     */
    @ExcludeConstructorFromJacocoGeneratedReport
    public WorkerCreationException() {
        super();
    }

    /**
     * Cause taking constructor.
     *
     * @param cause cause of an exception
     */
    public WorkerCreationException(Throwable cause) {
        super(cause);
    }
}
