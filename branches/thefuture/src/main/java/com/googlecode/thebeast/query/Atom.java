package com.googlecode.thebeast.query;

import com.googlecode.thebeast.world.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An Atom object represents a First Order Logic Atom: a predicate symbol with a
 * sequence of argument terms.
 *
 * @author Sebastian Riedel
 */
public final class Atom {

  /**
   * The predicate of this atom.
   */
  private final Predicate predicate;

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
  Atom(final Predicate predicate, final List<Term> arguments) {
    this.predicate = predicate;
    this.arguments =
      Collections.unmodifiableList(new ArrayList<Term>(arguments));
  }

  /**
   * Method getPredicate returns the predicate of this atom.
   *
   * @return the predicate (type UserPredicate) of this Atom object.
   */
  public Predicate getPredicate() {
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
