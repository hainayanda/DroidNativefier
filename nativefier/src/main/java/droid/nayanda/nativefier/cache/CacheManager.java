package droid.nayanda.nativefier.cache;

import android.support.annotation.NonNull;

/**
 * Created by nayanda on 18/03/18.
 */

public interface CacheManager<TValue> {

    int getMaxCacheNumber();

    TValue get(@NonNull String key);

    void put(@NonNull String key, @NonNull TValue value);

    void clear();

    boolean isExist(@NonNull String key);

    void delete(@NonNull String key);
}