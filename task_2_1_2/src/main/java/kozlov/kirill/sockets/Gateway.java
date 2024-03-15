package kozlov.kirill.sockets;

import kozlov.kirill.sockets.multicast.MulticastManager;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Server's gateway.
 * <br>
 * Listens to server socket and creates connection for clients
 */
public class Gateway implements Runnable {
    public static final int FIRST_SERVER_PORT = 8000;
    private static final int SERVER_SOCKET_BACKLOG = 100;
    private static final int GATEWAY_TIMEOUT = 1000;
    private int connectionsToDie = -1;
    private final int port;
    private final int workersPerOneTask;
    private ServerSocket serverSocket = null;
    private final ArrayList<Worker> workers;

    public Gateway(int port, int connectionsToDie, int workersCount, int workersPerOneTask) {
        this.port = port;
        this.connectionsToDie = connectionsToDie;
        this.workersPerOneTask = workersPerOneTask;

        createServerSocket(port);
        MulticastManager multicastManager = new MulticastManager("230.0.0.0", port);
        workers = Worker.launchAndGetWorkers(port + 1, workersCount, multicastManager);
    }

    private void createServerSocket(int port) {
        try {
            serverSocket = new ServerSocket(port, SERVER_SOCKET_BACKLOG);
            serverSocket.setSoTimeout(GATEWAY_TIMEOUT);
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
        ArrayList<Thread> managerThreads = new ArrayList<>();
        try {
            do {
                managerThreads = new ArrayList<>(
                        managerThreads.stream().filter(Thread::isAlive).toList()
                );
                Socket connectionSocket = null;
                try {
                    connectionSocket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                System.out.println("Connection to gateway from " + connectionSocket.getRemoteSocketAddress());
                Thread managerThread = new Thread(
                        new Manager(connectionSocket, port, workersPerOneTask),
                        "Manager for " + connectionSocket.getRemoteSocketAddress()
                ); // TODO: перейти на виртуальные потоки
                managerThread.start();
                managerThreads.add(managerThread);
                establishedConnections++;
            } while (
                connectionsToDie == -1 ||
                !managerThreads.isEmpty() ||
                establishedConnections < connectionsToDie
            );
        } catch (IOException ignored) {}
        try {
            serverSocket.close();
        } catch (Exception ignored) {}
        System.out.println("Server closed");
        for (Worker worker : workers) {
            worker.setCloseFlag();
        }
    }
}
