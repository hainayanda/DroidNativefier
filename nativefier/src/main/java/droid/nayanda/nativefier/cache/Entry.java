package droid.nayanda.nativefier.cache;

import android.support.annotation.NonNull;

/**
 * Created by nayanda on 04/04/18.
 */

public class Entry<TValue> {
    private final String key;
    private final TValue value;

    public Entry(@NonNull String key, @NonNull TValue value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public TValue getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entry)) return false;

        Entry entry = (Entry) o;

        return key.equals(entry.key) && value.equals(entry.value);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
