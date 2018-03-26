package droid.nayanda.nativefier;

import java.io.Serializable;

/**
 * Created by nayanda on 26/03/18.
 */

public class CacheModel implements Serializable {

    private String name;
    private int number;
    private boolean isTrue;

    public CacheModel(String name, int number, boolean isTrue) {
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
        if (!(o instanceof CacheModel)) return false;

        CacheModel that = (CacheModel) o;

        if (number != that.number) return false;
        if (isTrue != that.isTrue) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + number;
        result = 31 * result + (isTrue ? 1 : 0);
        return result;
    }
}