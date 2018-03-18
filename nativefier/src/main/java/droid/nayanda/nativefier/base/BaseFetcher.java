package droid.nayanda.nativefier.base;

import android.support.annotation.NonNull;

/**
 * Created by nayanda on 18/03/18.
 */

public abstract class BaseFetcher<TValue> implements Fetcher<TValue> {

    private final Task<String, TValue> task;

    public BaseFetcher(@NonNull Task<String, TValue> task){
        this.task = task;
    }

    @Override
    public void asyncFetch(@NonNull String key, @NonNull Finisher<TValue> finisher) {
        FetcherTask task = new FetcherTask<>(this.task, finisher);
        task.execute(key);
    }
}
