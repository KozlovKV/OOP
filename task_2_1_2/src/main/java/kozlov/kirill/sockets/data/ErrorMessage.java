package kozlov.kirill.sockets.data;

public record ErrorMessage(String message) implements NetworkSendable {
}
