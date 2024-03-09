package kozlov.kirill.sockets;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import kozlov.kirill.sockets.data.ErrorMessage;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Manager for processing client's tasks.
 * <br>
 * Implemented for thread-usage.
 */
public class Manager implements Runnable {
    final private Socket clientManagerSocket;

    public Manager(Socket clientManagerSocket) {
        this.clientManagerSocket = clientManagerSocket;
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
                new Thread(
                        getRunnableManagerWorkerCommunicationFunction(taskData)
                ).start(); // TODO: перейти на виртуальные потоки
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

    public Runnable getRunnableManagerWorkerCommunicationFunction(TaskData taskData) {
        return () -> {
            Socket workerSocket = getWorkerSocket(clientManagerSocket.getPort());
            if (workerSocket == null) {
                try {
                    BasicTCPSocketOperations.sendJSONObject(
                            clientManagerSocket, new ErrorMessage("Server couldn't find calculation node")
                    );
                } catch (IOException ignored) {}
                return;
            }
            System.out.println("Chosen worker " + workerSocket.getRemoteSocketAddress());
            try {
                BasicTCPSocketOperations.sendJSONObject(workerSocket, taskData);
                BasicTCPSocketOperations.sendJSONObject(
                        clientManagerSocket,
                        BasicTCPSocketOperations.receiveJSONObject(workerSocket, TaskResult.class)
                );
            } catch (IOException e) {
                System.err.println("Error in communication with worker");
            }
        };
    }

    static private final int BROADCAST_RECEIVING_TIMEOUT = 1000;
    static private final int BROADCAST_RECEIVING_ATTEMPTS = 5;
    private Socket getWorkerSocket(int broadcastPort) {
        try (DatagramSocket socket = new DatagramSocket(broadcastPort)) {
            socket.setBroadcast(true);
            byte[] data = (broadcastPort + "\n").getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), Gateway.FIRST_SERVER_PORT);
            socket.setSoTimeout(BROADCAST_RECEIVING_TIMEOUT);
            for (int i = 0;; ++i) {
                try {
                    socket.send(packet);
                    socket.receive(packet);
                    break;
                } catch (SocketTimeoutException e) {
                    System.err.println("There are no available workers.");
                    if (i >= BROADCAST_RECEIVING_ATTEMPTS) {
                        System.err.println("Attempts number exceeded.");
                        return null;
                    }
                    System.err.println("Next try.");
                }
            }
            System.out.println("Packet got from " + packet.getSocketAddress());
            return new Socket(packet.getAddress(), packet.getPort());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
