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

import droid.nayanda.nativefier.CacheModel;
import droid.nayanda.nativefier.DiskUsage;
import droid.nayanda.nativefier.serializer.SerializableSerializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CacheManagerTest {

    private static DiskCacheManager<CacheModel> diskCacheManager;
    private static MemoryCacheManager<CacheModel> memCacheManager;

    private static DiskCacheManager<CacheModel> getDiskCacheManager() throws IOException {
        if (diskCacheManager == null) {
            Context appContext = InstrumentationRegistry.getTargetContext();
            diskCacheManager = new DiskCacheManager<>(appContext, DiskUsage.EXTERNAL, "manager", 4, new SerializableSerializer<>());
        }
        return diskCacheManager;
    }

    private static MemoryCacheManager<CacheModel> getMemCacheManager() throws IOException {
        if (memCacheManager == null) {
            memCacheManager = new MemoryCacheManager<>(2);
        }
        return memCacheManager;
    }

    @Test
    public void diskTest() throws Exception {
        getDiskCacheManager().clear();
        Thread.sleep(5000);
        assertNull(getDiskCacheManager().get("1"));
        CacheModel one = new CacheModel("one", 1, true);
        CacheModel two = new CacheModel("two", 2, false);
        CacheModel three = new CacheModel("three", 3, true);
        getDiskCacheManager().put("1", one);
        getDiskCacheManager().put("2", two);
        getDiskCacheManager().put("3", three);
        Thread.sleep(5000);
        assertTrue(getDiskCacheManager().isExist("1"));
        assertTrue(getDiskCacheManager().isExist("2"));
        assertTrue(getDiskCacheManager().isExist("3"));
        assertFalse(getDiskCacheManager().isExist("4"));
        CacheModel getOne = getDiskCacheManager().get("1");
        CacheModel getTwo = getDiskCacheManager().get("2");
        CacheModel getThree = getDiskCacheManager().get("3");
        CacheModel getFour = getDiskCacheManager().get("4");
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
        getMemCacheManager().clear();
        assertNull(getMemCacheManager().get("1"));
        CacheModel one = new CacheModel("one", 1, true);
        CacheModel two = new CacheModel("two", 2, false);
        CacheModel three = new CacheModel("three", 3, true);
        getMemCacheManager().put("1", one);
        getMemCacheManager().put("2", two);
        getMemCacheManager().put("3", three);
        assertTrue(getMemCacheManager().isExist("1"));
        assertTrue(getMemCacheManager().isExist("2"));
        assertFalse(getMemCacheManager().isExist("3"));
        CacheModel getOne = getMemCacheManager().get("1");
        CacheModel getTwo = getMemCacheManager().get("2");
        CacheModel getThree = getMemCacheManager().get("3");
        assertNotNull(getOne);
        assertNotNull(getTwo);
        assertNull(getThree);
        assertEquals(one, getOne);
        assertEquals(two, getTwo);
        getMemCacheManager().clear();
    }
}
