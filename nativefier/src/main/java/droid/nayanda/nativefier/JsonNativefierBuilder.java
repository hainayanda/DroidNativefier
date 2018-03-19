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
    private DiskUsage diskUsage = DiskUsage.EXTERNAL;

    JsonNativefierBuilder() {
    }

    public JsonNativefierBuilder<TJsonObj> setContext(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public JsonNativefierBuilder<TJsonObj> setContainerName(@NonNull String containerName) {
        this.containerName = containerName;
        return this;
    }

    public JsonNativefierBuilder<TJsonObj> setMaxCacheNumber(int maxCacheNumber) {
        this.maxCacheNumber = maxCacheNumber;
        return this;
    }

    public JsonNativefierBuilder<TJsonObj> setJsonObjClass(@NonNull Class<TJsonObj> jsonObjClass) {
        this.jsonObjClass = jsonObjClass;
        return this;
    }

    public JsonNativefierBuilder<TJsonObj> setFetcher(@NonNull Fetcher<TJsonObj> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    public JsonNativefierBuilder<TJsonObj> setAppVersion(@NonNull String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public JsonNativefierBuilder<TJsonObj> setDiskUsage(@NonNull DiskUsage diskUsage) {
        this.diskUsage = diskUsage;
        return this;
    }

    public Nativefier<TJsonObj> createNativefier() throws IOException {
        if (context == null) throw new IllegalStateException("context cannot be null");
        if (containerName == null) throw new IllegalStateException("containerName cannot be null");
        if (jsonObjClass == null) throw new IllegalStateException("jsonObjClass cannot be null");
        if (appVersion != null)
            return new JsonNativefier<>(context, diskUsage, appVersion, containerName, maxCacheNumber, jsonObjClass, fetcher);
        return new JsonNativefier<>(context, diskUsage, containerName, maxCacheNumber, jsonObjClass, fetcher);
    }
}