package kozlov.kirill.sockets.server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

/**
 * Server's gateway.
 * <br>
 * Listens to server socket and creates connection for clients
 */
public class Gateway implements Runnable {
    private static final int SERVER_SOCKET_BACKLOG = 100;
    private static final int GATEWAY_TIMEOUT = 1000;
    private int connectionsToDie = -1;
    private final int port;
    private final int multicastPort;
    private final int workersPerOneTask;
    private ServerSocket serverSocket = null;

    public Gateway(int port, int multicastPort, int connectionsToDie, int workersPerOneTask) {
        this.port = port;
        this.multicastPort = multicastPort;
        this.connectionsToDie = connectionsToDie;
        this.workersPerOneTask = workersPerOneTask;
        createServerSocket(port);
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
                ThreadFactory factory = Thread.ofVirtual().factory();
                Thread managerThread = factory.newThread(new ClientManager(connectionSocket, multicastPort, workersPerOneTask));
//                Thread managerThread = new Thread(
//                        new ClientManager(connectionSocket, multicastPort, workersPerOneTask),
//                        "Manager for " + connectionSocket.getRemoteSocketAddress()
//                ); // TODO: перейти на виртуальные потоки
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
    }
}
