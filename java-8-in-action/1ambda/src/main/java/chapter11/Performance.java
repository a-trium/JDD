package chapter11;

public class Performance {
    public static long getConsumedTime(Runnable callback) {
        long start = System.nanoTime();

        callback.run();

        long retrievalTime = ((System.nanoTime() - start) / 1000000);

        return retrievalTime;
    }
}
