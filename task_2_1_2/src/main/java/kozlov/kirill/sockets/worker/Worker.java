package kozlov.kirill.sockets.worker;

import kozlov.kirill.primes.ParallelStreamsUnprimeChecker;
import kozlov.kirill.primes.UnprimeChecker;
import kozlov.kirill.sockets.BasicTCPSocketOperations;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.multicast.DatagramUtils;
import kozlov.kirill.sockets.multicast.MulticastHandler;
import kozlov.kirill.sockets.multicast.MulticastManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Worker implements Runnable {
    static final public int WORKER_SOCKET_TIMEOUT = 1000;

    final private Integer workerPort;
    private AtomicBoolean isFree = new AtomicBoolean(true);
    private boolean createdSuccessfully = false;
    private boolean shouldBeClosed = false;

    private ServerSocket serverSocket = null;
    private MulticastSocket multicastSocket = null;

    public Worker(Integer workerPort, MulticastManager multicastManager) {
        this.workerPort = workerPort;
        try {
            serverSocket = new ServerSocket(workerPort);
            serverSocket.setSoTimeout(WORKER_SOCKET_TIMEOUT);
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
        try {
            while (!shouldBeClosed) {
                Socket connectionSocket = null;
                try {
                    connectionSocket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                isFree.set(false);
                System.out.println("Connection to worker from " + connectionSocket.getRemoteSocketAddress());
                resolveTask(connectionSocket);
                isFree.set(true);
            }
        } catch (IOException broke) {}
        try {
            serverSocket.close();
            multicastSocket.close();
        } catch (Exception ignored) {}
        System.out.println("Worker killed");
    }

    public void setCloseFlag() {
        shouldBeClosed = true;
    }

    private void resolveTask(Socket socket) {
        TaskData taskData = null;
        try {
            taskData = BasicTCPSocketOperations.receiveJSONObject(
                    socket, TaskData.class
            );
        } catch (IOException ignored) {} // Manager has already checked validness
        UnprimeChecker checker = new ParallelStreamsUnprimeChecker().setNumbers(taskData.numbers());
        TaskResult taskResult = new TaskResult(checker.isAnyUnprime());
        try {
            BasicTCPSocketOperations.sendJSONObject(socket, taskResult);
        } catch (IOException ignored) {}
    }
}
