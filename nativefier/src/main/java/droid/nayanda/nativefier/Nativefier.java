package droid.nayanda.nativefier;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.io.IOException;

import droid.nayanda.nativefier.base.Fetcher;
import droid.nayanda.nativefier.base.Finisher;
import droid.nayanda.nativefier.cache.CacheManager;
import droid.nayanda.nativefier.cache.DiskCacheManager;
import droid.nayanda.nativefier.cache.MemoryCacheManager;
import droid.nayanda.nativefier.serializer.Serializer;

/**
 * Created by nayanda on 18/03/18.
 */

public class Nativefier<TValue> implements CacheManager<TValue> {

    private MemoryCacheManager<TValue> memoryCacheManager;
    private DiskCacheManager<TValue> diskCacheManager;
    private Fetcher<TValue> fetcher;

    Nativefier(@NonNull Context context, @NonNull String containerName, int maxCacheNumber,
               @NonNull Serializer<TValue> serializer, Fetcher<TValue> fetcher) throws IOException {
        if (maxCacheNumber < 2)
            throw new IllegalArgumentException("maxNumber minimum value is 2 : " + maxCacheNumber);
        diskCacheManager = new DiskCacheManager<>(context, containerName, maxCacheNumber, serializer);
        memoryCacheManager = new MemoryCacheManager<>(maxCacheNumber / 2);
        this.fetcher = fetcher;
    }

    Nativefier(@NonNull Context context, @NonNull String appVersion, @NonNull String containerName,
               int maxCacheNumber, @NonNull Serializer<TValue> serializer, Fetcher<TValue> fetcher) throws IOException {
        this(context, appVersion + "_" + containerName, maxCacheNumber, serializer, fetcher);
    }

    @Override
    public int getMaxCacheNumber() {
        return diskCacheManager.getMaxCacheNumber();
    }

    @Override
    public TValue get(@NonNull String key) {
        TValue obj = memoryCacheManager.get(key);
        if (obj == null) {
            obj = diskCacheManager.get(key);
            if (obj != null) memoryCacheManager.put(key, obj);
        }
        return obj;
    }

    public TValue getOrFetchIfNotFound(@NonNull String key) {
        TValue obj = get(key);
        if (obj == null) {
            obj = fetcher.fetch(key);
            if (obj != null) {
                memoryCacheManager.put(key, obj);
                diskCacheManager.put(key, obj);
            }
        }
        return obj;
    }

    public void asyncGet(@NonNull final String key, final Finisher<TValue> finisher) {
        final TValue obj = get(key);
        if (obj == null && fetcher != null) {
            fetcher.asyncFetch(key, object -> {
                if (object != null) {
                    memoryCacheManager.put(key, object);
                    diskCacheManager.put(key, object);
                }
                finisher.onFinished(object);
            });
        } else finisher.onFinished(obj);
    }

    @Override
    public void put(@NonNull String key, @NonNull TValue value) {
        memoryCacheManager.put(key, value);
        diskCacheManager.put(key, value);
    }

    @Override
    public void clear() {
        memoryCacheManager.clear();
        diskCacheManager.clear();
    }

    @Override
    public boolean isExist(@NonNull String key) {
        if (memoryCacheManager.isExist(key)) return true;
        else {
            boolean exist = diskCacheManager.isExist(key);
            if (exist) {
                TValue obj = diskCacheManager.get(key);
                memoryCacheManager.put(key, obj);
                return true;
            }
            return false;
        }
    }
}
