package kozlov.kirill.sockets;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Client implements Callable<Boolean> {
    private Socket socket = null;
    private TaskData taskData = null;
    private final int serverPort;

    public Client(ArrayList<Integer> list, int serverPort) {
        this.taskData = new TaskData(list);
        this.serverPort = serverPort;
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
            socket = new Socket("localhost", serverPort);
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
