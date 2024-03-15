package kozlov.kirill.sockets;

import kozlov.kirill.sockets.data.TaskData;

import java.net.Socket;

public record WorkerActiveTask(TaskData taskData, Socket workerSocket) {}
