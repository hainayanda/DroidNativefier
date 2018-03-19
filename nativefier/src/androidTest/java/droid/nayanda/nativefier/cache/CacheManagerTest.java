package droid.nayanda.nativefier.cache;

/**
 * Created by nayanda on 19/03/18.
 */

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import droid.nayanda.nativefier.Model;
import droid.nayanda.nativefier.serializer.JsonSerializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CacheManagerTest {

    private static DiskCacheManager<Model> diskCacheManager;
    private static MemoryCacheManager<Model> memCacheManager;

    private static DiskCacheManager<Model> getDiskCacheManager() throws IOException {
        if (diskCacheManager == null) {
            Context appContext = InstrumentationRegistry.getTargetContext();
            diskCacheManager = new DiskCacheManager<>(appContext, "test", 4, new JsonSerializer<>(Model.class));
        }
        return diskCacheManager;
    }

    private static MemoryCacheManager<Model> getMemCacheManager() throws IOException {
        if (memCacheManager == null) {
            memCacheManager = new MemoryCacheManager<>(2);
        }
        return memCacheManager;
    }

    @Test
    public void diskTest() throws Exception {
        Thread.sleep(1500);
        assertNull(getDiskCacheManager().get("1"));
        Model one = new Model("one", 1, true);
        Model two = new Model("two", 2, false);
        Model three = new Model("three", 3, true);
        getDiskCacheManager().put("1", one);
        getDiskCacheManager().put("2", two);
        getDiskCacheManager().put("3", three);
        Thread.sleep(1500);
        assertTrue(getDiskCacheManager().isExist("1"));
        assertTrue(getDiskCacheManager().isExist("2"));
        assertTrue(getDiskCacheManager().isExist("3"));
        assertFalse(getDiskCacheManager().isExist("4"));
        Model getOne = getDiskCacheManager().get("1");
        Model getTwo = getDiskCacheManager().get("2");
        Model getThree = getDiskCacheManager().get("3");
        Model getFour = getDiskCacheManager().get("4");
        assertNotNull(getOne);
        assertNotNull(getTwo);
        assertNotNull(getThree);
        assertNull(getFour);
        assertEquals(one, getOne);
        assertEquals(two, getTwo);
        assertEquals(three, getThree);
        getDiskCacheManager().clear();
    }

    @Test
    public void memTest() throws Exception {
        Thread.sleep(1500);
        assertNull(getMemCacheManager().get("1"));
        Model one = new Model("one", 1, true);
        Model two = new Model("two", 2, false);
        Model three = new Model("three", 3, true);
        getMemCacheManager().put("1", one);
        getMemCacheManager().put("2", two);
        getMemCacheManager().put("3", three);
        Thread.sleep(1500);
        assertTrue(getMemCacheManager().isExist("1"));
        assertTrue(getMemCacheManager().isExist("2"));
        assertFalse(getMemCacheManager().isExist("3"));
        Model getOne = getMemCacheManager().get("1");
        Model getTwo = getMemCacheManager().get("2");
        Model getThree = getMemCacheManager().get("3");
        assertNotNull(getOne);
        assertNotNull(getTwo);
        assertNull(getThree);
        assertEquals(one, getOne);
        assertEquals(two, getTwo);
        getMemCacheManager().clear();
    }
}
