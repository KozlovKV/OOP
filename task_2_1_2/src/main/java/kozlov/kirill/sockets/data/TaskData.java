package kozlov.kirill.sockets.data;

import java.util.ArrayList;

/**
 * Record class for a task's data.
 *
 * @param numbers list of numbers
 */
public record TaskData(ArrayList<Integer> numbers) implements NetworkSendable {}
