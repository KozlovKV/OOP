package kozlov.kirill.sockets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPReceiver implements Runnable {
    private DatagramSocket socket = null;

    public UDPReceiver(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (IOException e) {
            System.err.println("Cannot create listener socket");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            System.out.println("Got packet from " + packet.getSocketAddress());
        } catch (IOException ignored) {

        }
    }
}
