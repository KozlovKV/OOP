package kozlov.kirill.primes;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadUnprimeChecker extends UnprimeChecker {
    private final int threadsCount;

    public ThreadUnprimeChecker(int threadsCount) {
        super();
        this.threadsCount = threadsCount;
    }

    @Override
    public boolean isAnyUnprime() {
        final int numbersSize = numbers.size();
        final int threadNumberListSize = numbersSize / threadsCount + 1;
        AtomicBoolean unprimeFound = new AtomicBoolean(false);
        Thread[] threads = new Thread[threadsCount];
        for (int i = 0; i < threadsCount; ++i) {
            final int threadNumbersStartIndex = i * threadNumberListSize;
            Thread newThread = new Thread(() -> {
                if (unprimeFound.get() || threadNumbersStartIndex >= numbersSize)
                    return;
                int threadNumbersEndIndex =
                        threadNumbersStartIndex + threadNumberListSize < numbersSize ?
                        threadNumbersStartIndex + threadNumberListSize :
                        numbersSize - 1;
                boolean result = numbers.subList(
                    threadNumbersStartIndex, threadNumbersEndIndex
                ).stream().anyMatch(this::isNumUnprime);
                if (result)
                    unprimeFound.set(true);
            });
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
