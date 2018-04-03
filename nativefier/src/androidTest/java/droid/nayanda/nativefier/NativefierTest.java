package droid.nayanda.nativefier;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import droid.nayanda.nativefier.base.SimpleFetcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NativefierTest {

    private static Nativefier<Model> nativefier;
    private static Nativefier<Model> getNativefier() throws IOException {
        if(nativefier == null) {
            Context appContext = InstrumentationRegistry.getTargetContext();
            nativefier = Builder.<Model>getJsonNativefier().setContext(appContext).setContainerName("model")
                    .setMaxCacheNumber(4).setMaxRamCacheNumber(2)
                    .setJsonObjClass(Model.class)
                    .setFetcher(new SimpleFetcher<Model>() {
                        @Override
                        public Model fetch(@NonNull String key) {
                            if (key.equals("fetch")) return new Model("fetch", 100, true);
                            else return null;
                        }
                    }).createNativefier();
        }
        return nativefier;
    }

    @Test
    public void syncTest() throws Exception {
        getNativefier().clear();
        assertNull(getNativefier().get("1"));
        Model one = new Model("one", 1, true);
        Model two = new Model("two", 2, false);
        Model three = new Model("three", 3, true);
        getNativefier().put("1", one);
        getNativefier().put("2", two);
        getNativefier().put("3", three);
        assertTrue(getNativefier().isExist("1"));
        assertTrue(getNativefier().isExist("2"));
        assertTrue(getNativefier().isExist("3"));
        assertFalse(getNativefier().isExist("4"));
        Model getOne = getNativefier().get("1");
        Model getTwo = getNativefier().get("2");
        Model getThree = getNativefier().get("3");
        Model getFour = getNativefier().get("4");
        assertNotNull(getOne);
        assertNotNull(getTwo);
        assertNotNull(getThree);
        assertNull(getFour);
        assertEquals(one, getOne);
        assertEquals(two, getTwo);
        assertEquals(three, getThree);
        getNativefier().clear();
    }

    @Test
    public void asyncTest() throws Exception {
        getNativefier().clear();
        assertFalse(getNativefier().isExist("fetch"));
        assertNull(getNativefier().get("fetch"));
        Model dummy = new Model("fetch", 100, true);
        final Model[] getDummy = {getNativefier().getOrFetchIfNotFound("fetch")};
        assertEquals(dummy, getDummy[0]);
        assertTrue(getNativefier().isExist("fetch"));
        CountDownLatch latch = new CountDownLatch(1);
        getNativefier().clear();
        assertFalse(getNativefier().isExist("fetch"));
        assertNull(getNativefier().get("fetch"));
        getNativefier().asyncGet("fetch", model -> {
            getDummy[0] = model;
            latch.countDown();
        });
        latch.await(5000, TimeUnit.MILLISECONDS);
        assertEquals(dummy, getDummy[0]);
        getNativefier().clear();
    }
}
