package droid.nayanda.nativefier;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import droid.nayanda.nativefier.base.ArgumentsFinisher;
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
    private Context context;

    Nativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String containerName, int maxRamCacheNumber, int maxDiskCacheNumber,
               @NonNull Serializer<TValue> serializer, Fetcher<TValue> fetcher) throws IOException {
        if (maxRamCacheNumber < 2 || maxDiskCacheNumber < 2)
            throw new IllegalArgumentException("maxNumber minimum value is 2");
        diskCacheManager = new DiskCacheManager<>(context, diskUsage, containerName, maxDiskCacheNumber, serializer);
        memoryCacheManager = new MemoryCacheManager<>(maxRamCacheNumber);
        this.fetcher = fetcher;
        this.context = context;
    }

    Nativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String containerName, int maxCacheNumber,
               @NonNull Serializer<TValue> serializer, Fetcher<TValue> fetcher) throws IOException {
        this(context, diskUsage, containerName, maxCacheNumber / 2, maxCacheNumber, serializer, fetcher);
    }

    Nativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String appVersion, @NonNull String containerName,
               int maxCacheNumber, @NonNull Serializer<TValue> serializer, Fetcher<TValue> fetcher) throws IOException {
        this(context, diskUsage, appVersion + "_" + containerName, maxCacheNumber, serializer, fetcher);
    }

    Nativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String appVersion, @NonNull String containerName,
               int maxRamCacheNumber, int maxDiskCacheNumber, @NonNull Serializer<TValue> serializer, Fetcher<TValue> fetcher) throws IOException {
        this(context, diskUsage, appVersion + "_" + containerName, maxRamCacheNumber, maxDiskCacheNumber, serializer, fetcher);
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

    public void asyncGet(@NonNull String key, final Finisher<TValue> finisher) {
        final TValue obj = get(key);
        if (obj == null && fetcher != null) {
            Handler uiHandler = new Handler(context.getMainLooper());
            uiHandler.post(() -> fetcher.asyncFetch(key,
                    new ArgumentsFinisher<TValue, Object>(finisher, key, memoryCacheManager, diskCacheManager) {
                        @Override
                        public void onFinished(TValue obj1, Object[] args) {
                            if (obj1 != null) {
                                ((CacheManager<TValue>) args[2]).put((String) args[1], obj1);
                                ((CacheManager<TValue>) args[3]).put((String) args[1], obj1);
                            }
                            try {
                                ((Finisher<TValue>) args[0]).onFinished(obj1);
                            } catch (Exception e) {
                                Log.e("Nativefier Error", e.getMessage());
                            }
                        }
                    }));
        } else try {
            finisher.onFinished(obj);
        } catch (Exception e) {
            Log.e("Nativefier Error", e.getMessage());
        }
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
        return (memoryCacheManager.isExist(key) || diskCacheManager.isExist(key));
    }

    @Override
    public void delete(@NonNull String key) {
        memoryCacheManager.delete(key);
        diskCacheManager.delete(key);
    }
}
