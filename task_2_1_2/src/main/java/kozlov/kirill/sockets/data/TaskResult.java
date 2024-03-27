package kozlov.kirill.sockets.data;

/**
 * Record class for task's result.
 *
 * @param result boolean result value
 */
public record TaskResult(Boolean result) implements NetworkSendable {}
