package kozlov.kirill.sockets;

import kozlov.kirill.sockets.multicast.MulticastManager;
import kozlov.kirill.sockets.multicast.MulticastUtilsFactory;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server's gateway.
 * <br>
 * Listens to server socket and creates connection for clients
 */
public class Gateway implements Runnable {
    public static final int BROADCAST_PORT = 8000;
    public static final int FIRST_SERVER_PORT = 8000;
    private static final int SERVER_SOCKET_BACKLOG = 100;
    private int connectionsToDie = -1;
    private ServerSocket serverSocket = null;
    private ExecutorService workersThreadPool;

    public Gateway(int port, int connectionsToDie, int workersCount) {
        this.connectionsToDie = connectionsToDie;
        createServerSocket(port);
        MulticastManager multicastManager = new MulticastManager("230.0.0.0", port);
        workersThreadPool = Executors.newFixedThreadPool(workersCount);
        for (int i = 0; i < workersCount; ++i) {
            Worker worker = new Worker(
                    port + i + 1, multicastManager
            );
            workersThreadPool.submit(worker);
        }
    }

    private void createServerSocket(int port) {
        try {
            serverSocket = new ServerSocket(port, SERVER_SOCKET_BACKLOG);
        } catch (IOException e) {
            System.err.println("Failed to create gateway on thread " +
                    Thread.currentThread().getName());
            throw new RuntimeException(e);
        }
    }

    /**
     * Main server's infinite-loop.
     * <br>
     * Accepts connection from clients and creates Managers for working with client's data in other thread
     */
    public void run() {
        int establishedConnections = 0;
        try {
            do {
                Socket connectionSocket = serverSocket.accept();
                System.out.println("Connection to gateway from " + connectionSocket.getRemoteSocketAddress());
                new Thread(new Manager(
                        connectionSocket),
                        "Manager for " + connectionSocket.getRemoteSocketAddress()
                ).start(); // TODO: перейти на виртуальные потоки
                establishedConnections++;
            } while (connectionsToDie == -1 || establishedConnections < connectionsToDie);
        } catch (IOException ignored) {}
        try {
            serverSocket.close();
            workersThreadPool.shutdown();
        } catch (Exception ignored) {}
        System.out.println("Server closed");
    }
}
