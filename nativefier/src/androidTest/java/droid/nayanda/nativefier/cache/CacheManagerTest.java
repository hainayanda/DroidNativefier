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
            memCacheManager = new MemoryCacheManager<>(4);
        }
        return memCacheManager;
    }

    @Test
    public void diskTest() throws Exception {
        baseTest(getDiskCacheManager());
    }

    @Test
    public void memTest() throws Exception {
        baseTest(getMemCacheManager());
    }

    private void baseTest(CacheManager<CacheModel> cacheManager) throws IOException {
        cacheManager.clear();
        assertNull(cacheManager.get("1"));
        CacheModel one = new CacheModel("one", 1, true);
        CacheModel two = new CacheModel("two", 2, false);
        CacheModel three = new CacheModel("three", 3, true);
        CacheModel four = new CacheModel("four", 4, true);
        CacheModel five = new CacheModel("five", 5, false);
        CacheModel six = new CacheModel("six", 6, true);
        cacheManager.put("1", one);
        cacheManager.put("2", two);
        cacheManager.put("3", three);
        cacheManager.put("4", four);
        cacheManager.put("5", five);
        cacheManager.put("6", six);
        assertFalse(cacheManager.isExist("1"));
        assertFalse(cacheManager.isExist("2"));
        assertTrue(cacheManager.isExist("3"));
        assertTrue(cacheManager.isExist("4"));
        assertTrue(cacheManager.isExist("5"));
        assertTrue(cacheManager.isExist("6"));
        assertFalse(cacheManager.isExist("7"));
        CacheModel getOne = cacheManager.get("1");
        CacheModel getTwo = cacheManager.get("2");
        CacheModel getThree = cacheManager.get("3");
        CacheModel getFour = cacheManager.get("4");
        CacheModel getFive = cacheManager.get("5");
        CacheModel getSix = cacheManager.get("6");
        CacheModel getSeven = cacheManager.get("7");
        assertNull(getOne);
        assertNull(getTwo);
        assertNotNull(getThree);
        assertNotNull(getFour);
        assertNotNull(getFive);
        assertNotNull(getSix);
        assertNull(getSeven);
        assertEquals(three, getThree);
        assertEquals(four, getFour);
        assertEquals(five, getFive);
        assertEquals(six, getSix);
        cacheManager.clear();
    }
}
