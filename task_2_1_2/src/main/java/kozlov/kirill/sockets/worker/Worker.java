package kozlov.kirill.sockets.worker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import kozlov.kirill.primes.SimpleUnprimeChecker;
import kozlov.kirill.primes.UnprimeChecker;
import kozlov.kirill.sockets.BasicTCPSocketOperations;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.exceptions.EndOfStreamException;
import kozlov.kirill.sockets.exceptions.ParsingException;
import kozlov.kirill.sockets.exceptions.WorkerCreationException;
import kozlov.kirill.sockets.multicast.DatagramUtils;
import kozlov.kirill.sockets.multicast.MulticastHandler;
import kozlov.kirill.sockets.multicast.MulticastManager;

/**
 * Calculation node.
 */
public class Worker implements Runnable {
    public static final int WORKER_SOCKET_TIMEOUT = 1000;

    private final Integer workerPort;
    private final AtomicBoolean isFree = new AtomicBoolean(true);
    private boolean shouldBeClosed = false;

    private ServerSocket workerServerSocket = null;
    private MulticastSocket multicastSocket = null;

    /**
     * Worker constructor.
     * <br>
     * Creates worker and register multicast handler to make it available
     * using broadcast request
     * <br>
     *
     * @param workerPort port for worker TCP server socket
     * @param multicastManager manager for registering multicast handler
     *
     * @throws WorkerCreationException thrown in case of any error during worker creation
     */
    public Worker(
        Integer workerPort, MulticastManager multicastManager
    ) throws WorkerCreationException {
        this.workerPort = workerPort;
        try {
            workerServerSocket = new ServerSocket(workerPort);
            workerServerSocket.setSoTimeout(WORKER_SOCKET_TIMEOUT);
            multicastSocket = multicastManager.registerMulticastHandler(
                    getWorkerMulticastHandler()
            );
        } catch (IOException e) {
            throw new WorkerCreationException(e);
        }
    }

    /**
     * Multicast handler creator.
     *
     * @return created MulticastHandler lambda function for
     *     sending response to specified in packet port
     */
    private MulticastHandler getWorkerMulticastHandler() {
        return (DatagramPacket packet) -> {
            if (!isFree.get()) {
                return;
            }
            try {
                String inputStr = DatagramUtils.extractContentFromPacket(packet);
                int ackPort = -1;
                try {
                    ackPort = Integer.parseInt(
                        inputStr, 0, inputStr.length() - 1, 10
                    );
                } catch (NumberFormatException e) {
                    System.err.println("Incorrect request port");
                    return;
                }
                DatagramSocket socket = new DatagramSocket(workerPort);
                DatagramPacket responsePacket = DatagramUtils.createPacket(
                    workerPort + "\n", packet.getAddress(), ackPort
                );
                socket.send(responsePacket);
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to handle response for multicast request");
            }
        };
    }

    /**
     * Thread runnable function with infinite loop for calculations.
     * <br>
     * Accepts connection, blocks for broadcast requests and tries to resolve
     * task from created connection
     * <br>
     * Cycle can be break by interruption or shouldBeClosed flag set to true
     */
    public void run() {
        Socket connectionSocket = null;
        try {
            while (!shouldBeClosed) {
                try {
                    connectionSocket = workerServerSocket.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                isFree.set(false);
                System.out.println(
                    "Connection to worker from " + connectionSocket.getRemoteSocketAddress()
                );
                resolveTask(connectionSocket);
                isFree.set(true);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Worker was interrupted while counting task");
        }
        try {
            workerServerSocket.close();
            multicastSocket.close();
            if (connectionSocket != null && !connectionSocket.isClosed()) {
                connectionSocket.close();
            }
        } catch (Exception ignored) {}
        System.out.println("Worker killed");
    }

    /**
     * shouldBeClosed flag switcher on.
     */
    public void setCloseFlag() {
        shouldBeClosed = true;
    }

    /**
     * Task resolver.
     *
     * @param socket socket for getting data and sending result
     *
     * @throws InterruptedException thrown in case when calculation counting was interrupted
     */
    private void resolveTask(Socket socket) throws InterruptedException {
        TaskData taskData = new TaskData(new ArrayList<>());
        try {
            taskData = BasicTCPSocketOperations.receiveJSONObject(
                    socket, TaskData.class
            );
        } catch (IOException | EndOfStreamException socketException) {
            System.err.println(
                "Error to get data in worker on port " + workerPort +
                " from " + socket.getRemoteSocketAddress()
            );
            return;
        } catch (ParsingException parsingException) {
            System.err.println(
                "Error to parse data in worker on port " + workerPort +
                " from " + socket.getRemoteSocketAddress()
            );
        }
        UnprimeChecker checker = new SimpleUnprimeChecker().setNumbers(taskData.numbers());
        TaskResult taskResult = new TaskResult(checker.isAnyUnprime());
        try {
            BasicTCPSocketOperations.sendJSONObject(socket, taskResult);
        } catch (IOException socketException) {
            System.err.println(
                "Error to send calculation result from worker on port " + workerPort +
                " to " + socket.getRemoteSocketAddress()
            );
        }
    }
}
