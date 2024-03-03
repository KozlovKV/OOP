package kozlov.kirill.sockets;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Server's gateway.
 * <br>
 * Listens to server socket and creates connection for clients
 */
public class Gateway implements Runnable {
    public static final int GATEWAY_PORT = 8001; // TODO: реализовать пул шлюзов
    private final int SERVER_SOCKET_BACKLOG = 5;
    private ServerSocket serverSocket = null;

    public Gateway() {
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
        try {
            while (true) {
                Socket connectionSocket = serverSocket.accept();
                new Thread(new Manager(connectionSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Server closed");
        }
    }
}
