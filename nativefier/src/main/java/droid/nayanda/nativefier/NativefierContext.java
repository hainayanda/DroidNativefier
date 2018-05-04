package droid.nayanda.nativefier;

import android.support.annotation.IntRange;

import java.util.concurrent.atomic.AtomicInteger;

public class NativefierContext {

    private static final AtomicInteger numberOfQueueRunning = new AtomicInteger(0);
    private static final AtomicInteger numberOfPoolRunning = new AtomicInteger(0);
    private static int maxPoolThreadCount = 128;
    private static int maxQueueThreadCount = Integer.MAX_VALUE;

    public static int getNumberOfPoolRunning() {
        return numberOfPoolRunning.get();
    }

    static int getNumberOfQueueRunning() {
        return numberOfQueueRunning.get();
    }

    static int incrementNumberOfQueueRunning() {
        return numberOfQueueRunning.incrementAndGet();
    }

    static int decrementNumberOfQueueRunning() {
        return numberOfQueueRunning.decrementAndGet();
    }

    static int incrementNumberOfPoolRunning() {
        return numberOfPoolRunning.incrementAndGet();
    }

    static int decrementNumberOfPoolRunning() {
        return numberOfPoolRunning.decrementAndGet();
    }

    private NativefierContext() {
    }

    public static int getMaxPoolThreadCount() {
        return maxPoolThreadCount;
    }

    public static void setMaxPoolThreadCount(@IntRange(from = 1, to = 128) int maxThreadCount) {
        NativefierContext.maxPoolThreadCount = maxThreadCount;
    }


    public static int getMaxQueueThreadCount() {
        return maxQueueThreadCount;
    }

    public static void setMaxQueueThreadCount(@IntRange(from = 1, to = Integer.MAX_VALUE) int maxQueueThreadCount) {
        NativefierContext.maxQueueThreadCount = maxQueueThreadCount;
    }
}
