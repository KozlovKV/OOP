package kozlov.kirill.sockets;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Gateway implements Runnable {
    public static final int GATEWAY_PORT = 8001;
    private final int SERVER_SOCKET_BACKLOG = 5;
    private ServerSocket serverSocket = null;

    public Gateway() {
        try {
            serverSocket = new ServerSocket(GATEWAY_PORT, SERVER_SOCKET_BACKLOG);
        } catch (IOException ignored) {}
    }

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
