package thebeast.nodmem.expression;

import thebeast.nod.expression.Expression;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.TupleComponent;
import thebeast.nod.expression.TupleSelector;
import thebeast.nod.type.TupleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemTupleSelector extends AbstractMemExpression<TupleType> implements TupleSelector {

  private ArrayList<TupleComponent> components;
  private HashSet<TupleComponent> asSet;
  private ArrayList<Expression> expressions;

  public MemTupleSelector(TupleType type, List<TupleComponent> components) {
    super(type);
    this.components = new ArrayList<TupleComponent>(components);
    asSet = new HashSet<TupleComponent>(components);
    expressions = new ArrayList<Expression>(components.size());
    for (int i = 0; i < components.size();++i) expressions.add(null);

    for (TupleComponent c : components)
      expressions.set(type.heading().getIndex(c.name()),c.expression());
  }

  public List<TupleComponent> components() {
    return Collections.unmodifiableList(components);
  }

  public List<Expression> expressions() {
    return expressions;
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitTupleSelector(this);
  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemTupleSelector that = (MemTupleSelector) o;

    return this.asSet.equals(that.asSet);

  }

  public int hashCode() {
    return asSet.hashCode();
  }
}
