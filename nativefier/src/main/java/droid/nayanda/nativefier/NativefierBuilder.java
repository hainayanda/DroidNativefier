package droid.nayanda.nativefier;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import droid.nayanda.nativefier.base.Fetcher;
import droid.nayanda.nativefier.serializer.Serializer;

public class NativefierBuilder<TValue> {
    private Context context;
    private String containerName;
    private int maxCacheNumber = 50;
    private Serializer<TValue> serializer;
    private Fetcher<TValue> fetcher = null;
    private String appVersion;

    NativefierBuilder() {
    }

    public NativefierBuilder setContext(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public NativefierBuilder setContainerName(@NonNull String containerName) {
        this.containerName = containerName;
        return this;
    }

    public NativefierBuilder setMaxCacheNumber(int maxCacheNumber) {
        this.maxCacheNumber = maxCacheNumber;
        return this;
    }

    public NativefierBuilder setSerializer(@NonNull Serializer<TValue> serializer) {
        this.serializer = serializer;
        return this;
    }

    public NativefierBuilder setFetcher(@NonNull Fetcher<TValue> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    public NativefierBuilder setAppVersion(@NonNull String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public Nativefier<TValue> createNativefier() throws IOException {
        if (context == null) throw new IllegalStateException("context cannot be null");
        if (containerName == null) throw new IllegalStateException("containerName cannot be null");
        if (serializer == null) throw new IllegalStateException("serializer cannot be null");
        if (appVersion == null)
            return new Nativefier<>(context, containerName, maxCacheNumber, serializer, fetcher);
        else
            return new Nativefier<>(context, appVersion, containerName, maxCacheNumber, serializer, fetcher);
    }
}