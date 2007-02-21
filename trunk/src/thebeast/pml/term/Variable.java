package thebeast.pml.term;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:29:38
 */
public class Variable extends Term{

  private String name;

  public Variable(Type type, String name) {
    super(type);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void acceptTermVisitor(TermVisitor visitor) {
    visitor.visitVariable(this);
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    Variable variable = (Variable) o;

    return name.equals(variable.name);

  }

  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + name.hashCode();
    return result;
  }

  public boolean isNonPositive() {
    return getType().getTypeClass() == Type.Class.NEGATIVE_DOUBLE;
  }

  public boolean isNonNegative() {
    return getType().getTypeClass() == Type.Class.POSITIVE_DOUBLE;
  }
}
