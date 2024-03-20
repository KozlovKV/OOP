package kozlov.kirill.sockets.multicast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * Static class with datagram sockets utils.
 */
public final class DatagramUtils {
    private DatagramUtils() {}

    /**
     * Data extractor.
     *
     * @param packet datagram packet
     * @return extracted string
     */
    static public String extractContentFromPacket(DatagramPacket packet) {
        return new String(packet.getData(), packet.getOffset(), packet.getLength());
    }

    /**
     * Simple datagram packet creator.
     *
     * @param str data for packet
     * @return datagram packet with specified data
     */
    static public DatagramPacket createPacket(String str) {
        byte[] buf = str.getBytes(StandardCharsets.UTF_8);
        return new DatagramPacket(buf, buf.length);
    }

    /**
     * Sendable datagram packet creator.
     *
     * @param str data for packet
     * @param addr source address
     * @param port source port
     * @return datagram packet ready for sending through datagram socket
     */
    static public DatagramPacket createPacket(String str, InetAddress addr, int port) {
        DatagramPacket packet = createPacket(str);
        packet.setAddress(addr);
        packet.setPort(port);
        return packet;
    }
}
