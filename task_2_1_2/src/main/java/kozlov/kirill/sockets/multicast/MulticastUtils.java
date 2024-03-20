package kozlov.kirill.sockets.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Static class with utils for multicast sockets.
 */
public final class MulticastUtils {
    private MulticastUtils() {}

    static private final int BROADCAST_RECEIVING_TIMEOUT = 1000;
    static private final int BROADCAST_RECEIVING_ATTEMPTS = 5;

    /**
     * Method for acquiring connection using like-ARP broadcast requesting.
     * <br>
     * Send broadcast request to specified port and tries to establish TCP socket connection
     * to address from response packet
     *
     * @param hostPort port for creation of broadcast socket
     * @param broadcastPort port for broadcast request
     *
     * @return created TCP socket or null if there are any trouble with creation
     */
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
