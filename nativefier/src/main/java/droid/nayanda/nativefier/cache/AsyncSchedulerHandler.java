package droid.nayanda.nativefier.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import droid.nayanda.nativefier.NativefierContext;

class AsyncSchedulerHandler {

    private static final ConcurrentLinkedQueue<Runnable> pendingTask = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger numberOfThread = new AtomicInteger(0);

    private AsyncSchedulerHandler() {
    }

    static void execute(@NonNull final Runnable task, @NonNull final Context context) {
        int maxNumberOfThread = NativefierContext.getMaxThreadCount();
        if (numberOfThread.get() <= maxNumberOfThread) {
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
            numberOfThread.incrementAndGet();
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
            numberOfThread.decrementAndGet();
            Runnable task = pendingTask.poll();
            if (task != null) {
                AsyncSchedulerHandler.execute(task, context);
            }
        }
    }

}
