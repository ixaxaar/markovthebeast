package com.googlecode.thebeast.clause;

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
   * Constructor GeneralizedClause creates a new GeneralizedClause with the
   * given head and body atoms.
   *
   * @param head the head atoms of the clause
   * @param body the body atoms of the clause
   */
  public GeneralizedClause(final List<Atom> head,
                           final List<Atom> body) {
    this.head = new ArrayList<Atom>(head);
    this.body = new ArrayList<Atom>(body);
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
}
