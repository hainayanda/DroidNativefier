package droid.nayanda.nativefier.base;

/**
 * Created by nayanda on 18/03/18.
 */

public interface Finisher<TValue> {
    void onFinished(TValue object);
}
