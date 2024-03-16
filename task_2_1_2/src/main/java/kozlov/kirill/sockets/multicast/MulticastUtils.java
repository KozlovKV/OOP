package kozlov.kirill.sockets.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

final public class MulticastUtils {
    static private int BUF_SZ = 1024;

    private MulticastUtils() {}

    static public Runnable getRunnableHandlerWrapper(MulticastSocket socket, MulticastHandler handler) {
        return () -> {
            while (true) {
                byte[] buf = new byte[BUF_SZ];
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(datagramPacket);
                    handler.handle(datagramPacket);
                } catch (IOException e) {
                    break;
                }
            }
            System.out.println("Handler interrupted");
        };
    }

    static private final int BROADCAST_RECEIVING_TIMEOUT = 1000;
    static private final int BROADCAST_RECEIVING_ATTEMPTS = 5;
    static public Socket getClientSocketByMulticastResponse(int hostPort, int broadcastPort) {
        try (DatagramSocket socket = new DatagramSocket(hostPort)) {
            socket.setBroadcast(true);
            DatagramPacket packet = DatagramUtils.createPacket(
                hostPort + "\n", InetAddress.getByName("255.255.255.255"), broadcastPort
            );
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
            System.out.println("Got response from multicast node " + packet.getSocketAddress());
            return new Socket(packet.getAddress(), packet.getPort());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
