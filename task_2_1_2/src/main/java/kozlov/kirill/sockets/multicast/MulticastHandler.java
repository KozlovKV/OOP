package kozlov.kirill.sockets.multicast;

import java.net.DatagramPacket;

public interface MulticastHandler {
    void handle(DatagramPacket datagramPacket);
}