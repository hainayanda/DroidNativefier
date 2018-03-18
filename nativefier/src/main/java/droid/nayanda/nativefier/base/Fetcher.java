package droid.nayanda.nativefier.base;

import android.support.annotation.NonNull;

/**
 * Created by nayanda on 18/03/18.
 */

public interface Fetcher<TValue> {
    TValue fetch(@NonNull String key);

    void asyncFetch(@NonNull String key, @NonNull Finisher<TValue> finisher);
}
