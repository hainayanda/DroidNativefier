package droid.nayanda.nativefier;

import android.support.annotation.IntRange;

public class NativefierContext {

    private static int maxThreadCount = Integer.MAX_VALUE;

    private NativefierContext() {
    }

    public static int getMaxThreadCount() {
        return maxThreadCount;
    }

    public static void setMaxThreadCount(@IntRange(from = 1, to = Integer.MAX_VALUE) int maxThreadCount) {
        NativefierContext.maxThreadCount = maxThreadCount;
    }


}
