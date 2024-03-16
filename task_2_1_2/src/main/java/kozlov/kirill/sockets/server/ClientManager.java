package kozlov.kirill.sockets.server;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import kozlov.kirill.sockets.BasicTCPSocketOperations;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.exceptions.InternalWorkerErrorException;
import kozlov.kirill.sockets.exceptions.WorkerNotFoundException;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

/**
 * Manager for processing client's tasks.
 * <br>
 * Implemented for thread-usage.
 */
public class ClientManager implements Runnable {
    final private int multicastServerPort;
    final private Socket clientManagerSocket;
    final private int workersPerTask;

    public ClientManager(Socket clientManagerSocket, int multicastServerPort, int workersPerTask) {
        this.clientManagerSocket = clientManagerSocket;
        this.multicastServerPort = multicastServerPort;
        this.workersPerTask = workersPerTask;
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
                new Thread(getTaskManagerHandler(taskData)).start(); // TODO: перейти на виртуальные потоки
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

    private Runnable getTaskManagerHandler(TaskData taskData) {
        return () -> {
            try {
                Optional<TaskResult> optionalTaskResult = new TaskManager(
                        taskData, workersPerTask,
                        multicastServerPort, clientManagerSocket.getPort()
                ).call();
                optionalTaskResult.ifPresent(taskResult -> {
                    try {
                        BasicTCPSocketOperations.sendJSONObject(
                                clientManagerSocket, taskResult
                        );
                    } catch (IOException e) {
                        System.err.println(
                                "Error to send result to client " +
                                        clientManagerSocket.getRemoteSocketAddress()
                        );
                    }
                });
            } catch (WorkerNotFoundException notFoundException) {
                try {
                    BasicTCPSocketOperations.sendJSONObject(
                            clientManagerSocket, new ErrorMessage("Server couldn't find calculation node")
                    );
                } catch (IOException ignored) {}
                System.err.println("Couldn't find calculation node");
            } catch (InternalWorkerErrorException workerErrorException) {
                try {
                    BasicTCPSocketOperations.sendJSONObject(
                            clientManagerSocket, new ErrorMessage("Server got error while calculated and cannot restart process")
                    );
                } catch (IOException ignored) {}
                System.out.println("Couldn't find new calculation node");
            }
        };
    }
}
