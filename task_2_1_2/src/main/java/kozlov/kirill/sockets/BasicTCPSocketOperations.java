package kozlov.kirill.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import kozlov.kirill.sockets.data.BasicMapperOperations;
import kozlov.kirill.sockets.data.NetworkSendable;

/**
 * Static class with operation with TCP sockets.
 */
final public class BasicTCPSocketOperations {
    private BasicTCPSocketOperations() {}

    /**
     * Sends object serialized to JSON through specified socket.
     *
     * @param socket socket for sending
     * @param object object implementing NetworkSendable interface
     * @throws IOException throws up any exception from socket or serialization
     */
    public static void sendJSONObject(
        Socket socket, NetworkSendable object
    ) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
        writer.write(BasicMapperOperations.serialize(object) + "\n");
        writer.flush();
    }

    /**
     * Receives object from socket using parsing by type.
     *
     * @param socket socket for receiving
     * @param type type for parsing implementing NetworkSendable interface
     * @param <T> return type
     * @return parsed object
     * @throws IOException any exception during parsing process.
     */
    public static <T extends NetworkSendable> T receiveJSONObject(
        Socket socket, Class<T> type
    ) throws IOException {
        InputStream inputStream = socket.getInputStream();
        return BasicMapperOperations.parse(inputStream, type);
        // TODO: обрабатывать только ошибки с получением результата, ошибки парсинга должны обрабатываться отдельно
    }

    /**
     * Reads string from socket.
     *
     * @param socket input socket
     * @return string with data from socket or null if socket's stream is closed
     * @throws IOException any exception during reading task
     */
    public static String receiveString(
            Socket socket
    ) throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            inputStream, StandardCharsets.UTF_8
        ));
        return reader.readLine();
    }
}
