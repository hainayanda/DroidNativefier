package droid.nayanda.nativefier;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.IOException;

import droid.nayanda.nativefier.base.Fetcher;
import droid.nayanda.nativefier.serializer.BitmapSerializer;

/**
 * Created by nayanda on 18/03/18.
 */

class BitmapNativefier extends Nativefier<Bitmap> {

    BitmapNativefier(Context context, int maxCacheNumber, Fetcher<Bitmap> fetcher) throws IOException {
        super(context, DiskUsage.EXTERNAL, "img", maxCacheNumber, new BitmapSerializer(), fetcher);
    }
}
