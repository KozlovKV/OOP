package kozlov.kirill.sockets;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import kozlov.kirill.sockets.data.BasicMapperOperations;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.NetworkSendable;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.exceptions.ParsingException;

/**
 * Client class.
 */
public class Client implements Callable<NetworkSendable> {
    private Socket socket = null;
    private TaskData taskData = null;
    private final int serverPort;
    private final String hostname;

    /**
     * Client constructor.
     *
     * @param list integers for task
     * @param hostname server address
     * @param serverPort server port
     */
    public Client(ArrayList<Integer> list, String hostname, int serverPort) {
        this.taskData = new TaskData(list);
        this.hostname = hostname;
        this.serverPort = serverPort;
    }

    /**
     * Client's callable method.
     *
     * @return result of servers working
     */
    public NetworkSendable call() {
        if (!getManagerSocket()) {
            return null;
        }
        sendTaskData();
        NetworkSendable result = null;
        if (!Thread.currentThread().isInterrupted()) {
            result = receiveTaskResult();
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Cannot close client socket");
        }
        return result;
    }

    private boolean getManagerSocket() {
        try {
            socket = new Socket(hostname, serverPort);
            return true;
        } catch (IOException e) {
            System.err.println("Couldn't acquire connection");
            return false;
        }
    }

    private void sendTaskData() {
        try {
            BasicTcpSocketOperations.sendJSONObject(socket, taskData);
        } catch (IOException ignored) {}
    }

    private NetworkSendable receiveTaskResult() {
        String response = "";
        try {
            response = BasicTcpSocketOperations.receiveString(socket);
            TaskResult taskResult = BasicMapperOperations.parse(response, TaskResult.class);
            return taskResult;
        } catch (ParsingException e) {
            System.err.println("Failed to parse correct result. Parsing error message...");
            try {
                ErrorMessage errorMessage = BasicMapperOperations.parse(response, ErrorMessage.class);
                return errorMessage;
            } catch (ParsingException ignored) {
                System.err.println("Failed to parse error message");
            }
        } catch (IOException ignored) {}
        return null;
    }
}
