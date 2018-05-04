package droid.nayanda.nativefier.base;

import android.content.Context;
import android.support.annotation.NonNull;

import droid.nayanda.nativefier.FetcherTaskScheduler;
import droid.nayanda.nativefier.FetcherTaskScheduler.FetcherTask;

/**
 * Created by nayanda on 18/03/18.
 */

public abstract class BaseFetcher<TValue> implements Fetcher<TValue> {

    private final Task<String, TValue> task;
    private final Context context;

    public BaseFetcher(@NonNull Task<String, TValue> task, @NonNull Context context) {
        this.task = task;
        this.context = context;
    }

    @Override
    public void asyncFetch(@NonNull String key, @NonNull Finisher<TValue> finisher) {
        FetcherTask<TValue> task = new FetcherTask<>(this.task, finisher);
        FetcherTaskScheduler.execute(task, key, context);
    }
}
