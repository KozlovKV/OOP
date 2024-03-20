package kozlov.kirill.sockets.worker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import kozlov.kirill.primes.SimpleUnprimeChecker;
import kozlov.kirill.primes.UnprimeChecker;
import kozlov.kirill.sockets.BasicTCPSocketOperations;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.exceptions.EndOfStreamException;
import kozlov.kirill.sockets.exceptions.ParsingException;
import kozlov.kirill.sockets.multicast.DatagramUtils;
import kozlov.kirill.sockets.multicast.MulticastHandler;
import kozlov.kirill.sockets.multicast.MulticastManager;

public class Worker implements Runnable {
    static final public int WORKER_SOCKET_TIMEOUT = 1000;

    final private Integer workerPort;
    private AtomicBoolean isFree = new AtomicBoolean(true);
    private boolean createdSuccessfully = false;
    private boolean shouldBeClosed = false;

    private ServerSocket workerServerSocket = null;
    private MulticastSocket multicastSocket = null;

    public Worker(Integer workerPort, MulticastManager multicastManager) {
        this.workerPort = workerPort;
        try {
            workerServerSocket = new ServerSocket(workerPort);
            workerServerSocket.setSoTimeout(WORKER_SOCKET_TIMEOUT);
            multicastSocket = multicastManager.registerMulticastHandler(
                    getWorkerMulticastHandler()
            );
        } catch (IOException e) {
            System.err.println("Failed to create worker on port " + workerPort);
            return;
        }
        createdSuccessfully = true;
    }

    public boolean isCreatedSuccessfully() {
        return createdSuccessfully;
    }

    private MulticastHandler getWorkerMulticastHandler() {
        return (DatagramPacket packet) -> {
            if (!isFree.get()) {
                return;
            }
            try {
                String inputStr = DatagramUtils.extractContentFromPacket(packet);
                int ackPort = -1;
                try {
                    ackPort = Integer.parseInt(inputStr, 0, inputStr.length()-1, 10);
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
                System.err.println("Error...");
            }
        };
    }

    /**
     * Main processing of client's requests.
     * <br>
     * Whereas socket is opened reads requests for it and send results of calculation
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
                System.out.println("Connection to worker from " + connectionSocket.getRemoteSocketAddress());
                resolveTask(connectionSocket);
                isFree.set(true);
            }
        } catch (IOException ignored) {
        } catch (InterruptedException e) {
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

    public void setCloseFlag() {
        shouldBeClosed = true;
    }

    private void resolveTask(Socket socket) throws InterruptedException {
        TaskData taskData = null;
        try {
            taskData = BasicTCPSocketOperations.receiveJSONObject(
                    socket, TaskData.class
            );
        } catch (IOException ignored) {
        } catch (ParsingException parsingException) {
            // TODO: рассмотреть это место как возможность убивать воркер в тестах
        } catch (EndOfStreamException eofException) {}
        UnprimeChecker checker = new SimpleUnprimeChecker().setNumbers(taskData.numbers());
        TaskResult taskResult = new TaskResult(checker.isAnyUnprime());
        try {
            BasicTCPSocketOperations.sendJSONObject(socket, taskResult);
        } catch (IOException ignored) {}
    }
}
