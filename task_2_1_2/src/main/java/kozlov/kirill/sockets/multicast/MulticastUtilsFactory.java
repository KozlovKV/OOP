package kozlov.kirill.sockets.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

abstract public class MulticastUtilsFactory {
    static private int BUF_SZ = 1024;

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

    static public MulticastHandler getStringDataResponse(int port, String responseStr) {
        return (DatagramPacket packet) -> {
            try {
                String inputStr = new String(packet.getData(), packet.getOffset(), packet.getLength());
                int ackPort = -1;
                try {
                    ackPort = Integer.parseInt(inputStr, 0, inputStr.length()-1, 10);
                } catch (NumberFormatException e) {
                    System.err.println("Incorrect request port");
                    return;
                }
                DatagramSocket socket = new DatagramSocket(port);
                byte[] buf = responseStr.getBytes(StandardCharsets.UTF_8);
                DatagramPacket responsePacket = new DatagramPacket(buf, buf.length, packet.getAddress(), ackPort);
                socket.send(responsePacket);
                socket.close();
            } catch (IOException e) {
                System.err.println("Error...");
            }
        };
    }
}
