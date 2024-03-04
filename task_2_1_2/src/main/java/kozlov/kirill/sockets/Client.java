package kozlov.kirill.sockets;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;

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
            System.err.println("Couldn't acquire connection");
            return false;
        }
    }

    private void sendTaskData() {
        try {
            BasicTCPSocketOperations.sendJSONObject(socket, taskData);
        } catch (IOException ignored) {}
    }

    private Boolean receiveTaskResult() {
        try {
            TaskResult taskResult = BasicTCPSocketOperations.receiveJSONObject(socket, TaskResult.class);
            return taskResult.result();
        } catch (UnrecognizedPropertyException e) {
            System.err.println("Result parsing error: " + e.getMessage());
        } catch (IOException ignored) {}
        return null;
    }
}
