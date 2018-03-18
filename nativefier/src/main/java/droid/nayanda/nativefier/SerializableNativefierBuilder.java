package droid.nayanda.nativefier;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;

import droid.nayanda.nativefier.base.Fetcher;

public class SerializableNativefierBuilder<TValue extends Serializable> {
    private Context context;
    private String containerName;
    private int maxCacheNumber;
    private Fetcher<TValue> fetcher;
    private String appVersion;

    SerializableNativefierBuilder() {
    }

    public SerializableNativefierBuilder setContext(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public SerializableNativefierBuilder setContainerName(@NonNull String containerName) {
        this.containerName = containerName;
        return this;
    }

    public SerializableNativefierBuilder setMaxCacheNumber(int maxCacheNumber) {
        this.maxCacheNumber = maxCacheNumber;
        return this;
    }

    public SerializableNativefierBuilder setFetcher(@NonNull Fetcher<TValue> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    public SerializableNativefierBuilder setAppVersion(@NonNull String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public Nativefier<TValue> createSerializableNativefier() throws IOException {
        if (context == null) throw new IllegalStateException("context cannot be null");
        if (containerName == null) throw new IllegalStateException("containerName cannot be null");
        if (appVersion != null)
            return new SerializableNativefier<>(context, appVersion, containerName, maxCacheNumber, fetcher);
        return new SerializableNativefier<>(context, containerName, maxCacheNumber, fetcher);

    }
}