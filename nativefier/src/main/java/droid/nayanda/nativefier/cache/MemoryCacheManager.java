package droid.nayanda.nativefier.cache;

import android.support.annotation.NonNull;

import java.util.LinkedList;

/**
 * Created by nayanda on 18/03/18.
 */

public class MemoryCacheManager<TValue> implements CacheManager<TValue> {

    private final int maxCacheNumber;
    private final LinkedList<Entry> lruCache = new LinkedList<>();

    public MemoryCacheManager(int maxCacheNumber) {
        this.maxCacheNumber = maxCacheNumber;
    }

    @Override
    public int getMaxCacheNumber() {
        return maxCacheNumber;
    }

    @Override
    public TValue get(@NonNull String key) {
        synchronized (lruCache) {
            for (Entry entry : lruCache) {
                if (entry != null) {
                    if (entry.getKey().equals(key)) {
                        lruCache.remove(entry);
                        lruCache.addFirst(entry);
                        return entry.getValue();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void put(@NonNull String key, @NonNull TValue value) {
        Entry newEntry = new Entry(key, value);
        synchronized (lruCache) {
            for (int i = 0; i < lruCache.size(); i++) {
                Entry entry = lruCache.get(i);
                if (entry.getKey().equals(key)) {
                    lruCache.remove(i);
                    break;
                }
            }
        }
        addFirstAndRemoveIfNecessaryForLru(newEntry);
        removeLastEntry();
    }

    private void removeLastEntry() {
        synchronized (lruCache) {
            if (lruCache.size() <= maxCacheNumber) return;
            while (lruCache.size() > maxCacheNumber) {
                lruCache.removeLast();
            }
        }
    }

    @Override
    public void clear() {
        synchronized (lruCache) {
            lruCache.clear();
        }
    }

    @Override
    public boolean isExist(@NonNull String key) {
        for (int i = 0; i < lruCache.size(); i++) {
            Entry entry = lruCache.get(i);
            String entryKey = entry.getKey();
            if (entryKey.equals(key)) return true;
        }
        return false;
    }

    @Override
    public void delete(@NonNull String key) {
        synchronized (lruCache) {
            for (int i = 0; i < lruCache.size(); i++) {
                Entry entry = lruCache.get(i);
                String entryKey = entry.getKey();
                if (entryKey.equals(key)) {
                    lruCache.remove(i);
                    return;
                }
            }
        }
    }

    private void addFirstAndRemoveIfNecessaryForLru(Entry entry) {
        synchronized (lruCache) {
            lruCache.remove(entry);
            lruCache.addFirst(entry);
            while (lruCache.size() > maxCacheNumber) {
                lruCache.removeLast();
            }
        }
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
