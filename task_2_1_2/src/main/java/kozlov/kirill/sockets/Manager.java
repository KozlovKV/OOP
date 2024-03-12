package kozlov.kirill.sockets;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.multicast.MulticastUtils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Manager for processing client's tasks.
 * <br>
 * Implemented for thread-usage.
 */
public class Manager implements Runnable {
    final private int multicastServerPort;
    final private Socket clientManagerSocket;
    final private int workersPerTask;

    public Manager(Socket clientManagerSocket, int multicastServerPort, int workersPerTask) {
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
                // TODO: добавить очередь ожидающих запросов
                new Thread(new MasterWorker(taskData)).start(); // TODO: перейти на виртуальные потоки
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

    private class MasterWorker implements Runnable {
        final private TaskData taskData;

        public MasterWorker(TaskData taskData) {
            this.taskData = taskData;
        }

        public void run() {
            ArrayList<Socket> workerSockets = getWorkersSockets(splitTaskData(taskData));
            if (workerSockets == null)
                return;
            for (var workerSocket : workerSockets) {
                try {
                    TaskResult taskResult =
                            BasicTCPSocketOperations.receiveJSONObject(
                                    workerSocket, TaskResult.class
                            );
                    if (taskResult.result()) {
                        BasicTCPSocketOperations.sendJSONObject(
                                clientManagerSocket, taskResult
                        );
                        return;
                    }
                } catch (IOException e) {
                    System.err.println("Error in communication with worker");
                }
            }
            try {
                BasicTCPSocketOperations.sendJSONObject(
                        clientManagerSocket, new TaskResult(false)
                );
            } catch (IOException e) {
                System.err.println("Error in communication with worker");
            }
        }

        private ArrayList<TaskData> splitTaskData(TaskData taskData) {
            final int numbersSize = taskData.numbers().size();
            final int subtaskNumbersSize = numbersSize / workersPerTask + 1;
            ArrayList<TaskData> tasks = new ArrayList<>();
            for (int i = 0; i < workersPerTask; ++i) {
                final int threadNumbersStartIndex = i * subtaskNumbersSize;
                final int threadNumbersEndIndex =
                        Math.min(threadNumbersStartIndex + subtaskNumbersSize, numbersSize);
                ArrayList<Integer> sublist = new ArrayList<>();
                for (int j = threadNumbersStartIndex; j < threadNumbersEndIndex; ++j)
                    sublist.add(taskData.numbers().get(j));
                tasks.add(new TaskData(sublist));
            }
            return tasks;
        }

        private Socket getNewWorkerSocket(TaskData taskData) {
            return null;
        }

        private ArrayList<Socket> getWorkersSockets(ArrayList<TaskData> tasks) {
            ArrayList<Socket> workerSockets = new ArrayList<>();
            for (var task : tasks) {
                if (task.numbers().isEmpty())
                    continue;
                Socket workerSocket = MulticastUtils.getClientSocketByMulticastResponse(
                        clientManagerSocket.getPort(), multicastServerPort
                );
                if (workerSocket == null) {
                    try {
                        BasicTCPSocketOperations.sendJSONObject(
                                clientManagerSocket, new ErrorMessage("Server couldn't find calculation node")
                        );
                    } catch (IOException ignored) {
                    }
                    return null;
                }
                System.out.println("Chosen worker " + workerSocket.getRemoteSocketAddress());
                try {
                    BasicTCPSocketOperations.sendJSONObject(workerSocket, task);
                } catch (IOException e) {
                    System.err.println("Error in communication with worker");

                }
                workerSockets.add(workerSocket);
            }
            return workerSockets;
        }
    }
}
