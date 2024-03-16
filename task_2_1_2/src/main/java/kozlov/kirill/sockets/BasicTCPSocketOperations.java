package kozlov.kirill.sockets;

import kozlov.kirill.sockets.data.BasicMapperOperations;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.NetworkSendable;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

final public class BasicTCPSocketOperations {
    public static void sendJSONObject(
        Socket socket, NetworkSendable object
    ) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
        writer.write(BasicMapperOperations.serialize(object) + "\n");
        writer.flush();
    }

    public static <T extends NetworkSendable> T receiveJSONObject(
        Socket socket, Class<T> type
    ) throws IOException {
        try {
            InputStream inputStream = socket.getInputStream();
            return BasicMapperOperations.parse(inputStream, type);
        } catch (IOException e) {
            // TODO: обрабатывать только ошибки с получением результата, ошибки парсинга должны обрабатываться отдельно
//            ErrorMessage errorMessage = new ErrorMessage("Incorrect type of request\n");
//            sendJSONObject(socket, errorMessage);
        }
        return null;
    }

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
