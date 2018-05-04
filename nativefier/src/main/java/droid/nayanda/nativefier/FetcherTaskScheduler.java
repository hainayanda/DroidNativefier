package droid.nayanda.nativefier;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.concurrent.ConcurrentLinkedQueue;

import droid.nayanda.nativefier.base.Finisher;
import droid.nayanda.nativefier.base.Task;

public class FetcherTaskScheduler {

    private static final ConcurrentLinkedQueue<Node<?>> pendingTask = new ConcurrentLinkedQueue<>();

    private FetcherTaskScheduler() {
    }

    public static <TValue> void execute(@NonNull final FetcherTask<TValue> task, @NonNull final String key, @NonNull final Context context) {
        int maxNumberOfThread = NativefierContext.getMaxQueueThreadCount();
        if (NativefierContext.getNumberOfQueueRunning() <= maxNumberOfThread) {
            Handler uiHandler = new Handler(context.getMainLooper());
            uiHandler.post(() -> task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, key));
        } else {
            Node node = new Node<>(task, key, context);
            pendingTask.offer(node);
        }
    }

    public static class FetcherTask<TValue> extends AsyncTask<String, Void, TValue> {

        private final Finisher<TValue> finisher;
        private final Task<String, TValue> task;

        public FetcherTask(@NonNull Task<String, TValue> task, @NonNull Finisher<TValue> finisher) {
            this.finisher = finisher;
            this.task = task;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NativefierContext.incrementNumberOfPoolRunning();
        }

        @Override
        protected TValue doInBackground(String... strings) {
            try {
                return task.run(strings[0]);
            } catch (Exception ignored) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(TValue value) {
            finisher.onFinished(value);
            NativefierContext.decrementNumberOfPoolRunning();
            Node node = pendingTask.poll();
            if (node != null) {
                FetcherTask task = node.task;
                String key = node.key;
                Context context = node.context;
                FetcherTaskScheduler.execute(task, key, context);
            }
        }
    }

    private static class Node<TValue> {
        private FetcherTask<TValue> task;
        private String key;
        private Context context;

        Node(FetcherTask<TValue> task, String key, Context context) {
            this.task = task;
            this.key = key;
            this.context = context;
        }
    }
}
