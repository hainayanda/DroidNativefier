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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import droid.nayanda.nativefier.DiskUsage;
import droid.nayanda.nativefier.serializer.Serializer;

/**
 * Created by nayanda on 18/03/18.
 */

public class DiskCacheManager<TValue> implements CacheManager<TValue> {

    private final File directory;
    private File indexFile;
    private Serializer<TValue> serializer;
    private int maxCacheNumber;
    private LinkedList<String> index;
    private Vector<DiskTask> pendingDiskTasks = new Vector<>();
    private boolean isWriting = false;
    private boolean isIndexNeedUpdate = false;
    private Context context;

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
            index = new LinkedList<>();
        } else index = readFileToList(indexFile);
        this.serializer = serializer;
        this.maxCacheNumber = maxCacheNumber;
    }

    private static byte[] readFileToBytes(File file) {
        FileInputStream inputStream = null;
        try {
            byte[] bytes = new byte[(int) file.length()];
            inputStream = new FileInputStream(file);
            inputStream.read(bytes);
            inputStream.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return new byte[0];
        }
    }

    private static <T> LinkedList<T> linkedListCopier(LinkedList<T> list) {
        LinkedList<T> copy = new LinkedList<>();
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            copy.add(iterator.next());
        }
        return copy;
    }

    @Override
    public int getMaxCacheNumber() {
        return maxCacheNumber;
    }

    private LinkedList<String> readFileToList(File file) {
        BufferedReader reader = null;
        LinkedList<String> strings = new LinkedList<>();
        try {
            InputStream inputStream = new FileInputStream(file.getAbsoluteFile());
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                strings.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            Log.e("Nativiefier Error", e.getMessage());
            if (reader != null) try {
                reader.close();
            } catch (IOException e1) {
                Log.e("Nativiefier Error", e1.getMessage());
            }
        }
        return strings;
    }

    @Override
    public TValue get(@NonNull String key) {
        if (!isExist(key)) return null;
        File file = new File(directory, key + ".ch");
        if (!file.exists()) {
            Object[] objects = pendingDiskTasks.toArray();
            for (int i = objects.length - 1; i >= 0; i--) {
                DiskTask task = (DiskTask) objects[i];
                if (task.getType() == TaskType.WRITE) {
                    if (task.getTask().getFileName().equals(key)) {
                        return task.getTask().getObject();
                    }
                }
            }
            removeIndex(key);
            isIndexNeedUpdate = true;
            writeAllPendingTask();
            return null;
        }
        try {
            TValue obj = deserializeFile(file);
            removeIndex(key);
            addFirstAndRemoveIfNecessaryForIndex(key);
            isIndexNeedUpdate = true;
            writeAllPendingTask();
            return obj;
        } catch (Exception e) {
            Log.e("Nativiefier Error", e.getMessage());
            removeIndex(key);
            isIndexNeedUpdate = true;
            pendingDiskTasks.add(new DiskTask(TaskType.DELETE, new TaskPair(key, null)));
            writeAllPendingTask();
            return null;
        }

    }

    private TValue deserializeFile(File file) throws IOException, ClassNotFoundException {
        byte[] bytes = readFileToBytes(file);
        return serializer.deserialize(bytes);
    }

    @Override
    public void put(@NonNull String key, @NonNull TValue value) {
        if (!isExist(key)) {
            addFirstAndRemoveIfNecessaryForIndex(key);
        } else {
            removeIndex(key);
            addFirstAndRemoveIfNecessaryForIndex(key);
            isIndexNeedUpdate = true;
            pendingDiskTasks.add(new DiskTask(TaskType.DELETE, new TaskPair(key, value)));
        }
        pendingDiskTasks.add(new DiskTask(TaskType.WRITE, new TaskPair(key, value)));
        writeAllPendingTask();
    }

    private void writeAllPendingTask() {
        if (isWriting) return;
        isWriting = true;
        Handler uiHandler = new Handler(context.getMainLooper());
        uiHandler.post(() -> AsyncTask.execute(() -> {
            while (pendingDiskTasks.size() > 0 || isIndexNeedUpdate) {
                if (pendingDiskTasks.size() > 0) {
                    executePendingDiskTask();
                }
                if (isIndexNeedUpdate) {
                    updateIndex();
                }
            }
            isWriting = false;
        }));
    }

    private void executePendingDiskTask() {
        Iterator<DiskTask> iterator = pendingDiskTasks.iterator();
        while (iterator.hasNext()) {
            DiskTask task = iterator.next();
            switch (task.getType()) {
                case WRITE:
                    putToDisk(task.getTask());
                    break;
                case DELETE:
                    removeFromDisk(task.getTask().getFileName());
                    break;
            }
            iterator.remove();
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
        BufferedWriter writer = null;
        try {
            if (!indexFile.exists()) {
                if (!indexFile.createNewFile()) throw new IOException("Failed to create new file");
            }
            if(index.size() > 0) {
                String[] thisIndex = index.toArray(new String[index.size()]);
                isIndexNeedUpdate = false;
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
    }

    @Override
    public void clear() {
        pendingDiskTasks = new Vector<>();
        for (String key : index) {
            pendingDiskTasks.add(new DiskTask(TaskType.DELETE, new TaskPair(key, null)));
        }
        index = new LinkedList<>();
        isIndexNeedUpdate = true;
        writeAllPendingTask();
    }

    @Override
    public boolean isExist(@NonNull String key) {
        return index.contains(key);
    }

    @Override
    public void delete(@NonNull String key) {
        pendingDiskTasks.add(new DiskTask(TaskType.DELETE, new TaskPair(key, null)));
        Iterator<DiskTask> iterator = pendingDiskTasks.iterator();
        removeIndex(key);
        while (iterator.hasNext()) {
            DiskTask task = iterator.next();
            if (task.getType() == TaskType.WRITE) {
                if (task.getTask().fileName.equals(key)) iterator.remove();
            }
        }
    }

    private void addFirstAndRemoveIfNecessaryForIndex(String key) {
        LinkedList<String> indexCopy = linkedListCopier(index);
        indexCopy.addFirst(key);
        boolean isIndexNeedUpdate = false;
        while (indexCopy.size() > maxCacheNumber) {
            indexCopy.pop();
            isIndexNeedUpdate = true;
        }
        index = indexCopy;
        if (isIndexNeedUpdate) writeAllPendingTask();
    }

    private void removeIndex(String key) {
        LinkedList<String> indexCopy = linkedListCopier(index);
        indexCopy.remove(key);
        index = indexCopy;
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
}
