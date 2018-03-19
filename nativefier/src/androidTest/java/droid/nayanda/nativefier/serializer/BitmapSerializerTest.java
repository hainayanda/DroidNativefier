package droid.nayanda.nativefier.serializer;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by nayanda on 19/03/18.
 */

@RunWith(AndroidJUnit4.class)
public class BitmapSerializerTest {

    @Test
    public void serializeTest() throws Exception {
        Serializer<Bitmap> serializer = new BitmapSerializer();
        Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        byte[] bytes = serializer.serialize(bmp);
        Bitmap deBmp = serializer.deserialize(bytes);
        assertEquals(bmp, deBmp);
    }

}