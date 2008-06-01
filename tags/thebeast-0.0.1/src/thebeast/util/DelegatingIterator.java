package thebeast.util;

import java.util.Iterator;

/**
 * @author Sebastian Riedel
 */
public abstract class DelegatingIterator<C,O> implements Iterator<C> {

    private Iterator<? extends O> original;


    protected DelegatingIterator(Iterator<? extends O> original) {
        this.original = original;
    }

    public boolean hasNext() {
        return original.hasNext();
    }

    protected abstract C convert(O original);

    public C next() {
        return convert(original.next());
    }

    public void remove() {
        original.remove();
    }
}
