package kozlov.kirill.sockets;

/**
 * Unprime checker's implementation with parallel Stream API.
 */
public class ParallelStreamsUnprimeChecker extends UnprimeChecker {
    @Override
    public boolean isAnyUnprime() {
        return numbers.parallelStream().anyMatch(UnprimeChecker::isNumUnprime);
    }
}
