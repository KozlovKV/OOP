package kozlov.kirill.primes;

/**
 * Simple unprime checker's implementation.
 */
public class SimpleUnprimeChecker extends UnprimeChecker {
    @Override
    public boolean isAnyUnprime() throws InterruptedException {
        for (var num : numbers) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Interrupted while counting");
            }
            if (isNumUnprime(num)) {
                return true;
            }
        }
        return false;
    }
}
