package droid.nayanda.nativefier.base;

/**
 * Created by nayanda on 18/03/18.
 */

public interface Task<TParam, TReturn> {
    TReturn run(TParam param);
}
