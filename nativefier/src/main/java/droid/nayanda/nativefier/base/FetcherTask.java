package droid.nayanda.nativefier.base;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * Created by nayanda on 18/03/18.
 */

public class FetcherTask<TValue> extends AsyncTask<String, Void, TValue> {

    private final Finisher<TValue> finisher;
    private final Task<String, TValue> task;

    FetcherTask(@NonNull Task<String, TValue> task, @NonNull Finisher<TValue> finisher){
        this.finisher = finisher;
        this.task = task;
    }

    @Override
    protected TValue doInBackground(String... strings) {
        return task.run(strings[0]);
    }

    @Override
    protected void onPostExecute(TValue value){
        finisher.onFinished(value);
    }
}