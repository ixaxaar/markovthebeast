package com.googlecode.thebeast.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Query defines a mapping from a possible world to a set of (nested) variable
 * bindings. This mapping is defined through two conjunctions of atoms: the
 * inner conjunction and the outer conjunction.
 * <p/>
 * Let the outer variables be those variables that appear in the outer
 * conjunction and the inner variables all variables which only appear in the
 * inner conjunction. Then the result of a query applied to a possible world is
 * the largest set of nested substitutions for the outer/inner variables so that
 * the outer conjunction holds for the outer substitution and the inner
 * conjunction holds for all inner substitutions merged with the outer
 * substitution.
 *
 * @author Sebastian Riedel
 */
public final class Query {

  /**
   * The inner conjunction.
   */
  private final List<Atom> innerConjunction;
  /**
   * The outer conjunction.
   */
  private final List<Atom> outerConjunction;

  /**
   * The inner and outer conjunction conjoined.
   */
  private final List<Atom> all;

  /**
   * List of outer variables.
   */
  private final List<Variable>
    outerVariables = new ArrayList<Variable>();

  /**
   * List of inner variables.
   */
  private final List<Variable>
    innerVariables = new ArrayList<Variable>();


  /**
   * Constructor Query creates a new Query with the given inner and outer
   * conjunctions.
   *
   * @param innerConjunction the inner conjunction.
   * @param outerConjunction the outer conjunction.
   */
  Query(final List<Atom> innerConjunction,
        final List<Atom> outerConjunction) {
    this.innerConjunction = new ArrayList<Atom>(innerConjunction);
    this.outerConjunction = new ArrayList<Atom>(outerConjunction);
    this.all = new ArrayList<Atom>();
    all.addAll(outerConjunction);
    all.addAll(innerConjunction);
    for (Atom atom : outerConjunction) {
      for (Term term : atom.getArguments()) {
        if (term instanceof Variable) {
          Variable var = (Variable) term;
          if (!outerVariables.contains(var))
            outerVariables.add(var);
        }
      }
    }
    for (Atom atom : innerConjunction) {
      for (Term term : atom.getArguments()) {
        if (term instanceof Variable) {
          Variable var = (Variable) term;
          if (!outerVariables.contains(var)
            && !innerVariables.contains(var))
            innerVariables.add(var);
        }
      }
    }
  }

  /**
   * Returns the head atoms of the clause.
   *
   * @return a list of atoms representing the head of this clause.
   */
  public List<Atom> getInnerConjunction() {
    return Collections.unmodifiableList(innerConjunction);
  }

  /**
   * Returns the body atoms of the clause.
   *
   * @return a list of atoms representing the body of this clause.
   */
  public List<Atom> getOuterConjunction() {
    return Collections.unmodifiableList(outerConjunction);
  }


  /**
   * Returns the atoms in body and head of this clause as one list. The list is
   * ordered as: body atoms, head atoms.
   *
   * @return a list starting with the body atoms and ending with the head
   *         atoms.
   */
  public List<Atom> getAll() {
    return Collections.unmodifiableList(all);
  }

  /**
   * Returns a list of the universally quantified variables in this clause. The
   * list is ordered by appearance in the body of the clause.
   *
   * @return an unmodifiable view on the list of universal variables in this
   *         clause.
   */
  public List<Variable> getOuterVariables() {
    return Collections.unmodifiableList(outerVariables);
  }

  /**
   * Returns a list of the existentially quantified variables in this clause.
   * The list is ordered by appearance in the head of the clause.
   *
   * @return an unmodifiable view on the list of existential variables in this
   *         clause.
   */
  public List<Variable> getInnerVariables() {
    return Collections.unmodifiableList(innerVariables);
  }


}
