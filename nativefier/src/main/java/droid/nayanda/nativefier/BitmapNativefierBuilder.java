package droid.nayanda.nativefier;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.IOException;

import droid.nayanda.nativefier.base.Fetcher;

public class BitmapNativefierBuilder {
    private Context context;
    private int maxCacheNumber = 50;
    private int maxRamCacheNumber = 0;
    private Fetcher<Bitmap> fetcher = null;

    BitmapNativefierBuilder() {
    }

    public BitmapNativefierBuilder setContext(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public BitmapNativefierBuilder setMaxCacheNumber(int maxCacheNumber) {
        this.maxCacheNumber = maxCacheNumber;
        return this;
    }

    public BitmapNativefierBuilder setMaxRamCacheNumber(int maxCacheNumber) {
        this.maxRamCacheNumber = maxCacheNumber;
        return this;
    }

    public BitmapNativefierBuilder setFetcher(@NonNull Fetcher<Bitmap> fetcher) {
        this.fetcher = fetcher;
        return this;
    }

    public Nativefier<Bitmap> createNativefier() throws IOException {
        if (context == null) throw new IllegalStateException("context cannot be null");
        if (maxRamCacheNumber <= 0)
            return new BitmapNativefier(context, maxCacheNumber, fetcher);
        else
            return new BitmapNativefier(context, maxRamCacheNumber, maxCacheNumber, fetcher);
    }
}