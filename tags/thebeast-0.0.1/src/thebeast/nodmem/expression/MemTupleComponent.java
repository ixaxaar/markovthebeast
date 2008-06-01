package thebeast.nodmem.expression;

import thebeast.nod.expression.Expression;
import thebeast.nod.expression.TupleComponent;

/**
 * @author Sebastian Riedel
 */
public class MemTupleComponent implements TupleComponent {

    private String name;
    private Expression expression;

    public MemTupleComponent(String name, Expression expression) {
        this.name = name;
        this.expression = expression;
    }

    public String name() {
        return name;
    }

    public Expression expression() {
        return expression;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemTupleComponent that = (MemTupleComponent) o;

        return expression.equals(that.expression) && name.equals(that.name);

    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 31 * result + expression.hashCode();
        return result;
    }
}
