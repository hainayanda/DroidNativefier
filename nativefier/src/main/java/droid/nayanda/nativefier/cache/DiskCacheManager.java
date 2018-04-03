package droid.nayanda.nativefier.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import droid.nayanda.nativefier.DiskUsage;
import droid.nayanda.nativefier.serializer.Serializer;

/**
 * Created by nayanda on 18/03/18.
 */

public class DiskCacheManager<TValue> implements CacheManager<TValue> {

    private final File directory;
    private final File indexFile;
    private final ConcurrentLinkedQueue<String> index;
    private final ConcurrentHashMap<File, AsyncReader> readingTask = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<DiskTask> pendingDiskTasks = new ConcurrentLinkedQueue<>();
    private final Serializer<TValue> serializer;
    private final int maxCacheNumber;
    private final Context context;
    private final AtomicBoolean isWriting = new AtomicBoolean(false);
    private final AtomicReference<AsyncUpdate> asyncUpdater = new AtomicReference<>();

    public DiskCacheManager(@NonNull Context context, @NonNull DiskUsage diskUsage, @NonNull String containerName, int maxCacheNumber, Serializer<TValue> serializer) throws IOException {
        this.context = context;
        switch (diskUsage) {
            case EXTERNAL:
                this.directory = new File(context.getExternalCacheDir(), containerName);
                break;
            default:
                this.directory = new File(context.getCacheDir(), containerName);
                break;
        }
        if (!this.directory.exists()) {
            if (!directory.mkdir())
                throw new IOException("Failed to create directory for " + containerName);
        }
        this.indexFile = new File(this.directory, "index.dat");
        if (!indexFile.exists()) {
            if (!indexFile.createNewFile()) throw new IOException("Failed to create new file");
            index = new ConcurrentLinkedQueue<>();
        } else index = readFileToList(indexFile);
        this.serializer = serializer;
        this.maxCacheNumber = maxCacheNumber;
    }

    private byte[] readFileToBytes(File file) {
        InputStream inputStream = null;
        try {
            byte[] bytes = new byte[(int) file.length()];
            inputStream = new FileInputStream(file);
            inputStream.read(bytes);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e1) {
                Log.e("Nativefier Error", e1.getMessage());
            }
            return new byte[0];
        }
    }

    @Override
    public int getMaxCacheNumber() {
        return maxCacheNumber;
    }

    private ConcurrentLinkedQueue<String> readFileToList(File file) {
        BufferedReader reader = null;
        ConcurrentLinkedQueue<String> strings = new ConcurrentLinkedQueue<>();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file.getAbsoluteFile());
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                strings.add(line);
                line = reader.readLine();
            }
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            Log.e("Nativiefier Error", e.getMessage());
            if (reader != null) try {
                reader.close();
            } catch (IOException e1) {
                Log.e("Nativiefier Error", e1.getMessage());
            }
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e1) {
                Log.e("Nativefier Error", e1.getMessage());
            }
        }
        return strings;
    }

    @Override
    public TValue get(@NonNull String key) {
        if (!isExist(key)) return null;
        File file = new File(directory, key + ".ch");
        if (!file.exists()) {
            for (DiskTask task : pendingDiskTasks) {
                if (task != null) {
                    if (task.getType() == TaskType.WRITE) {
                        if (task.getTask().getFileName().equals(key)) {
                            return task.getTask().getObject();
                        }
                    }
                }
            }
        }
        if (!file.exists()) return null;
        try {
            TValue obj = deserializeFile(file);
            addFirstAndRemoveIfNecessaryForIndex(key);
            return obj;
        } catch (Exception e) {
            Log.e("Nativiefier Error", e.getMessage());
            index.remove(key);
            updateIndex();
            pendingDiskTasks.add(new DiskTask(TaskType.DELETE, new TaskPair(key, null)));
            writeAllPendingTask();
            return null;

        }
    }

    private TValue deserializeFile(File file) throws IOException, ClassNotFoundException {
        if (readingTask.containsKey(file)) {
            try {
                AsyncReader reader = readingTask.get(file);
                if (reader == null) return newReadingTask(file);
                return reader.get();
            } catch (Exception e) {
                Log.e("Nativefier Error", e.getMessage());
                return null;
            }
        } else {
            return newReadingTask(file);
        }
    }

    private TValue newReadingTask(File file) {
        TValue result = null;
        try {
            AsyncReader reader = new AsyncReader();
            readingTask.put(file, reader);
            Handler uiHandler = new Handler(context.getMainLooper());
            uiHandler.post(() -> reader.execute(file));
            result = reader.get();
            readingTask.remove(file);
        } catch (Exception e) {
            Log.e("Nativefier Error", e.getMessage());
            if (readingTask.containsKey(file)) readingTask.remove(file);
        }
        return result;
    }

    @Override
    public void put(@NonNull String key, @NonNull TValue value) {
        if (!isExist(key)) {
            addFirstAndRemoveIfNecessaryForIndex(key);
        } else {
            addFirstAndRemoveIfNecessaryForIndex(key);
            pendingDiskTasks.add(new DiskTask(TaskType.DELETE, new TaskPair(key, value)));
        }
        pendingDiskTasks.add(new DiskTask(TaskType.WRITE, new TaskPair(key, value)));
        writeAllPendingTask();
    }

    private void writeAllPendingTask() {
        if (isWriting.get()) return;
        isWriting.set(true);
        Handler uiHandler = new Handler(context.getMainLooper());
        uiHandler.post(() -> AsyncTask.execute(() -> {
            while (pendingDiskTasks.size() > 0) {
                executePendingDiskTask();
            }
            isWriting.set(false);
        }));
    }

    private void executePendingDiskTask() {
        DiskTask task = pendingDiskTasks.poll();
        while (task != null) {
            switch (task.getType()) {
                case WRITE:
                    putToDisk(task.getTask());
                    break;
                case DELETE:
                    removeFromDisk(task.getTask().getFileName());
                    break;
            }
            task = pendingDiskTasks.poll();
        }
    }

    private void removeFromDisk(String fileName) {
        File file = new File(directory, fileName + ".ch");
        if (!file.exists()) return;
        file.delete();
    }

    private void putToDisk(TaskPair task) {
        FileOutputStream outputStream = null;
        try {
            File file = new File(directory, task.getFileName() + ".ch");
            byte[] bytes = serializer.serialize(task.getObject());
            if (!file.exists()) {
                if (!file.createNewFile())
                    throw new IOException("Failed to create new file : " + task.getFileName());
            }
            outputStream = new FileOutputStream(file.getAbsoluteFile());
            outputStream.write(bytes);
            outputStream.close();
        } catch (Exception e) {
            Log.e("Nativiefier Error", e.getMessage());
            if (outputStream != null) try {
                outputStream.close();
            } catch (IOException e1) {
                Log.e("Nativiefier Error", e1.getMessage());
            }
        }
    }

    private void updateIndex() {
        AsyncUpdate updater = asyncUpdater.get();
        if (updater == null) {
            updater = new AsyncUpdate();
            updater.execute();
            asyncUpdater.set(updater);
        } else if (updater.getStatus() == AsyncTask.Status.FINISHED) {
            updater = new AsyncUpdate();
            updater.execute();
            asyncUpdater.set(updater);
        } else {
            try {
                updater.get();
                updater = new AsyncUpdate();
                updater.execute();
                asyncUpdater.set(updater);
            } catch (InterruptedException | ExecutionException e) {
                Log.e("Nativefier Error", e.getMessage());
            }
        }

    }

    @Override
    public void clear() {
        pendingDiskTasks.clear();
        String key = index.poll();
        while (key != null) {
            pendingDiskTasks.add(new DiskTask(TaskType.DELETE, new TaskPair(key, null)));
            key = index.poll();
        }
        updateIndex();
        writeAllPendingTask();
    }

    @Override
    public boolean isExist(@NonNull String key) {
        return index.contains(key);
    }

    @Override
    public void delete(@NonNull String key) {
        pendingDiskTasks.add(new DiskTask(TaskType.DELETE, new TaskPair(key, null)));
        index.remove(key);
        updateIndex();
        writeAllPendingTask();
    }

    private void addFirstAndRemoveIfNecessaryForIndex(String key) {
        index.remove(key);
        index.offer(key);
        while (index.size() > maxCacheNumber) {
            index.poll();
        }
        updateIndex();
        writeAllPendingTask();
    }

    private enum TaskType {
        WRITE, DELETE
    }

    private class TaskPair {
        private String fileName;
        private TValue object;

        TaskPair(String fileName, TValue object) {
            this.fileName = fileName;
            this.object = object;
        }

        String getFileName() {
            return fileName;
        }

        TValue getObject() {
            return object;
        }
    }

    class DiskTask {
        private TaskType type;
        private TaskPair task;

        DiskTask(TaskType type, TaskPair task) {
            this.type = type;
            this.task = task;
        }

        TaskType getType() {
            return type;
        }

        TaskPair getTask() {
            return task;
        }
    }

    class AsyncUpdate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            BufferedWriter writer = null;
            try {
                if (!indexFile.exists()) {
                    if (!indexFile.createNewFile())
                        throw new IOException("Failed to create new file");
                }
                if (index.size() >= 0) {
                    String[] thisIndex = index.toArray(new String[index.size()]);
                    StringBuilder builder = new StringBuilder();
                    for (String line : thisIndex) {
                        builder.append(line).append('\n');
                    }
                    writer = new BufferedWriter(new FileWriter(indexFile.getAbsoluteFile()));
                    writer.write(builder.toString());
                    writer.close();
                }
            } catch (Exception e) {
                Log.e("Nativiefier Error", e.getMessage());
                e.printStackTrace();
                if (writer != null) try {
                    writer.close();
                } catch (IOException e1) {
                    Log.e("Nativiefier Error", e1.getMessage());
                }
            }
            return null;
        }
    }

    class AsyncReader extends AsyncTask<File, Void, TValue> {

        @Override
        protected TValue doInBackground(File... files) {
            try {
                byte[] bytes = readFileToBytes(files[0]);
                return serializer.deserialize(bytes);
            } catch (IOException | ClassNotFoundException e) {
                Log.e("Nativifier Error", e.getMessage());
                return null;
            }
        }
    }
}
