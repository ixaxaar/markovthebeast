package thebeast.nodmem.value;

import thebeast.nod.value.Value;
import thebeast.nod.type.Type;
import thebeast.nod.util.LineValuePrinter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public abstract class AbstractValue<T extends Type> implements Value<T> {

    protected T type;

    protected AbstractValue(T type) {
        this.type = type;
    }

    public T type() {
        return type;
    }

    public String toString(){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        LineValuePrinter printer = new LineValuePrinter(new PrintStream(os));
        acceptValueVisitor(printer);
        return new String(os.toByteArray());
    }
}
