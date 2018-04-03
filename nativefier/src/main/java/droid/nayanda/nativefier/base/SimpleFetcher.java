package droid.nayanda.nativefier.base;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;

/**
 * Created by nayanda on 18/03/18.
 */

public abstract class SimpleFetcher<TValue> implements Fetcher<TValue> {

    private Context context;

    public SimpleFetcher(Context context) {
        this.context = context;
    }

    @Override
    public void asyncFetch(@NonNull final String key, @NonNull final Finisher<TValue> finisher) {
        Handler uiHandler = new Handler(context.getMainLooper());
        uiHandler.post(() -> {
            FetcherTask<TValue> task = new FetcherTask<>(SimpleFetcher.this::fetch, finisher);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, key);
        });
    }
}
