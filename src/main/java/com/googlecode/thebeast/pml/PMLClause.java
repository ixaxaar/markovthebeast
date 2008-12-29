package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Atom;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.IntegerType;
import com.googlecode.thebeast.world.DoubleType;

import java.util.ArrayList;
import java.util.List;

/**
 * A PMLClause maps a possible world to a feature vector. todo
 * <p/>
 * todo: fixing <p/> Let m be a PMLClause with query q, scale variable s and
 * index variable i, and w a possible world, and let r be the result of applying
 * the query q to the world w. Then m maps w to a feature vector f(w) that
 * contains one component for each possible binding b of the index variable i
 * and the value f_b(w) is defined as follows. For each nested substitution s in
 * r for which the outer substitution is consistent with b we add 1.0 to f_b(w)
 * if the outer conjunction of q is false in w when applied to s, and the value
 * of the first order operator applied to the set of ground atoms defined by the
 * target atom and the inner substitutions in s.
 *
 * @author Sebastian Riedel
 */
public class PMLClause {

  /**
   * The scale variable.
   */
  private final Variable scaleVariable;

  /**
   * The index variable.
   */
  private final Variable indexVariable;

  private final List<Atom> body = new ArrayList<Atom>();
  private final List<Atom> restriction = new ArrayList<Atom>();
  private final Atom head;

  private final FirstOrderOperator firstOrderOperator;

  public PMLClause(final List<Atom> body,
                   final Atom head,
                   final List<Atom> restriction,
                   final FirstOrderOperator operator,
                   final Variable indexVariable,
                   final Variable scaleVariable) {

    //check index and scale variables have the right type.
    if (!(indexVariable.getType() instanceof IntegerType))
      throw new ConstructionException("Index variable not integer");

    if (!(scaleVariable.getType() instanceof DoubleType))
      throw new ConstructionException("Scale variable not double");

    //check whether index and scale variables actually appear in the body
    if (!Variable.getAllVariables(body).contains(indexVariable))
      throw new ConstructionException("Index variable not contained in body");

    if (!Variable.getAllVariables(body).contains(scaleVariable))
      throw new ConstructionException("Scale variable not contained in body");

    this.body.addAll(body);
    this.head = head;
    this.restriction.addAll(restriction);
    this.firstOrderOperator = operator;
    this.indexVariable = indexVariable;
    this.scaleVariable = scaleVariable;
  }


  public List<Atom> getBody() {
    return body;
  }

  public List<Atom> getRestriction() {
    return restriction;
  }

  public Atom getHead() {
    return head;
  }

  public Variable getScaleVariable() {
    return scaleVariable;
  }

  public Variable getIndexVariable() {
    return indexVariable;
  }

  public FirstOrderOperator getFirstOrderOperator() {
    return firstOrderOperator;
  }

  private class ConstructionException extends RuntimeException {
    public ConstructionException(String message) {
      super(message);
    }
  }
}
