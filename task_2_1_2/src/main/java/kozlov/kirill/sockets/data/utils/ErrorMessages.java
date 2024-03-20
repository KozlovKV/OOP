package kozlov.kirill.sockets.data.utils;

import kozlov.kirill.sockets.data.ErrorMessage;

/**
 * Static class with pre-defined error messages.
 */
final public class ErrorMessages {
    private ErrorMessages() {}

    public static ErrorMessage workerNotFoundMessage = new ErrorMessage("Server couldn't find calculation node");
    public static ErrorMessage workerInternalErrorMessage = new ErrorMessage("Server got error while calculated and cannot restart process");
}
