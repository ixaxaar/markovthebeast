package thebeast.nodmem.value;

import thebeast.nod.type.Type;
import thebeast.nod.util.LineValuePrinter;
import thebeast.nod.util.TabularValuePrinter;
import thebeast.nod.value.Value;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public abstract class AbstractMemValue<T extends Type> implements Value<T> {

    protected T type;

    protected AbstractMemValue(T type) {
        this.type = type;
    }

    public T type() {
        return type;
    }

    public String toString(){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        TabularValuePrinter printer = new TabularValuePrinter(new PrintStream(os));
        acceptValueVisitor(printer);
        return new String(os.toByteArray());
    }

    public abstract void copyFrom(AbstractMemValue v);

    public void clear(){
        
    }

}
