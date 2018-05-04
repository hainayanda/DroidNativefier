package droid.nayanda.nativefier;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncScheduler {

    private static final ConcurrentLinkedQueue<Runnable> pendingTask = new ConcurrentLinkedQueue<>();

    private AsyncScheduler() {
    }

    public static void execute(@NonNull final Runnable task, @NonNull final Context context) {
        int maxNumberOfThread = NativefierContext.getMaxQueueThreadCount();
        if (NativefierContext.getNumberOfQueueRunning() <= maxNumberOfThread) {
            Handler uiHandler = new Handler(context.getMainLooper());
            uiHandler.post(() -> new Runner().execute(task, context));
        } else {
            pendingTask.offer(task);
        }
    }

    private static class Runner extends AsyncTask<Object, Void, Context> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NativefierContext.incrementNumberOfQueueRunning();
        }

        @Override
        protected Context doInBackground(Object... objects) {

            Runnable task = (Runnable) objects[0];
            Context context = (Context) objects[1];
            try {
                task.run();
            } catch (Exception ignored) {
            }
            return context;
        }

        @Override
        protected void onPostExecute(Context context) {
            super.onPostExecute(context);
            NativefierContext.decrementNumberOfQueueRunning();
            Runnable task = pendingTask.poll();
            if (task != null) {
                AsyncScheduler.execute(task, context);
            }
        }
    }

}
