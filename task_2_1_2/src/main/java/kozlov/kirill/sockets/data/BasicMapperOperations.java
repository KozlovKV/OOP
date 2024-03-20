package kozlov.kirill.sockets.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import kozlov.kirill.sockets.exceptions.EndOfStreamException;
import kozlov.kirill.sockets.exceptions.ParsingException;

/**
 * Static class with function for serialization/parsing of data structures.
 * <br>
 * Functions will work ONLY with types implementing implementing NetworkSendable
 */
public final class BasicMapperOperations {
    private BasicMapperOperations() {}

    /**
     * Serializer.
     *
     * @param object NetworkSendable implementation object
     *
     * @return String result of serialization
     *
     * @throws IOException any exception while tries to serialize object
     */
    public static String serialize(NetworkSendable object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
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
    public static <T extends NetworkSendable> T parse(
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
    public static <T extends NetworkSendable> T parse(
            String inputString, Class<T> type
    ) throws ParsingException {
        try {
            return new ObjectMapper().readValue(inputString, type);
        } catch (IOException e) {
            throw new ParsingException(e);
        }
    }
}
