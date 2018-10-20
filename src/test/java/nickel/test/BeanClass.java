package nickel.test;

import java.util.Objects;

public class BeanClass {
    private int value;

    public BeanClass() {
    }

    public BeanClass(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanClass beanClass = (BeanClass) o;
        return value == beanClass.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
