package droid.nayanda.nativefier.serializer;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import droid.nayanda.nativefier.Model;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SerializableSerializerTest {

    @Test
    public void serializeTest() throws Exception {
        Serializer<Model> serializer = new SerializableSerializer<>();
        Model model = new Model("one", 1, true);
        byte[] bytes = serializer.serialize(model);
        Model deModel = serializer.deserialize(bytes);
        assertEquals(model, deModel);
    }

}