package droid.nayanda.nativefier.base;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * Created by nayanda on 18/03/18.
 */

public abstract class SimpleFetcher<TValue> implements Fetcher<TValue> {

    @Override
    public void asyncFetch(@NonNull final String key, @NonNull final Finisher<TValue> finisher) {
        FetcherTask task = new FetcherTask<>(this::fetch, finisher);
        task.execute(key);
    }
}
