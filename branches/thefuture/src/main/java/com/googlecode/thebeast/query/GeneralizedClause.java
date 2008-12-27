package com.googlecode.thebeast.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A GeneralizedClause contains two lists of atoms: the head and the body. Let
 * h1(x), h2(x) etc. be the atoms in the head and b1(x) ... the atoms in the
 * body, then the FOL meaning of a generalized clause is
 * <pre>
 *  forall x. b1(x) & b2(x) ... => exists y. h1(x,y) & h2(x,y) ...
 * </pre>
 * That is, all free variables in the body are universally quantified and all
 * remaining free variables in the head are existentially quantified.
 *
 * @author Sebastian Riedel
 */
public final class GeneralizedClause {

  /**
   * The head atoms.
   */
  private final List<Atom> head;
  /**
   * The body atoms.
   */
  private final List<Atom> body;

  /**
   * The list of all atoms (body, head)
   */
  private final List<Atom> all;

  /**
   * List of universally quantified variables.
   */
  private final List<Variable>
    universalVariables = new ArrayList<Variable>();

  /**
   * List of existentially quantified variables.
   */
  private final List<Variable>
    existentialVariables = new ArrayList<Variable>();


  /**
   * Constructor GeneralizedClause creates a new GeneralizedClause with the
   * given head and body atoms.
   *
   * @param head the head atoms of the clause
   * @param body the body atoms of the clause
   */
  GeneralizedClause(final List<Atom> head,
                    final List<Atom> body) {
    this.head = new ArrayList<Atom>(head);
    this.body = new ArrayList<Atom>(body);
    this.all = new ArrayList<Atom>();
    all.addAll(body);
    all.addAll(head);
    for (Atom atom : body) {
      for (Term term : atom.getArguments()) {
        if (term instanceof Variable) {
          Variable var = (Variable) term;
          if (!universalVariables.contains(var))
            universalVariables.add(var);
        }
      }
    }
    for (Atom atom : head) {
      for (Term term : atom.getArguments()) {
        if (term instanceof Variable) {
          Variable var = (Variable) term;
          if (!universalVariables.contains(var)
            && !existentialVariables.contains(var))
            existentialVariables.add(var);
        }
      }
    }
  }

  /**
   * Returns the head atoms of the clause.
   *
   * @return a list of atoms representing the head of this clause.
   */
  public List<Atom> getHead() {
    return Collections.unmodifiableList(head);
  }

  /**
   * Returns the body atoms of the clause.
   *
   * @return a list of atoms representing the body of this clause.
   */
  public List<Atom> getBody() {
    return Collections.unmodifiableList(body);
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
  public List<Variable> getUniversalVariables() {
    return Collections.unmodifiableList(universalVariables);
  }

  /**
   * Returns a list of the existentially quantified variables in this clause.
   * The list is ordered by appearance in the head of the clause.
   *
   * @return an unmodifiable view on the list of existential variables in this
   *         clause.
   */
  public List<Variable> getExistentialVariables() {
    return Collections.unmodifiableList(existentialVariables);
  }


}
