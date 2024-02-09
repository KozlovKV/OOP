package kozlov.kirill.primes;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unprime checker's implementation with multithreading.
 */
public class ThreadUnprimeChecker extends UnprimeChecker {
    private final int threadsCount;

    /**
     * Constructor.
     *
     * @param threadsCount thread for checking process
     */
    public ThreadUnprimeChecker(int threadsCount) {
        super();
        this.threadsCount = threadsCount;
    }

    /**
     * Constructor for thread's run method.
     *
     * @param unprimeFound atomic boolean object for syncing threads
     * @param threadNumbersStartIndex start index for numbers' list part
     * @param numbersSize full size of numbers' list
     * @param threadNumberListSize size of numbers' list part for thread
     * @return instance of Runnable lambda interface
     */
    private Runnable constructThreadRunFunction(AtomicBoolean unprimeFound,
                                                int threadNumbersStartIndex,
                                                int numbersSize, int threadNumberListSize) {
        return () -> {
            if (unprimeFound.get() || threadNumbersStartIndex >= numbersSize) {
                return;
            }
            int threadNumbersEndIndex =
                    Math.min(threadNumbersStartIndex + threadNumberListSize, numbersSize);
            boolean result = numbers.subList(
                    threadNumbersStartIndex, threadNumbersEndIndex
            ).stream().anyMatch(this::isNumUnprime);
            if (result) {
                unprimeFound.set(true);
            }
        };
    }

    @Override
    public boolean isAnyUnprime() {
        final int numbersSize = numbers.size();
        final int threadNumberListSize = numbersSize / threadsCount + 1;
        AtomicBoolean unprimeFound = new AtomicBoolean(false);
        Thread[] threads = new Thread[threadsCount];
        for (int i = 0; i < threadsCount; ++i) {
            final int threadNumbersStartIndex = i * threadNumberListSize;
            Thread newThread = new Thread(constructThreadRunFunction(
                    unprimeFound, threadNumbersStartIndex, numbersSize, threadNumberListSize
            ));
            newThread.start();
            threads[i] = newThread;
        }
        try {
            for (int i = 0; i < threadsCount; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Strange things...");
        }
        return unprimeFound.get();
    }
}
