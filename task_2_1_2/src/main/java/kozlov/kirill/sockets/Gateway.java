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
    public static final int GATEWAY_PORT = 8001; // TODO: реализовать пул шлюзов
    private static final int SERVER_SOCKET_BACKLOG = 100;
    private int connectionsToDie = -1;
    private ServerSocket serverSocket = null;

    public Gateway() {
        try {
            serverSocket = new ServerSocket(GATEWAY_PORT, SERVER_SOCKET_BACKLOG);
        } catch (IOException ignored) {}
    }

    public Gateway(int connectionsToDie) {
        this.connectionsToDie = connectionsToDie;
        try {
            serverSocket = new ServerSocket(GATEWAY_PORT, SERVER_SOCKET_BACKLOG);
        } catch (IOException ignored) {}
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
