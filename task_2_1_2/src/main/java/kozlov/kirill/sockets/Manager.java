package kozlov.kirill.sockets;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import kozlov.kirill.primes.ParallelStreamsUnprimeChecker;
import kozlov.kirill.primes.UnprimeChecker;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;

import java.io.*;
import java.net.Socket;

/**
 * Manager for processing client's tasks.
 * <br>
 * Implemented for thread-usage.
 */
public class Manager implements Runnable {
    final private Socket socket;

    public Manager(Socket socket) {
        this.socket = socket;
    }

    /**
     * Main processing of client's requests.
     * <br>
     * Whereas socket is opened reads requests for it and send results of calculation
     */
    public void run() {
        while (true) {
            TaskData taskData = receiveTaskData();
            if (socket.isClosed()) {
                break;
            }
            if (taskData == null) {
                System.err.println("Incorrect data");
                continue;
            }
            TaskResult taskResult = processTask(taskData);
            sendTaskResult(taskResult);
        }
        System.out.println("Connection closed");
    }

    private TaskData receiveTaskData() {
        try {
            return BasicTCPSocketOperations.receiveJSONObject(
                    socket, TaskData.class
            );
        } catch (UnrecognizedPropertyException e) {
            System.err.println("Parsing error: " + e.getMessage());
        } catch (IOException ignored) {}
        return null;
    }

    private void sendTaskResult(TaskResult taskResult) {
        try {
            BasicTCPSocketOperations.sendJSONObject(socket, taskResult);
        } catch (IOException ignored) {}
    }

    private TaskResult processTask(TaskData taskData) {
        UnprimeChecker checker = new ParallelStreamsUnprimeChecker();
        checker.setNumbers(taskData.numbers());
        return new TaskResult(checker.isAnyUnprime());
    }
}
