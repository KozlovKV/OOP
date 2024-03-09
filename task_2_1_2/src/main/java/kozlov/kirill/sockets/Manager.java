package kozlov.kirill.sockets;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.multicast.MulticastUtils;

import java.io.IOException;
import java.net.*;

/**
 * Manager for processing client's tasks.
 * <br>
 * Implemented for thread-usage.
 */
public class Manager implements Runnable {
    final private int multicastServerPort;
    final private Socket clientManagerSocket;

    public Manager(Socket clientManagerSocket, int multicastServerPort) {
        this.clientManagerSocket = clientManagerSocket;
        this.multicastServerPort = multicastServerPort;
    }

    /**
     * Main processing of client's requests.
     * <br>
     * Whereas socket is opened reads requests for it and send results of calculation
     */
    public void run() {
        try {
            while (true) {
                TaskData taskData = receiveTaskData(clientManagerSocket);
                if (clientManagerSocket.isClosed()) {
                    break;
                }
                if (taskData == null) {
                    System.err.println("Incorrect data");
                    continue;
                }
                // TODO: добавить очередь ожидающих запросов
                new Thread(
                        getRunnableManagerWorkerCommunicationFunction(taskData)
                ).start(); // TODO: перейти на виртуальные потоки
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Connection closed with exception");
        }
        System.out.println("Connection closed");
    }

    private TaskData receiveTaskData(Socket socket) {
        try {
            return BasicTCPSocketOperations.receiveJSONObject(
                    socket, TaskData.class
            );
        } catch (UnrecognizedPropertyException e) {
            System.err.println("Parsing error: " + e.getMessage());
        } catch (IOException ignored) {}
        return null;
    }

    private Runnable getRunnableManagerWorkerCommunicationFunction(TaskData taskData) {
        return () -> {
            Socket workerSocket = MulticastUtils.getClientSocketByMulticastResponse(
                    clientManagerSocket.getPort(), multicastServerPort
            );
            if (workerSocket == null) {
                try {
                    BasicTCPSocketOperations.sendJSONObject(
                            clientManagerSocket, new ErrorMessage("Server couldn't find calculation node")
                    );
                } catch (IOException ignored) {}
                return;
            }
            System.out.println("Chosen worker " + workerSocket.getRemoteSocketAddress());
            try {
                BasicTCPSocketOperations.sendJSONObject(workerSocket, taskData);
                BasicTCPSocketOperations.sendJSONObject(
                        clientManagerSocket,
                        BasicTCPSocketOperations.receiveJSONObject(workerSocket, TaskResult.class)
                );
            } catch (IOException e) {
                System.err.println("Error in communication with worker");
            }
        };
    }
}
