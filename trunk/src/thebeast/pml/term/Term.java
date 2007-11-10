package thebeast.pml.term;

import thebeast.pml.Type;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:22:56
 */
public abstract class Term {

  private Type type;

  protected Term(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public abstract void acceptTermVisitor(TermVisitor visitor);

  public boolean usesWeights(){
    return false;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Term term = (Term) o;

    return type.equals(term.type);

  }

  public int hashCode() {
    return type.hashCode();
  }

  public String toString(){
    return new TermPrinter(this).getResult();
  }

  public abstract boolean isNonPositive();
  public abstract boolean isNonNegative();

  public boolean isFree(){
    return !isNonNegative() && !isNonPositive();
  }


}
