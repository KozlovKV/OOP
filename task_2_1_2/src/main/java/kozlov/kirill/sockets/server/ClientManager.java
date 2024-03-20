package kozlov.kirill.sockets.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import kozlov.kirill.sockets.BasicTcpSocketOperations;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.data.utils.ErrorMessages;
import kozlov.kirill.sockets.exceptions.EndOfStreamException;
import kozlov.kirill.sockets.exceptions.InternalWorkerErrorException;
import kozlov.kirill.sockets.exceptions.ParsingException;
import kozlov.kirill.sockets.exceptions.WorkerNotFoundException;

/**
 * Manager for communication wih client.
 */
public class ClientManager implements Runnable {
    private final int multicastServerPort;
    private final Socket clientManagerSocket;
    private final int workersPerTask;

    /**
     * ClientManager constructor.
     * <br>
     * Saves data from Gateway where this thread-usage instance was created
     *
     * @param clientManagerSocket client's TCP socket
     * @param multicastServerPort multicast sockets' port for communication with workers
     * @param workersPerTask count of workers needed for processing one task
     */
    public ClientManager(Socket clientManagerSocket, int multicastServerPort, int workersPerTask) {
        this.clientManagerSocket = clientManagerSocket;
        this.multicastServerPort = multicastServerPort;
        this.workersPerTask = workersPerTask;
    }

    /**
     * Thread-started method.
     * <br>
     * Whereas socket is opened provides validation for user requests and
     * creation of virtual threads for processing valid tasks
     */
    public void run() {
        ThreadFactory virtualThreadsFactory =
            Thread.ofVirtual().name("Task manager ", 1).factory();
        try {
            while (true) {
                receiveTaskData()
                    .ifPresent(
                        taskData -> virtualThreadsFactory.newThread(
                            getTaskManagerHandler(taskData)
                        ).start()
                    );
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Connection closed with exception");
        } catch (EndOfStreamException e) {
            System.out.println("Connection closed");
        }
    }

    /**
     * TaskData receiver and validator.
     * <br>
     * Receives data from clientManagerSockets and validate check
     * it's serialized TaskData
     * <br>
     * Sends to user error message in case of incorrect data
     *
     * @return optional TaskData or empty
     *
     * @throws EndOfStreamException throws up this exception
     *     for breaking while-live loop in run method
     */
    private Optional<TaskData> receiveTaskData() throws EndOfStreamException {
        try {
            return Optional.of(BasicTcpSocketOperations.receiveJsonObject(
                clientManagerSocket, TaskData.class
            ));
        } catch (ParsingException parsingException) {
            System.err.println("Parsing error: " + parsingException.getMessage());
            try {
                BasicTcpSocketOperations.sendJsonObject(
                    clientManagerSocket, ErrorMessages.taskDataParsingError
                );
            } catch (IOException e) {
                System.err.println(
                    "Error to send parsing error message to client " +
                    clientManagerSocket.getRemoteSocketAddress()
                );
            }
        } catch (IOException ignored) {}
        return Optional.empty();
    }

    /**
     * Runnable wrapper creator for processing TaskData.
     *
     * @param taskData validated task data
     *
     * @return function for processing task data and sending corresponding messages to client
     */
    private Runnable getTaskManagerHandler(TaskData taskData) {
        return () -> {
            try {
                Optional<TaskResult> optionalTaskResult = new TaskManager(
                    taskData, workersPerTask,
                    multicastServerPort, clientManagerSocket.getPort()
                ).processTask();
                optionalTaskResult.ifPresent(taskResult -> {
                    try {
                        BasicTcpSocketOperations.sendJsonObject(
                            clientManagerSocket, taskResult
                        );
                    } catch (IOException e) {
                        System.err.println(
                            "Error to send result to client "
                            + clientManagerSocket.getRemoteSocketAddress()
                        );
                    }
                });
            } catch (WorkerNotFoundException notFoundException) {
                try {
                    BasicTcpSocketOperations.sendJsonObject(
                        clientManagerSocket, ErrorMessages.workerNotFoundMessage
                    );
                } catch (IOException ignored) {}
                System.err.println("Couldn't find calculation node");
            } catch (InternalWorkerErrorException workerErrorException) {
                try {
                    BasicTcpSocketOperations.sendJsonObject(
                        clientManagerSocket, ErrorMessages.workerInternalErrorMessage
                    );
                } catch (IOException ignored) {}
                System.out.println("Couldn't find new calculation node");
            }
        };
    }
}
