package droid.nayanda.nativefier.base;

/**
 * Created by nayanda on 19/03/18.
 */

public abstract class ArgumentsFinisher<TValue, TArgs> implements Finisher<TValue> {

    private TArgs[] args;

    private ArgumentsFinisher() {
    }

    public ArgumentsFinisher(TArgs... args) {
        this.args = args;
    }

    public abstract void onFinished(TValue obj, TArgs... args);

    @Override
    public void onFinished(TValue object) {
        onFinished(object, args);
    }
}
