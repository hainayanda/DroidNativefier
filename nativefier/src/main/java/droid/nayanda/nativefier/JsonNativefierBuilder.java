package droid.nayanda.nativefier;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import droid.nayanda.nativefier.base.Fetcher;

public class JsonNativefierBuilder<TJsonObj> {
    private Context context;
    private String containerName;
    private int maxCacheNumber = 50;
    private Class<TJsonObj> jsonObjClass;
    private Fetcher<TJsonObj> fetcher;
    private String appVersion;

    JsonNativefierBuilder() {
    }

    public JsonNativefierBuilder setContext(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public JsonNativefierBuilder setContainerName(@NonNull String containerName) {
        this.containerName = containerName;
        return this;
    }

    public JsonNativefierBuilder setMaxCacheNumber(int maxCacheNumber) {
        this.maxCacheNumber = maxCacheNumber;
        return this;
    }

    public JsonNativefierBuilder setJsonObjClass(@NonNull Class<TJsonObj> jsonObjClass) {
        this.jsonObjClass = jsonObjClass;
        return this;
    }

    public JsonNativefierBuilder setFetcher(@NonNull Fetcher<TJsonObj> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    public JsonNativefierBuilder setAppVersion(@NonNull String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public Nativefier<TJsonObj> createJsonNativefier() throws IOException {
        if (context == null) throw new IllegalStateException("context cannot be null");
        if (containerName == null) throw new IllegalStateException("containerName cannot be null");
        if (jsonObjClass == null) throw new IllegalStateException("jsonObjClass cannot be null");
        if (appVersion != null)
            return new JsonNativefier<>(context, appVersion, containerName, maxCacheNumber, jsonObjClass, fetcher);
        return new JsonNativefier<>(context, containerName, maxCacheNumber, jsonObjClass, fetcher);
    }
}