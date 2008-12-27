package com.googlecode.thebeast.query;

import com.googlecode.thebeast.world.Predicate;

import java.util.List;

/**
 * A Factory for atoms and clauses. All atoms and clauses have to be created
 * through this class.
 *
 * @author Sebastian Riedel
 */
public final class ClauseFactory {

  /**
   * The singleton clause factory.
   */
  private static ClauseFactory instance = new ClauseFactory();

  /**
   * Private constructor to create singleton instance.
   */
  private ClauseFactory() {

  }

  /**
   * This method returns the singleton clause factory.
   *
   * @return the single clause factory available in a JVM.
   */
  public static ClauseFactory getInstance() {
    return instance;
  }

  /**
   * Create a new atom.
   *
   * @param pred the predicate of the atom.
   * @param args the argument terms of the atom.
   * @return an atom for the given predicate and arguments.
   */
  public Atom createAtom(final Predicate pred, final List<Term> args) {
    return new Atom(pred, args);
  }

  /**
   * Creates a new clause with the given head and body atoms.
   *
   * @param head the head of the clause.
   * @param body the body of the clause
   * @return a GeneralizedClause with the given head and body atoms.
   */
  public GeneralizedClause createClause(final List<Atom> head,
                                        final List<Atom> body) {
    return new GeneralizedClause(head, body);
  }

  /**
   * Returns a builder for easy construction of clauses that uses the singleton
   * factory.
   *
   * @return a ClauseBuilder that uses global clause factory
   */
  public static ClauseBuilder build() {
    return new ClauseBuilder(instance);
  }


}
