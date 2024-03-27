package kozlov.kirill.sockets.multicast;

import java.net.DatagramPacket;

/**
 * Interface for handlers of multicast requests.
 */
public interface MulticastHandler {
    /**
     * Multicast response handler.
     * <br>
     * This method is responsible for handling UDP packets from multicast groups
     *
     * @param datagramPacket packet from request
     */
    void handle(DatagramPacket datagramPacket);
}