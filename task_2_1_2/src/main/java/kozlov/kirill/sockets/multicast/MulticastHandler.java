package kozlov.kirill.sockets.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public interface MulticastHandler {
    void handle(DatagramPacket datagramPacket);
}