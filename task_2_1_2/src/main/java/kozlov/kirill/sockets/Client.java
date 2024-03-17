package kozlov.kirill.sockets;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import kozlov.kirill.sockets.data.BasicMapperOperations;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.NetworkSendable;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;

public class Client implements Callable<NetworkSendable> {
    private Socket socket = null;
    private TaskData taskData = null;
    private final int serverPort;

    public Client(ArrayList<Integer> list, int serverPort) {
        this.taskData = new TaskData(list);
        this.serverPort = serverPort;
    }

    public NetworkSendable call() {
        if (!getManagerSocket()) {
            return null;
        }
        sendTaskData();
        NetworkSendable result = receiveTaskResult();
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

    private NetworkSendable receiveTaskResult() {
        String response = "";
        try {
            response = BasicTCPSocketOperations.receiveString(socket);
            TaskResult taskResult = BasicMapperOperations.parse(response, TaskResult.class);
            return taskResult;
        } catch (UnrecognizedPropertyException e) {
            System.err.println("Failed to parse correct result. Parsing error message...");
            try {
                ErrorMessage errorMessage = BasicMapperOperations.parse(response, ErrorMessage.class);
                return errorMessage;
            } catch (IOException ignored) {}
        } catch (IOException ignored) {}
        return null;
    }
}
