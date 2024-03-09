package kozlov.kirill.sockets;

import kozlov.kirill.primes.ParallelStreamsUnprimeChecker;
import kozlov.kirill.primes.UnprimeChecker;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;
import kozlov.kirill.sockets.multicast.MulticastHandler;
import kozlov.kirill.sockets.multicast.MulticastManager;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class Worker implements Runnable {
    final private Integer workerPort;
    private AtomicBoolean isFree = new AtomicBoolean(true);

    private ServerSocket serverSocket = null;
    private MulticastSocket multicastSocket = null;

    public Worker(Integer workerPort, MulticastManager multicastManager) {
        this.workerPort = workerPort;
        try {
            serverSocket = new ServerSocket(workerPort);
        } catch (IOException e) {
            System.err.println("Failed to create server worker on port " + workerPort);
        }
        multicastSocket = multicastManager.registerMulticastHandler(
                getWorkerMulticastHandler()
        );
    }

    public MulticastHandler getWorkerMulticastHandler() {
        return (DatagramPacket packet) -> {
            if (!isFree.get()) {
                System.err.println("Worker on " + workerPort + " is unavailable for new task");
                return;
            }
            try {
                String inputStr = new String(packet.getData(), packet.getOffset(), packet.getLength());
                int ackPort = -1;
                try {
                    ackPort = Integer.parseInt(inputStr, 0, inputStr.length()-1, 10);
                } catch (NumberFormatException e) {
                    System.err.println("Incorrect request port");
                    return;
                }
                DatagramSocket socket = new DatagramSocket(workerPort);
                byte[] buf = (workerPort + "\n").getBytes(StandardCharsets.UTF_8);
                DatagramPacket responsePacket = new DatagramPacket(buf, buf.length, packet.getAddress(), ackPort);
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
            while (true) {
                Socket connectionSocket = serverSocket.accept();
                isFree.set(false);
                System.out.println("Connection to worker from " + connectionSocket.getRemoteSocketAddress());
                resolveTask(connectionSocket);
                connectionSocket.close();
                Thread.sleep(7000);
                isFree.set(true);
            }
        } catch (IOException | InterruptedException broke) {}
        try {
            serverSocket.close();
            multicastSocket.close();
        } catch (Exception ignored) {}
        System.out.println("Worker killed");
    }

    void resolveTask(Socket socket) {
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
