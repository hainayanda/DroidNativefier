package droid.nayanda.nativefier.serializer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by nayanda on 18/03/18.
 */

public class BitmapSerializer implements Serializer<Bitmap> {

    @Override
    public byte[] serialize(@NonNull Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public Bitmap deserialize(@NonNull byte[] bytes) throws IOException, ClassNotFoundException {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
