package kozlov.kirill.sockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
//        ExecutorService e = Executors.newFixedThreadPool(5);
//        e.submit(() -> {
//            try {
////                TimeUnit.SECONDS.sleep(2);
//                int port = 8888;
//                byte[] data = {0, 1, 2, 3, 4, 5, 6, 7};
//                // send to all nodes on the current physical network
//                // via the limited broadcast address
//                InetAddress address = InetAddress.getByName("255.255.255.255");
//                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
////                DatagramSocket socket = new DatagramSocket(9000);
//                MulticastSocket socket = new MulticastSocket(9000);
//                System.out.println("Sending broadcast from thread");
//                socket.send(packet);
//                byte[] response = new byte[1024];
//                DatagramPacket responsePacket = new DatagramPacket(response, response.length);
//                socket.receive(responsePacket);
//                System.out.println("Got response from " + responsePacket.getAddress() + ":" + responsePacket.getPort());
//            } catch (IOException ignored){
//            }
//
//        });
//        e.shutdown();
//        String addr = "localhost";
//        int port = 8888;
//        byte[] data = new byte[1024];
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ignored) {}
//        try (
//            DatagramSocket s = new DatagramSocket(port)
//        ) {
//            InetAddress broadcast = InetAddress.getByName(addr);
//            DatagramPacket d = new DatagramPacket(data, data.length);
//            s.receive(d);
//            System.out.println("Got packet from " + d.getAddress() + ":" + d.getPort());
//            DatagramPacket responsePacket = new DatagramPacket(data, data.length,
//                    d.getAddress(), d.getPort());
//            s.send(responsePacket);
//        } catch (IOException ignored) {
//
//        }
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; ++i) {
            list.add(i);
        }
        TaskData taskData = new TaskData(list);
        ObjectMapper mapper = new ObjectMapper();
        String stringData = "null";
        try {
            stringData = mapper.writeValueAsString(taskData);
        } catch (JsonProcessingException e) {
            System.err.println("Couldn't convert to JSON");
        }
        Thread gatewayThread = new Thread(new Gateway());
        gatewayThread.start();
        Client client = new Client();
        var socket = client.getManagerSocket();
        if (socket != null) {
            System.out.println("Client: " + socket.getInetAddress() + " " + socket.getPort());
            try {
                OutputStream out = socket.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(out);
                writer.write(stringData);
                writer.flush();
                Thread.sleep(10000);
                writer.write(stringData);
                writer.flush();
                socket.close();
            } catch (IOException | InterruptedException ignored) {}
        } else {
            gatewayThread.interrupt();
        }
    }
}