package kozlov.kirill.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server's gateway.
 * <br>
 * Listens to server socket and creates connection for clients
 */
public class Gateway implements Runnable {
    public static final int BROADCAST_PORT = 8000;
    public static final int FIRST_SERVER_PORT = 8001;
    private static final int SERVER_SOCKET_BACKLOG = 100;
    private int connectionsToDie = -1;
    private ServerSocket serverSocket = null;

    public Gateway(int port) {
        createServerSocket(port);
    }

    public Gateway(int port, int connectionsToDie) {
        this.connectionsToDie = connectionsToDie;
        createServerSocket(port);
        new Thread(new UDPReceiver(port), "UDP receiver for gateway on port " + port).start();
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
                new Thread(new Manager(
                        connectionSocket),
                        "Manager for " + connectionSocket.getRemoteSocketAddress()
                ).start();
                establishedConnections++;
            } while (connectionsToDie == -1 || establishedConnections < connectionsToDie);
        } catch (IOException ignored) {}
        try {
            serverSocket.close();
        } catch (Exception ignored) {}
        System.out.println("Server closed");
    }
}
