package droid.nayanda.nativefier;

import java.io.Serializable;

/**
 * Created by nayanda on 18/03/18.
 */

public class Builder {

    private Builder() {
    }

    public static BitmapNativefierBuilder getBitmapNativefier() {
        return new BitmapNativefierBuilder();
    }

    public static <TJsonObj> JsonNativefierBuilder<TJsonObj> getJsonNativefier() {
        return new JsonNativefierBuilder<>();
    }

    public static <TSerializable extends Serializable> SerializableNativefierBuilder<TSerializable> getSerializableNativefier() {
        return new SerializableNativefierBuilder<>();
    }

    public static <TValue> NativefierBuilder<TValue> getNativefierBuilder() {
        return new NativefierBuilder<>();
    }
}
