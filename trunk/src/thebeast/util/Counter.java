package thebeast.util;

import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class Counter<T> extends HashMap<T,Integer> {

    public Integer get(Object o) {
        Integer original = super.get(o);
        return original == null ? 0 : original;
    }

    public void increment(T value, int howmuch){
        Integer old = super.get(value);
        put(value,old == null ? howmuch : old + howmuch);
    }
}
