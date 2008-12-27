package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Atom;
import com.googlecode.thebeast.query.Query;
import com.googlecode.thebeast.query.Variable;

/**
 * A PMLClause maps a possible world to a feature vector. It
 * contains of a query, a double variable of the outer variables of the query
 * which denotes the scaling factor (scaling variable), a set of at least one
 * variables in the outer variables of the query which identify the indices
 * within the feature vectors (index variables), an target atom and a first
 * order operator.
 *
 * <p/> Let m be a PMLClause with query q, scale variable s and
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
   * The query of this clause.
   */
  private final Query query;
  /**
   * The scale variable.
   */
  private final Variable scaleVariable;

  private Atom targetAtom;

  private FirstOrderOperator firstOrderOperator;

  /**
   * The index variable.
   */
  private final Variable indexVariables;

  public PMLClause(final Query query,
                                 final Variable scaleVariable,
                                 final Variable variable) {
    this.query = query;
    this.scaleVariable = scaleVariable;
    this.indexVariables = variable;
  }
}
