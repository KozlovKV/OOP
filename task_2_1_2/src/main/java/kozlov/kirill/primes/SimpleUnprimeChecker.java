package kozlov.kirill.primes;

/**
 * Unprime checker's implementation with Stream API.
 */
public class SimpleUnprimeChecker extends UnprimeChecker {
    @Override
    public boolean isAnyUnprime() throws InterruptedException {
        for (var num : numbers) {
            if (Thread.currentThread().isInterrupted())
                throw new InterruptedException("Interrupted while countring");
            if (isNumUnprime(num))
                return true;
        }
        return false;
    }
}
