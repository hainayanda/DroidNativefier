package droid.nayanda.nativefier.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import droid.nayanda.nativefier.serializer.Serializer;

/**
 * Created by nayanda on 04/04/18.
 */

public class ReadWorker<TValue> {

    private final File file;
    private final Serializer<TValue> serializer;
    private final AsyncReader<TValue> reader;
    private final AtomicReference<TValue> result = new AtomicReference<>();
    private final Context context;

    public ReadWorker(Context context, File file, Serializer<TValue> serializer) {
        this.context = context;
        this.file = file;
        this.serializer = serializer;
        reader = new AsyncReader<>();
    }

    public TValue read() {
        if (result.get() != null) return result.get();
        synchronized (reader) {
            if (reader.getStatus() == AsyncTask.Status.PENDING) {
                Handler uiHandler = new Handler(context.getMainLooper());
                uiHandler.post(() -> reader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, file, serializer));
                try {
                    result.set(reader.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                reader.notifyAll();
                return result.get();
            }
        }
        return read();
    }

    static class AsyncReader<T> extends AsyncTask<Object, Void, T> {

        @Override
        protected T doInBackground(Object... objects) {
            File file = (File) objects[0];
            Serializer<T> serializer = (Serializer<T>) objects[1];
            InputStream inputStream = null;
            try {
                byte[] bytes = new byte[(int) file.length()];
                inputStream = new FileInputStream(file);
                inputStream.read(bytes);
                return serializer.deserialize(bytes);
            } catch (IOException | ClassNotFoundException e) {
                Log.e("Nativifier Error", e.getMessage());
                if (inputStream != null) try {
                    inputStream.close();
                } catch (IOException e1) {
                    Log.e("Nativefier Error", e1.getMessage());
                }
                return null;
            }
        }
    }
}
