package droid.nayanda.nativefier.serializer;

import android.support.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by nayanda on 18/03/18.
 */

public class SerializableSerializer<TSerializable extends Serializable> implements Serializer<TSerializable> {

    @Override
    public byte[] serialize(@NonNull TSerializable obj) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput output = new ObjectOutputStream(outputStream);
        output.writeObject(obj);
        output.flush();
        return outputStream.toByteArray();
    }

    @Override
    public TSerializable deserialize(@NonNull byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput input = new ObjectInputStream(inputStream);
        return (TSerializable) input.readObject();
    }
}
