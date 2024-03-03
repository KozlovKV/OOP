package kozlov.kirill.sockets;

import java.io.IOException;
import java.net.*;
import java.nio.MappedByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.FutureTask;

public class Client {
    public static final int CLIENT_PORT = 8000;
    private final int MS_FOR_UDO_RESPONSE = 1000;
    private Socket socket = null;

    public Socket getManagerSocket() {
        try {
            Socket clientSocket = new Socket("localhost", Gateway.GATEWAY_PORT);
            return clientSocket;
        } catch (IOException ignored) {
            return null;
        }
//        try (
//            DatagramSocket requestSocket = new DatagramSocket(CLIENT_PORT)
//        ) {
//            InetAddress broadcastAddr = InetAddress.getByName("255.255.255.255");
//            DatagramPacket packet = new DatagramPacket(
//                    new byte[]{1}, 1, broadcastAddr, Gateway.GATEWAY_PORT
//            );
//            requestSocket.setSoTimeout(1000);
//            requestSocket.send(packet);
//            requestSocket.receive(packet);
//            System.out.println("Got response from gateway " + packet.getAddress() + ":" + packet.getPort());
//
//            System.out.println(packet.getData());
//        } catch (UnknownHostException e) {
//            System.err.println("Cannot create finding socket");
//        } catch (SocketTimeoutException e) {
//            System.err.println("Couldn't find manager");
//        } catch (IOException ignored) {}
//        return Optional.empty();
    }
}
