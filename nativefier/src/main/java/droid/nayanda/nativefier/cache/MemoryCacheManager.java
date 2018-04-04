package droid.nayanda.nativefier.cache;

import android.support.annotation.NonNull;

import java.util.concurrent.ConcurrentLinkedQueue;

import droid.nayanda.nativefier.model.Entry;

/**
 * Created by nayanda on 18/03/18.
 */

public class MemoryCacheManager<TValue> implements CacheManager<TValue> {

    private final int maxCacheNumber;
    private final ConcurrentLinkedQueue<Entry<TValue>> lruCache = new ConcurrentLinkedQueue<>();

    public MemoryCacheManager(int maxCacheNumber) {
        this.maxCacheNumber = maxCacheNumber;
    }

    @Override
    public int getMaxCacheNumber() {
        return maxCacheNumber;
    }

    @Override
    public TValue get(@NonNull String key) {
        for (Entry<TValue> entry : lruCache) {
            if (entry != null) {
                if (entry.getKey().equals(key)) {
                    lruCache.remove(entry);
                    lruCache.offer(entry);
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void put(@NonNull String key, @NonNull TValue value) {
        Entry<TValue> newEntry = new Entry<>(key, value);
        lruCache.remove(newEntry);
        lruCache.offer(newEntry);
        while (lruCache.size() > maxCacheNumber) {
            lruCache.poll();
        }
    }

    @Override
    public void clear() {
        lruCache.clear();
    }

    @Override
    public boolean isExist(@NonNull String key) {
        for (Entry entry : lruCache) {
            if (entry != null) {
                if (entry.getKey().equals(key)) return true;
            }
        }
        return false;
    }

    @Override
    public void delete(@NonNull String key) {
        for (Entry entry : lruCache) {
            if (entry != null) {
                if (entry.getKey().equals(key)) lruCache.remove(entry);
            }
        }
    }
}
