package kozlov.kirill.sockets;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import kozlov.kirill.primes.ParallelStreamsUnprimeChecker;
import kozlov.kirill.primes.UnprimeChecker;
import kozlov.kirill.sockets.data.TaskData;
import kozlov.kirill.sockets.data.TaskResult;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Worker implements Runnable {
    final private Socket socket;
    final private ArrayList<Integer> numbers;

    public Worker(Socket socket, ArrayList<Integer> numbers) {
        this.socket = socket;
        this.numbers = numbers;
    }

    /**
     * Main processing of client's requests.
     * <br>
     * Whereas socket is opened reads requests for it and send results of calculation
     */
    public void run() {
        UnprimeChecker checker = new ParallelStreamsUnprimeChecker().setNumbers(numbers);
        TaskResult taskResult = new TaskResult(checker.isAnyUnprime());
        try {
            BasicTCPSocketOperations.sendJSONObject(socket, taskResult);
        } catch (IOException ignored) {}
    }
}
