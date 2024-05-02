package kozlov.kirill.util;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for JsonUtils class.
 */
public class JsonUtilsTest {
    public record TestObjectType(int intField) implements Serializable {}

    @Test
    void stringsSerializeParseTest() {
        TestObjectType obj = new TestObjectType(5);
        try {
            String serialized = JsonUtils.serialize(obj);
            Assertions.assertEquals(serialized, "{\"intField\":5}");
            TestObjectType parsedObj = JsonUtils.parse(serialized, TestObjectType.class);
            Assertions.assertEquals(obj, parsedObj);
        } catch (IOException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void streamsSerializeParseTest() {
        TestObjectType obj = new TestObjectType(5);
        try {
            PipedOutputStream pipedOutputStream = new PipedOutputStream();
            PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
            JsonUtils.serialize(obj, pipedOutputStream);
            TestObjectType parsedObj = JsonUtils.parse(pipedInputStream, TestObjectType.class);
            Assertions.assertEquals(obj, parsedObj);
        } catch (IOException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void parsingErrorTest() {
        try {
            TestObjectType parsedObj = JsonUtils.parse(
                "{\"intField\":\"abc\"}", TestObjectType.class
            );
        } catch (ParsingException e) {
            return;
        }
        Assertions.fail();
    }
}

