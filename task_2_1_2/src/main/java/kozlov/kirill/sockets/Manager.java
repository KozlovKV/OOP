package kozlov.kirill.sockets;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.exceptions.WorkerNotFoundException;
import kozlov.kirill.sockets.multicast.MulticastUtils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;
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
            ArrayDeque<WorkerActiveTask> activeWorkers = new ArrayDeque<>();
            var tasks = splitTaskData(taskData);
            try {
                activeWorkers = getActiveWorkers(tasks);
            } catch (WorkerNotFoundException e) {
                try {
                    BasicTCPSocketOperations.sendJSONObject(
                            clientManagerSocket, new ErrorMessage("Server couldn't find calculation node")
                    );
                } catch (IOException ignored) {}
                System.err.println("Couldn't find calculation node");
                return;
            }
            TaskResult taskResult = new TaskResult(false);
            while (!activeWorkers.isEmpty()) {
                WorkerActiveTask activeTask = activeWorkers.poll();
                TaskResult workerResult = null;
                try {
                    workerResult =
                            BasicTCPSocketOperations.receiveJSONObject(
                                    activeTask.workerSocket(), TaskResult.class
                            );
                    if (workerResult.result()) {
                        taskResult = workerResult;
                        break;
                    }
                } catch (IOException e) {
                    try {
                        System.out.println("Error in active node. Trying to find new node for subtask...");
                        getNewWorkerSocket(activeTask.taskData())
                                .ifPresent(activeWorkers::add);
                    } catch (WorkerNotFoundException notFoundException) {
                        try {
                            BasicTCPSocketOperations.sendJSONObject(
                                    clientManagerSocket, new ErrorMessage("Server got error while calculated and cannot restart process")
                            );
                        } catch (IOException ignored) {}
                        System.out.println("Couldn't find new calculation node");
                        return;
                    }
                }
            }
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

        private Optional<WorkerActiveTask> getNewWorkerSocket(
            TaskData task
        ) throws WorkerNotFoundException {
            if (task.numbers().isEmpty())
                return Optional.empty();
            Socket workerSocket = MulticastUtils.getClientSocketByMulticastResponse(
                    clientManagerSocket.getPort(), multicastServerPort
            );
            if (workerSocket == null) {
                throw new WorkerNotFoundException();
            }
            System.out.println("Chosen worker " + workerSocket.getRemoteSocketAddress());
            try {
                BasicTCPSocketOperations.sendJSONObject(workerSocket, task);
            } catch (IOException e) {
                System.err.println("Error in communication with worker");

            }
            return Optional.of(new WorkerActiveTask(task, workerSocket));
        }

        private ArrayDeque<WorkerActiveTask> getActiveWorkers(
            ArrayList<TaskData> tasks
        ) throws WorkerNotFoundException {
            ArrayDeque<WorkerActiveTask> workers = new ArrayDeque<>();
            for (var task : tasks) {
                getNewWorkerSocket(task).ifPresent(workers::add);
            }
            return workers;
        }
    }
}
