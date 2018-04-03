package droid.nayanda.nativefier;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;

import droid.nayanda.nativefier.base.Fetcher;
import droid.nayanda.nativefier.serializer.SerializableSerializer;

/**
 * Created by nayanda on 18/03/18.
 */

class SerializableNativefier<TSerializable extends Serializable> extends Nativefier<TSerializable> {

    SerializableNativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String containerName, int maxCacheNumber, Fetcher<TSerializable> fetcher) throws IOException {
        super(context, diskUsage, containerName, maxCacheNumber, new SerializableSerializer<>(), fetcher);
    }

    SerializableNativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String appVersion, @NonNull String containerName, int maxCacheNumber, Fetcher<TSerializable> fetcher) throws IOException {
        this(context, diskUsage, appVersion + "_" + containerName, maxCacheNumber, fetcher);
    }

    SerializableNativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String containerName, int maxRamCacheNumber,
                           int maxDiskCacheNumber, Fetcher<TSerializable> fetcher) throws IOException {
        super(context, diskUsage, containerName, maxRamCacheNumber, maxDiskCacheNumber, new SerializableSerializer<>(), fetcher);
    }

    SerializableNativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String appVersion, @NonNull String containerName,
                           int maxRamCacheNumber, int maxDiskCacheNumber, Fetcher<TSerializable> fetcher) throws IOException {
        this(context, diskUsage, appVersion + "_" + containerName, maxRamCacheNumber, maxDiskCacheNumber, fetcher);
    }
}
