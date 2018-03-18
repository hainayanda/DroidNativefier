package droid.nayanda.nativefier.serializer;

import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * Created by nayanda on 18/03/18.
 */

public interface Serializer<TValue> {

    byte[] serialize(@NonNull TValue obj) throws IOException;

    TValue deserialize(@NonNull byte[] bytes) throws IOException, ClassNotFoundException;

}
