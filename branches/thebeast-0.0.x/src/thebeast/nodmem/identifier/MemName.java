package thebeast.nodmem.identifier;

import thebeast.nod.identifier.Name;
import thebeast.nod.identifier.IdentifierVisitor;

/**
 * @author Sebastian Riedel
 */
public class MemName implements Name {

    private String name;
    private MemName qualifier;


    public MemName(String name) {
        int period = name.lastIndexOf('.');
        if (period == -1) {
            this.name = name;
        } else {
            qualifier = new MemName(name.substring(0, period));
            this.name = name.substring(period);
        }
    }

    public MemName(String name, MemName qualifier) {
        this.name = name;
        this.qualifier = qualifier;
    }

    public String name() {
        return name;
    }

    public Name qualifier() {
        return qualifier;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemName that = (MemName) o;

        if (!name.equals(that.name)) return false;

        //noinspection RedundantIfStatement
        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = name.hashCode();
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        return result;
    }

    public String toString() {
        return qualifier == null ? name : qualifier.toString() + "." + name;
    }

    public void acceptIdentifierVisitor(IdentifierVisitor visitor) {
        visitor.visitName(this);

    }
}
