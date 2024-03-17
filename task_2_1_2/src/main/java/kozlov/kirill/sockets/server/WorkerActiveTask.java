package kozlov.kirill.sockets.server;

import java.net.Socket;
import kozlov.kirill.sockets.data.TaskData;

public record WorkerActiveTask(TaskData taskData, Socket workerSocket) {}
