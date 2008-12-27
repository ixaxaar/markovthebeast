package com.googlecode.thebeast.mln;

import com.googlecode.thebeast.query.Query;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.query.Atom;

import java.util.List;
import java.util.ArrayList;

/**
 * A MLNClause maps a possible world to a feature vector. It contains of a
 * query, a double variable of the outer variables of the query which denotes
 * the scaling factor (scaling variable), a set of at least one variables in the
 * outer variables of the query which identify the indices within the feature
 * vectors (index variables), an target atom and a first order operator.
 * <p/>
 * Let m be an MLNClause with query q, scale variable s and index variables i,
 * and w a possible world, and let r be the result of applying the query q to
 * the world w. Then m maps w to a feature vector f(w) that contains one
 * component for each possible binding b of the variables i and the value f_b(w)
 * is defined as follows. For each nested substitution s in r for which the
 * outer substitution is consistent with b we add 1.0 to f_b(w) if the outer
 * conjunction of q is false in w when applied to s, and the value of the first
 * order operator applied to the set of ground atoms defined by the target atom
 * and the inner substitutions in s.
 *
 * @author Sebastian Riedel
 */
public class MLNClause {

  /**
   * The query of this MLNClause.
   */
  private final Query query;
  /**
   * The scale variable.
   */
  private final Variable scaleVariable;

  private Atom targetAtom;

  private FirstOrderOperator firstOrderOperator;

  //private FeatureFunction featureFunction;

  /**
   * The list of index variables.
   */
  private final List<Variable> indexVariables;

  public MLNClause(final Query query,
                   final Variable scaleVariable,
                   final List<Variable> indexVariables) {
    this.query = query;
    this.scaleVariable = scaleVariable;
    this.indexVariables = new ArrayList<Variable>(indexVariables);
  }
}
