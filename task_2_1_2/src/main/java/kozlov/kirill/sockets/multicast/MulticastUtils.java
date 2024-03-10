package kozlov.kirill.sockets.multicast;

import kozlov.kirill.sockets.Gateway;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

abstract public class MulticastUtils {
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
                DatagramPacket responsePacket = DatagramUtils.createPacket(responseStr, packet.getAddress(), ackPort);
                socket.send(responsePacket);
                socket.close();
            } catch (IOException e) {
                System.err.println("Error...");
            }
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
