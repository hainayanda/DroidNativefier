package droid.nayanda.nativefier.cache;

import android.support.annotation.NonNull;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by nayanda on 18/03/18.
 */

public class MemoryCacheManager<TValue> implements CacheManager<TValue> {

    private final int maxCacheNumber;
    private LinkedList<Entry> lruCache = new LinkedList<>();

    public MemoryCacheManager(int maxCacheNumber) {
        this.maxCacheNumber = maxCacheNumber;
    }

    private static <T> LinkedList<T> linkedListCopier(LinkedList<T> list) {
        LinkedList<T> copy = new LinkedList<>();
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            copy.add(iterator.next());
        }
        return copy;
    }

    @Override
    public int getMaxCacheNumber() {
        return maxCacheNumber;
    }

    @Override
    public TValue get(@NonNull String key) {
        for (Entry entry : lruCache) {
            if (entry.getKey().equals(key)) {
                return swapToFirstForLru(entry).getValue();
            }
        }
        return null;
    }

    @Override
    public void put(@NonNull String key, @NonNull TValue value) {
        Entry newEntry = new Entry(key, value);
        for (int i = 0; i < lruCache.size(); i++) {
            Entry entry = lruCache.get(i);
            if (entry.getKey().equals(key)) {
                removeLru(i);
                break;
            }
        }
        addFirstAndRemoveIfNecessaryForLru(newEntry);
        removeLastEntry();
    }

    private void removeLastEntry() {
        if (lruCache.size() <= maxCacheNumber) return;
        LinkedList<Entry> lruCopy = linkedListCopier(lruCache);
        while (lruCopy.size() > maxCacheNumber) {
            lruCopy.pop();
        }
        lruCache = lruCopy;
    }

    @Override
    public void clear() {
        lruCache = new LinkedList<>();
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
        for (int i = 0; i < lruCache.size(); i++) {
            Entry entry = lruCache.get(i);
            String entryKey = entry.getKey();
            if (entryKey.equals(key)) {
                removeLru(i);
                return;
            }
        }
    }

    private void addFirstAndRemoveIfNecessaryForLru(Entry entry) {
        LinkedList<Entry> lruCopy = linkedListCopier(lruCache);
        lruCopy.addFirst(entry);
        while (lruCopy.size() > maxCacheNumber) {
            lruCopy.pop();
        }
        lruCache = lruCopy;
    }

    private Entry swapToFirstForLru(Entry entry) {
        LinkedList<Entry> lruCopy = linkedListCopier(lruCache);
        lruCopy.remove(entry);
        lruCopy.addFirst(entry);
        while (lruCopy.size() > maxCacheNumber) {
            lruCopy.pop();
        }
        lruCache = lruCopy;
        return entry;
    }

    private void removeLru(int i) {
        LinkedList<Entry> lruCopy = linkedListCopier(lruCache);
        lruCopy.remove(i);
        lruCache = lruCopy;
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
