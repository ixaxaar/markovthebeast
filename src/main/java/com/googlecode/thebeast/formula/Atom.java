package com.googlecode.thebeast.formula;

import com.googlecode.thebeast.world.UserPredicate;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * An Atom object represents a First Order Logic Atom: a predicate symbol with a
 * sequence of argument terms.
 *
 * @author Sebastian Riedel
 */
public class Atom implements Formula {

  /**
   * The predicate of this atom.
   */
  private final UserPredicate predicate;

  /**
   * The argument terms of this atom.
   */
  private final List<Term> arguments;

  /**
   * Constructor Atom creates a new atom with the given predicate and
   * arguments.
   *
   * @param predicate the predicate of this atom.
   * @param arguments the arguments of this atom.
   */
  public Atom(UserPredicate predicate, List<Term> arguments) {
    this.predicate = predicate;
    this.arguments =
      Collections.unmodifiableList(new ArrayList<Term>(arguments));
  }

  /**
   * Returns an empty list since an Atom is a terminal symbol of a FOL formula.
   *
   * @return the empty list.
   * @see Formula#getChildren()
   */
  public List<Formula> getChildren() {
    return Collections.emptyList();
  }

  /**
   * Method getPredicate returns the predicate of this atom.
   *
   * @return the predicate (type UserPredicate) of this Atom object.
   */
  public UserPredicate getPredicate() {
    return predicate;
  }

  /**
   * Returns the argument terms of this atom.
   *
   * @return a list of argument terms in proper order.
   */
  public List<Term> getArguments() {
    return arguments;
  }
}
