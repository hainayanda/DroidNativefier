package droid.nayanda.nativefier.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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
    private LinkedList<String> pendingRemoves = new LinkedList<>();
    private LinkedHashMap<String, TValue> pendingPut = new LinkedHashMap<>();
    private boolean isWriting = false;
    private boolean isIndexNeedUpdate = false;

    public DiskCacheManager(@NonNull Context context, @NonNull String containerName, int maxCacheNumber, Serializer<TValue> serializer) throws IOException {
        this.directory = new File(context.getCacheDir(), containerName);
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

    public DiskCacheManager(@NonNull Context context, @NonNull String appVersion, @NonNull String containerName, int maxCacheNumber, Serializer<TValue> serializer) throws IOException {
        this(context, appVersion + "_" + containerName, maxCacheNumber, serializer);
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
        } catch (IOException e) {
            e.printStackTrace();
            if (reader != null) try {
                reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return strings;
    }

    @Override
    public TValue get(@NonNull String key) {
        if (!isExist(key)) return null;
        File file = new File(directory, key + ".ch");
        if (!file.exists()) {
            index.remove(key);
            isIndexNeedUpdate = true;
            writeAllPendingTask();
            return null;
        }
        try {
            TValue obj = deserializeFile(file);
            index.remove(key);
            index.addFirst(key);
            isIndexNeedUpdate = true;
            writeAllPendingTask();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            index.remove(key);
            isIndexNeedUpdate = true;
            if (pendingPut.containsKey(key)) pendingPut.remove(key);
            pendingRemoves.add(key);
            writeAllPendingTask();
            return null;
        }

    }

    private void removeLastIndex() {
        boolean needUpdate = false;
        if (index.size() > maxCacheNumber) {
            needUpdate = true;
            while (index.size() > maxCacheNumber) {
                index.pop();
            }
        }
        isIndexNeedUpdate = needUpdate;
        writeAllPendingTask();
    }

    private TValue deserializeFile(File file) throws IOException, ClassNotFoundException {
        byte[] bytes = readFileToBytes(file);
        return serializer.deserialize(bytes);
    }

    @Override
    public void put(@NonNull String key, @NonNull TValue value) {
        if (!isExist(key)) {
            index.addFirst(key);
            removeLastIndex();
        } else {
            index.remove(key);
            index.addFirst(key);
            isIndexNeedUpdate = true;
        }
        pendingPut.put(key, value);
        writeAllPendingTask();
    }

    private void writeAllPendingTask() {
        if (isWriting) return;
        isWriting = true;
        AsyncTask.execute(() -> {
            while (pendingPut.size() > 0 || pendingRemoves.size() > 0 || isIndexNeedUpdate) {
                if (pendingRemoves.size() > 0) {
                    executePendingRemoves();
                }
                if (pendingPut.size() > 0) {
                    executePendingPut();
                }
                if (isIndexNeedUpdate) {
                    updateIndex();
                }
            }
            isWriting = false;
        });
    }

    private void updateIndex() {
        BufferedWriter writer = null;
        try {
            if (!indexFile.exists()) {
                if (!indexFile.createNewFile()) throw new IOException("Failed to create new file");
            }
            if(index.size() > 0) {
                LinkedList<String> thisIndex = new LinkedList<>(index);
                isIndexNeedUpdate = false;
                StringBuilder builder = new StringBuilder();
                for (String line : thisIndex) {
                    builder.append(line).append('\n');
                }
                writer = new BufferedWriter(new FileWriter(indexFile.getAbsoluteFile()));
                writer.write(builder.toString());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (writer != null) try {
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void executePendingPut() {
        Set<Map.Entry<String, TValue>> puts = ((LinkedHashMap<String, TValue>) (pendingPut.clone())).entrySet();
        pendingPut.clear();
        for (Map.Entry<String, TValue> entry : puts) {
            FileOutputStream outputStream = null;
            try {
                File file = new File(directory, entry.getKey() + ".ch");
                byte[] bytes = serializer.serialize(entry.getValue());
                if (!file.exists()) {
                    if (!file.createNewFile())
                        throw new IOException("Failed to create new file : " + entry.getKey());
                }
                outputStream = new FileOutputStream(file.getAbsoluteFile());
                outputStream.write(bytes);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                if (outputStream != null) try {
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void executePendingRemoves() {
        LinkedList<String> removes = (LinkedList<String>) pendingRemoves.clone();
        pendingRemoves.clear();
        for (String remove : removes) {
            File file = new File(directory, remove + ".ch");
            if (!file.exists()) continue;
            file.delete();
        }

    }

    @Override
    public void clear() {
        for (String key : index) {
            pendingPut.remove(key);
            pendingRemoves.add(key);
        }
        index.clear();
        isIndexNeedUpdate = true;
        writeAllPendingTask();
    }

    @Override
    public boolean isExist(@NonNull String key) {
        return index.contains(key);
    }

    @Override
    public void delete(@NonNull String key) {
        pendingPut.remove(key);
        pendingRemoves.add(key);
        index.remove(key);
    }
}
