package kozlov.kirill.sockets.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Callable;
import kozlov.kirill.sockets.BasicTCPSocketOperations;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.data.utils.TaskDataUtils;
import kozlov.kirill.sockets.exceptions.InternalWorkerErrorException;
import kozlov.kirill.sockets.exceptions.WorkerNotFoundException;
import kozlov.kirill.sockets.multicast.MulticastUtils;

public class TaskManager implements Callable<Optional<TaskResult>> {
    final private TaskData taskData;
    final private int workersPerTask;
    final private int managerPort;
    final private int multicastServerPort;

    public TaskManager(
        TaskData taskData, int workersPerTask,
        int multicastServerPort, int managerPort
    ) {
        this.taskData = taskData;
        this.workersPerTask = workersPerTask;
        this.multicastServerPort = multicastServerPort;
        this.managerPort = managerPort;
    }

    public Optional<TaskResult> call()
    throws WorkerNotFoundException, InternalWorkerErrorException {
        System.out.println("Start task manager on " + managerPort);
        var tasks = TaskDataUtils.splitTaskData(taskData, workersPerTask);
        ArrayDeque<WorkerActiveTask> activeWorkers = getActiveWorkers(tasks);
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
                    System.err.println("Error in active node. Trying to find new node for subtask...");
                    getNewWorkerSocket(activeTask.taskData())
                            .ifPresent(activeWorkers::add);
                } catch (WorkerNotFoundException notFoundException) {
                    throw new InternalWorkerErrorException();
                }
            }
        }
        return Optional.of(taskResult);
    }

    private Optional<WorkerActiveTask> getNewWorkerSocket(
            TaskData task
    ) throws WorkerNotFoundException {
        if (task.numbers().isEmpty())
            return Optional.empty();
        Socket workerSocket = MulticastUtils.getClientSocketByMulticastResponse(
                managerPort, multicastServerPort
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
