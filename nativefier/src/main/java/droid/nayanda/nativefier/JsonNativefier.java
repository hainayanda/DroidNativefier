package droid.nayanda.nativefier;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import droid.nayanda.nativefier.base.Fetcher;
import droid.nayanda.nativefier.serializer.JsonSerializer;

/**
 * Created by nayanda on 18/03/18.
 */

class JsonNativefier<TJsonObj> extends Nativefier<TJsonObj> {

    JsonNativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String containerName, int maxCacheNumber,
                   @NonNull Class<TJsonObj> jsonObjClass, Fetcher<TJsonObj> fetcher) throws IOException {
        super(context, diskUsage, containerName, maxCacheNumber, new JsonSerializer<>(jsonObjClass), fetcher);
    }

    JsonNativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String appVersion, @NonNull String containerName,
                   int maxCacheNumber, @NonNull Class<TJsonObj> jsonObjClass, Fetcher<TJsonObj> fetcher) throws IOException {
        this(context, diskUsage, appVersion + "_" + containerName, maxCacheNumber, jsonObjClass, fetcher);
    }

    JsonNativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String containerName, int maxRamCacheNumber,
                   int maxDiskCacheNumber, @NonNull Class<TJsonObj> jsonObjClass, Fetcher<TJsonObj> fetcher) throws IOException {
        super(context, diskUsage, containerName, maxRamCacheNumber, maxDiskCacheNumber, new JsonSerializer<>(jsonObjClass), fetcher);
    }

    JsonNativefier(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String appVersion, @NonNull String containerName,
                   int maxRamCacheNumber, int maxDiskCacheNumber, @NonNull Class<TJsonObj> jsonObjClass, Fetcher<TJsonObj> fetcher) throws IOException {
        this(context, diskUsage, appVersion + "_" + containerName, maxRamCacheNumber, maxDiskCacheNumber, jsonObjClass, fetcher);
    }
}
