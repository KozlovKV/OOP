package kozlov.kirill.sockets;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Manager implements Runnable {
    final private Socket socket;

    public Manager(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String str;
            ObjectMapper mapper = new ObjectMapper();
            var parser = mapper.createParser(bufferedReader);
            TaskData taskData = null;
            var it = parser.readValuesAs(TaskData.class);
            while (it.hasNext()) {
                taskData = it.next();
                System.out.println(taskData.numbers().size());
            }
//            while ((str = bufferedReader.readLine()) != null) {
//                taskData = mapper.readValue(str, TaskData.class);
//                System.out.println(taskData.numbers().size());
//            }
            System.out.println("Connetcion closed");
        } catch (UnrecognizedPropertyException e) {
            System.err.println("Parsing error: " + e.getMessage());
        } catch (IOException ignored) {
            System.err.println("IO server error");
        }
    }
}
