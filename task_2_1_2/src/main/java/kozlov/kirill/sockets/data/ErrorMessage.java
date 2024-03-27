package kozlov.kirill.sockets.data;

/**
 * Record class for an error message.
 *
 * @param message string with message
 */
public record ErrorMessage(String message) implements NetworkSendable {}
