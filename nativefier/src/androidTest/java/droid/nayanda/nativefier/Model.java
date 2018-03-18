package droid.nayanda.nativefier;

import java.io.Serializable;

/**
 * Created by nayanda on 18/03/18.
 */

public class Model implements Serializable {

    private String name;
    private int number;
    private boolean isTrue;

    Model(String name, int number, boolean isTrue) {
        this.name = name;
        this.number = number;
        this.isTrue = isTrue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Model)) return false;

        Model model = (Model) o;

        return number == model.number
                && isTrue == model.isTrue
                && (name != null ? name.equals(model.name) : model.name == null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + number;
        result = 31 * result + (isTrue ? 1 : 0);
        return result;
    }
}
