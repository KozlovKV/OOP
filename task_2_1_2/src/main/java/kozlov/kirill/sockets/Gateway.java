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
//        new Thread(() -> {
//            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
//            byteBuffer.putInt(serverSocket.getLocalPort());
//            byte[] bytesPort = byteBuffer.array();
//            System.out.println(serverSocket.getLocalPort());
//            System.out.println(bytesPort);
//            byte[] data = new byte[1024];
//            DatagramPacket packet = new DatagramPacket(data, data.length);
//            try (
//                DatagramSocket signalSocket = new DatagramSocket(GATEWAY_PORT)
//            ) {
//                while (true) {
//                    signalSocket.receive(packet);
//                    System.out.println("GATEWAY got request from " + packet.getAddress() + ":" + packet.getPort());
//                    DatagramPacket responsePacket = new DatagramPacket(bytesPort, bytesPort.length, packet.getAddress(), packet.getPort());
//                    signalSocket.send(responsePacket);
//                }
//            } catch (IOException ignored) {}
//        }).start();

    }

    public void run() {
        try {
            while (true) {
                Socket connectionSocket = serverSocket.accept();
                new Thread(new Manager(connectionSocket)).start();
            }
        } catch (IOException ignored) {}
    }
}
