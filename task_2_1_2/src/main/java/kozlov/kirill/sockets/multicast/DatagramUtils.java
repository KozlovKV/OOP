package kozlov.kirill.sockets.multicast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

abstract public class DatagramUtils {
    static public String extractFromPacket(DatagramPacket packet) {
        return new String(packet.getData(), packet.getOffset(), packet.getLength());
    }

    static public DatagramPacket createPacket(String str) {
        byte[] buf = str.getBytes(StandardCharsets.UTF_8);
        return new DatagramPacket(buf, buf.length);
    }

    static public DatagramPacket createPacket(String str, InetAddress addr, int port) {
        DatagramPacket packet = createPacket(str);
        packet.setAddress(addr);
        packet.setPort(port);
        return packet;
    }
}
