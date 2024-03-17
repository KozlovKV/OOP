package kozlov.kirill.sockets.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

final public class BasicMapperOperations {
    private BasicMapperOperations() {}

    public static String serialize(NetworkSendable object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    public static <T extends NetworkSendable> T parse(
            InputStream inputStream, Class<T> type
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream, StandardCharsets.UTF_8
        ));
        var parser = mapper.createParser(reader);
        return parser.readValueAs(type);
    }

    public static <T extends NetworkSendable> T parse(
            String inputString, Class<T> type
    ) throws IOException {
        return new ObjectMapper().readValue(inputString, type);
    }
}
