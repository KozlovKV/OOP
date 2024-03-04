package kozlov.kirill.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.NetworkSendable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class BasicTCPSocketOperations {
    public static void sendJSONObject(
        Socket socket, NetworkSendable object
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String stringData = mapper.writeValueAsString(object);
        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
        writer.write(stringData + "\n");
        writer.flush();
    }

    public static <T extends NetworkSendable> T receiveJSONObject(
        Socket socket, Class<T> type
    ) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var parser = mapper.createParser(
                    new BufferedReader(new InputStreamReader(
                            socket.getInputStream(), StandardCharsets.UTF_8
                    ))
            );
            return parser.readValueAs(type);
        } catch (IOException e) {
            StringBuilder stringBuilder = new StringBuilder("Incorrect type of request\n");
//            stringBuilder.append() TODO: Тут хочу придумать, как отправлять пользователю также и жеалемый формат
            ErrorMessage errorMessage = new ErrorMessage(stringBuilder.toString());
            sendJSONObject(socket, errorMessage);
        }
        return null;
    }
}
