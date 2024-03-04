package kozlov.kirill.sockets.data;

import java.util.ArrayList;

public record TaskData(ArrayList<Integer> numbers) implements NetworkSendable {}
