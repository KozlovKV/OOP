package kozlov.kirill.sockets.multicast;

import java.io.IOException;
import java.net.*;

public class MulticastManager {
    static private int currentFreeInterfaceIndex = 0;
    int port;
    private InetSocketAddress groupAddress = null;
    private NetworkInterface groupInterface = null;

    public MulticastManager(String ip, int port) {
        this.port = port;
        try {
            groupInterface = NetworkInterface.getByIndex(
                currentFreeInterfaceIndex++
            );
            groupAddress = new InetSocketAddress(
                InetAddress.getByName(ip), port
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MulticastSocket registerMulticastHandler(MulticastHandler handler) {
        try {
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(groupAddress, groupInterface);
            Thread handlerThread = new Thread(
                    MulticastUtilsFactory.getRunnableHandlerWrapper(socket, handler)
            );
            handlerThread.start();
            return socket;
        } catch (IOException e) {
            System.err.println("Error to register handler");
        }
        return null;
    }
}
