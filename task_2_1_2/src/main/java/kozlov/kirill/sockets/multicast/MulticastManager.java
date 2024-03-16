package kozlov.kirill.sockets.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.concurrent.ThreadFactory;

public class MulticastManager {
    static private int currentFreeInterfaceIndex = 0;
    int port;
    private InetSocketAddress groupAddress = null;
    private NetworkInterface groupInterface = null;
    private ThreadFactory handlersFactory;

    public MulticastManager(String ip, int port) {
        this.port = port;
        handlersFactory = Thread.ofVirtual().name("Workers handler").factory();
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

    public MulticastSocket registerMulticastHandler(
        MulticastHandler handler
    ) throws IOException {
        MulticastSocket socket = new MulticastSocket(port);
        socket.joinGroup(groupAddress, groupInterface);
        Thread handlerThread = handlersFactory.newThread(
            MulticastUtils.getRunnableHandlerWrapper(socket, handler)
        );
        handlerThread.start();
        return socket;
    }
}
