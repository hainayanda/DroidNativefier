package droid.nayanda.nativefier.serializer;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StringSerializerTest {

    @Test
    public void serializeTest() throws Exception {
        StringSerializer<String> serializer = new TestSerializer();
        byte[] bytes = serializer.serialize("test");
        String str = new String(bytes);
        assertEquals("test", str);
        String deStr = serializer.deserialize(bytes);
        assertEquals(str, deStr);
    }

    private class TestSerializer extends StringSerializer<String> {

        @Override
        String serializeToString(@NonNull String obj) {
            return obj;
        }

        @Override
        String deserializeFromString(@NonNull String string) {
            return string;
        }
    }

}