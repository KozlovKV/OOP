package kozlov.kirill.sockets.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.concurrent.ThreadFactory;

/**
 * Manager for creation multicast groups with linked handlers.
 */
public class MulticastManager {
    private final int port;
    private InetSocketAddress groupAddress = null;
    private NetworkInterface groupInterface = null;
    private final ThreadFactory handlersFactory;

    /**
     * MulticastManager constructor.
     * <br>
     * Creates multicast group with the first network interface and virtual threads factory
     * for linking handlers to multicast address
     *
     * @param ip hostname of multicast group which will be used by manager
     * @param port port of multicast group which will be used by manager
     */
    public MulticastManager(String ip, int port) {
        this.port = port;
        handlersFactory = Thread.ofVirtual().name("Workers handler").factory();
        try {
            groupInterface = NetworkInterface.getByIndex(0);
            groupAddress = new InetSocketAddress(
                InetAddress.getByName(ip), port
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Multicast handler registrator.
     * <br>
     * Creates multicast socket, joins it to manager's multicast group and wraps handler
     * in while-live loop in virtual thread
     *
     * @param handler handler for processing packet received through multicast socket
     *
     * @return created multicast socket
     *
     * @throws IOException any exception during registration process
     */
    public MulticastSocket registerMulticastHandler(
        MulticastHandler handler
    ) throws IOException {
        MulticastSocket socket = new MulticastSocket(port);
        socket.joinGroup(groupAddress, groupInterface);
        Thread handlerThread = handlersFactory.newThread(
            getRunnableHandlerWrapper(socket, handler)
        );
        handlerThread.start();
        return socket;
    }

    private static final int BUF_SZ = 1024;
    private static Runnable getRunnableHandlerWrapper(MulticastSocket socket, MulticastHandler handler) {
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
}
