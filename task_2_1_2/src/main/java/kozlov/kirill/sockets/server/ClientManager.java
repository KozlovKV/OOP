package kozlov.kirill.sockets.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import kozlov.kirill.sockets.BasicTCPSocketOperations;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.data.utils.ErrorMessages;
import kozlov.kirill.sockets.exceptions.EndOfStreamException;
import kozlov.kirill.sockets.exceptions.InternalWorkerErrorException;
import kozlov.kirill.sockets.exceptions.ParsingException;
import kozlov.kirill.sockets.exceptions.WorkerNotFoundException;

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
        ThreadFactory virtualThreadsFactory =
            Thread.ofVirtual().name("Task manager ", 1).factory();
        try {
            while (true) {
                TaskData taskData = receiveTaskData(clientManagerSocket);
                virtualThreadsFactory.newThread(
                    getTaskManagerHandler(taskData)
                ).start();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Connection closed with exception");
        } catch (EndOfStreamException e) {
            System.out.println("Connection closed");
        }
    }

    private TaskData receiveTaskData(Socket socket)
    throws EndOfStreamException {
        try {
            return BasicTCPSocketOperations.receiveJSONObject(
                    socket, TaskData.class
            );
        } catch (ParsingException parsingException) {
            System.err.println("Parsing error: " + parsingException.getMessage());
            try {
                BasicTCPSocketOperations.sendJSONObject(
                        clientManagerSocket, ErrorMessages.taskDataParsingError
                );
            } catch (IOException e) {
                System.err.println(
                    "Error to send parsing error message to client " +
                    clientManagerSocket.getRemoteSocketAddress()
                );
            }
        } catch (IOException ignored) {}
        return null;
    }

    private Runnable getTaskManagerHandler(TaskData taskData) {
        return () -> {
            try {
                Optional<TaskResult> optionalTaskResult = new TaskManager(
                        taskData, workersPerTask,
                        multicastServerPort, clientManagerSocket.getPort()
                ).processTask();
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
                            clientManagerSocket, ErrorMessages.workerNotFoundMessage
                    );
                } catch (IOException ignored) {}
                System.err.println("Couldn't find calculation node");
            } catch (InternalWorkerErrorException workerErrorException) {
                try {
                    BasicTCPSocketOperations.sendJSONObject(
                            clientManagerSocket, ErrorMessages.workerInternalErrorMessage
                    );
                } catch (IOException ignored) {}
                System.out.println("Couldn't find new calculation node");
            }
        };
    }
}
