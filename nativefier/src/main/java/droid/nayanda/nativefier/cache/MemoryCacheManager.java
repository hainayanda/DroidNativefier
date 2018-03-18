package droid.nayanda.nativefier.cache;

import android.support.annotation.NonNull;

import java.util.LinkedList;

/**
 * Created by nayanda on 18/03/18.
 */

public class MemoryCacheManager<TValue> implements CacheManager<TValue> {

    private final LinkedList<Entry> lruCache = new LinkedList<>();
    private final int maxCacheNumber;

    public MemoryCacheManager(int maxCacheNumber) {
        this.maxCacheNumber = maxCacheNumber;
    }

    @Override
    public int getMaxCacheNumber() {
        return maxCacheNumber;
    }

    @Override
    public TValue get(@NonNull String key) {
        for (Entry entry : lruCache) {
            if (entry.getKey().equals(key)) {
                lruCache.remove(entry);
                lruCache.addFirst(entry);
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public void put(@NonNull String key, @NonNull TValue value) {
        Entry newEntry = new Entry(key, value);
        for (Entry entry : lruCache) {
            if (entry.getKey().equals(key)) {
                lruCache.remove(entry);
                lruCache.addFirst(newEntry);
                removeLastEntry();
                return;
            }
        }
        lruCache.addFirst(newEntry);
        removeLastEntry();
    }

    private void removeLastEntry() {
        while (lruCache.size() > maxCacheNumber) {
            lruCache.pop();
        }
    }

    @Override
    public void clear() {
        lruCache.clear();
    }

    @Override
    public boolean isExist(@NonNull String key) {
        for (Entry entry : lruCache) {
            if (entry.getKey().equals(key)) return true;
        }
        return false;
    }

    private class Entry {
        private final String key;
        private final TValue value;

        Entry(@NonNull String key, @NonNull TValue value) {
            this.key = key;
            this.value = value;
        }

        String getKey() {
            return key;
        }

        TValue getValue() {
            return value;
        }
    }
}
