package com.googlecode.thebeast.mln;

import com.googlecode.thebeast.query.Query;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.query.Atom;

import java.util.List;
import java.util.ArrayList;

/**
 * A MLNClause maps a possible world to a feature vector. It contains of a
 * generalized clause, a double variable of this clause which denotes the
 * scaling factor (scaling variable), and a set of at least one variables in the
 * clause which identify the indices within the feature vectors (index
 * variables).
 * <p/>
 * If m is an MLNClause with clause c, scale variable s and index variables i,
 * and w a possible world, then m maps w to a feature vector f that has a
 * component for each possible binding b of the index variables, and the value
 * f_b of this component can be calculated as follows: find all bindings for
 * which (a) the body condition is fulfilled in w and (b) the index variables
 * are are bound to b. For each of these bindings apply the head feature to the
 * ground atoms defined by the head feature domain specification in the clause.
 * Scale the returned value by the value of the scale variable and add it to
 * f_b. For each binding that matches b and does not fulfill the body condition
 * the value 1.0 times the scaling value is added to f_b.
 *
 * @author Sebastian Riedel
 */
public class MLNClause {

  /**
   * The clause of this MLNClause.
   */
  private final Query clause;
  /**
   * The scale variable.
   */
  private final Variable scaleVariable;

  private Atom headAtom;

  //private FeatureFunction featureFunction;

  /**
   * The list of index variables.
   */
  private final List<Variable> indexVariables;

  public MLNClause(final Query clause,
                   final Variable scaleVariable,
                   final List<Variable> indexVariables) {
    this.clause = clause;
    this.scaleVariable = scaleVariable;
    this.indexVariables = new ArrayList<Variable>(indexVariables);
  }
}
