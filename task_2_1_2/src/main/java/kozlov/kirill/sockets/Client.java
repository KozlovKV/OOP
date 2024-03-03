package kozlov.kirill.sockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.io.*;
import java.net.*;
import java.nio.MappedByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Client implements Callable<Boolean> {
    public static final int CLIENT_PORT = 8000;
    private final int MS_FOR_UDO_RESPONSE = 1000;
    private Socket socket = null;
    private TaskData taskData = null;

    public Client() {}

    public Client(ArrayList<Integer> list) {
        this.taskData = new TaskData(list);
    }

    public Boolean call() {
        if (!getManagerSocket()) {
            return null;
        }
        sendTaskData();
        Boolean result = receiveTaskResult();
        System.out.println("Result got");
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Cannot close client socket");
        }
        return result;
    }

    private boolean getManagerSocket() {
        try {
            socket = new Socket("localhost", Gateway.GATEWAY_PORT);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private void sendTaskData() {
        ObjectMapper mapper = new ObjectMapper();
        String stringData = "";
        try {
            stringData = mapper.writeValueAsString(taskData);
        } catch (JsonProcessingException e) {
            System.err.println("Couldn't convert task data to JSON");
        }
        try {
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write(stringData);
            writer.flush();
        } catch (IOException ignored) {}
    }

    private Boolean receiveTaskResult() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var parser = mapper.createParser(
                    new BufferedReader(new InputStreamReader(
                            socket.getInputStream(), StandardCharsets.UTF_8
                    ))
            );
            TaskResult taskResult = parser.readValueAs(TaskResult.class);
            return taskResult.result();
        } catch (UnrecognizedPropertyException e) {
            System.err.println("Result parsing error: " + e.getMessage());
        } catch (IOException ignored) {}
        return null;
    }
}
