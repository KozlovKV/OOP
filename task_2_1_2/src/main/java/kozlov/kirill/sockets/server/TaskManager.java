package kozlov.kirill.sockets.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;

import kozlov.kirill.sockets.BasicTcpSocketOperations;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.data.utils.TaskDataUtils;
import kozlov.kirill.sockets.exceptions.EndOfStreamException;
import kozlov.kirill.sockets.exceptions.InternalWorkerErrorException;
import kozlov.kirill.sockets.exceptions.ParsingException;
import kozlov.kirill.sockets.exceptions.WorkerNotFoundException;
import kozlov.kirill.sockets.multicast.MulticastUtils;

/**
 * Class for communication manager with workers.
 */
public class TaskManager {
    final private TaskData taskData;
    final private int workersPerTask;
    final private int managerPort;
    final private int multicastServerPort;

    /**
     * TaskManager constructor.
     *
     * @param taskData data for processing
     * @param workersPerTask count of workers need to process task
     * @param multicastServerPort port for founding workers
     * @param managerPort client's socket port from ClientManager
     */
    public TaskManager(
        TaskData taskData, int workersPerTask,
        int multicastServerPort, int managerPort
    ) {
        this.taskData = taskData;
        this.workersPerTask = workersPerTask;
        this.multicastServerPort = multicastServerPort;
        this.managerPort = managerPort;
    }

    private record WorkerActiveTask(TaskData taskData, Socket workerSocket) {}

    /**
     * Task processor.
     *
     * @return result of concurrent calculation in found workers. There are no ways to
     *     get Optional.empty() because any error will produce exceptions described below
     *
     * @throws WorkerNotFoundException thrown in case when there are not enough free
     *     workers for calculation
     * @throws InternalWorkerErrorException thrown in case when some error occurred in worker and
     *     after that there are not enough free workers for calculation
     */
    public Optional<TaskResult> processTask(
    ) throws WorkerNotFoundException, InternalWorkerErrorException {
        System.out.println("Start task manager on " + managerPort);
        var tasks = TaskDataUtils.splitTaskData(taskData, workersPerTask);
        ArrayDeque<WorkerActiveTask> activeWorkers = getActiveWorkers(tasks);
        TaskResult taskResult = new TaskResult(false);
        while (!activeWorkers.isEmpty()) {
            WorkerActiveTask activeTask = activeWorkers.poll();
            TaskResult workerResult = null;
            try {
                workerResult =
                    BasicTcpSocketOperations.receiveJsonObject(
                        activeTask.workerSocket(), TaskResult.class
                    );
                if (workerResult.result()) {
                    taskResult = workerResult;
                    break;
                }
            } catch (IOException | ParsingException | EndOfStreamException e) {
                try {
                    System.err.println(
                        "Error in active node. Trying to find new node for subtask..."
                    );
                    getNewWorkerSocket(activeTask.taskData())
                            .ifPresent(activeWorkers::add);
                } catch (WorkerNotFoundException notFoundException) {
                    throw new InternalWorkerErrorException();
                }
            }
        }
        return Optional.of(taskResult);
    }

    /**
     * Workers finder.
     *
     * @param task data for found worker.
     *
     * @return found and blocked for this task worker or empty if task is empty
     *
     * @throws WorkerNotFoundException thrown in case when there is no available worker
     */
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
            BasicTcpSocketOperations.sendJsonObject(workerSocket, task);
        } catch (IOException e) {
            System.err.println("Error in communication with worker");

        }
        return Optional.of(new WorkerActiveTask(task, workerSocket));
    }

    /**
     * Active workers deque collector.
     *
     * @param tasks tasks for workers
     *
     * @return deque of WorkerActiveTasks
     *
     * @throws WorkerNotFoundException thrown in case when there are not enough free
     *      workers for not empty tasks
     */
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
