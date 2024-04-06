package kozlov.kirill.jsonutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class JsonUtils {
    private JsonUtils() {}

    /**
     * Serializer.
     *
     * @param object NetworkSendable implementation object
     *
     * @return String result of serialization
     *
     * @throws IOException any exception while tries to serialize object
     */
    public static String serialize(Serializable object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    /**
     * Serializer.
     *
     * @param object NetworkSendable implementation object
     * @param outputStream stream for writing serialized object
     *
     * @throws IOException any exception while tries to serialize object
     */
    public static void serialize(
        Serializable object, OutputStream outputStream
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, object);
    }

    /**
     * Input string parser.
     *
     * @param inputString string with full data
     * @param type object's class type for parsing
     * @param <T> type of returned object implementing NetworkSendable interface
     *
     * @return parsed object
     *
     * @throws ParsingException throws up any exception which can be produced during parsing process
     */
    public static <T extends Serializable> T parse(
        String inputString, Class<T> type
    ) throws ParsingException {
        try {
            return new ObjectMapper().readValue(inputString, type);
        } catch (IOException e) {
            throw new ParsingException(e);
        }
    }

    /**
     * Input stream parser.
     *
     * @param inputStream stream with input data. Input data can fill many lines
     * @param type object's class type for parsing
     * @param <T> type of returned object implementing NetworkSendable interface
     *
     * @return parsed object or null if input stream was closed
     *
     * @throws ParsingException throws up any exception which can be produced during parsing process
     * @throws EndOfStreamException specific exception for closed stream situation
     */
    public static <T extends Serializable> T parse(
        InputStream inputStream, Class<T> type
    ) throws ParsingException, EndOfStreamException {
        ObjectMapper mapper = new ObjectMapper();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            inputStream, StandardCharsets.UTF_8
        ));
        try {
            var parser = mapper.createParser(reader);
            return parser.readValueAs(type);
        } catch (MismatchedInputException endOfFileException) {
            throw new EndOfStreamException(endOfFileException);
        } catch (IOException e) {
            throw new ParsingException(e);
        }
    }
}
