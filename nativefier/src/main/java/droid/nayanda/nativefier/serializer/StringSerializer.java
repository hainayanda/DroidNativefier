package droid.nayanda.nativefier.serializer;

import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * Created by nayanda on 18/03/18.
 */

public abstract class StringSerializer<TValue> implements Serializer<TValue> {

    public abstract String serializeToString(@NonNull TValue obj);

    public abstract TValue deserializeFromString(@NonNull String string);

    @Override
    public byte[] serialize(@NonNull TValue obj) throws IOException {
        String str = serializeToString(obj);
        if (str == null) return new byte[0];
        return str.getBytes();
    }

    @Override
    public TValue deserialize(@NonNull byte[] bytes) throws IOException, ClassNotFoundException {
        String str = new String(bytes);
        return deserializeFromString(str);
    }
}
