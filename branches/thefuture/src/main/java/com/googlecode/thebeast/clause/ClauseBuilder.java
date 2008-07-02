package com.googlecode.thebeast.clause;

import com.googlecode.thebeast.world.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * A ClauseBuilder can be used to conveniently create clauses.
 *
 * @author Sebastian Riedel
 */
public final class ClauseBuilder {

  /**
   * The factory to use.
   */
  private ClauseFactory factory;

  /**
   * The atoms that have been produced so far.
   */
  private ArrayList<Atom> atoms = new ArrayList<Atom>();

  /**
   * The atoms of the head
   */
  private ArrayList<Atom> body = new ArrayList<Atom>();

  /**
   * Create a new builder that uses the given factory.
   *
   * @param factory the factory that will be used to create atoms and clauses.
   */
  public ClauseBuilder(ClauseFactory factory) {
    this.factory = factory;
  }

  /**
   * Add a new atom, either to the body (if {@link ClauseBuilder#body()} has not
   * been called yet, or to the head, if it has been called.
   *
   * @param pred the predicate of the atom.
   * @param args the arguments of the atoms. Each capitalized String is
   *             interpreted as a variable, every other string is a constant.
   *             Term objects are added as is. Objects of other types cause an
   *             exception.
   * @return this builder.
   */
  public ClauseBuilder atom(Predicate pred, Object... args) {
    List<Term> argTerms = new ArrayList<Term>();
    for (int i = 0; i < args.length; ++i) {
      if (args[i] instanceof String) {
        String arg = (String) args[i];
        if (Character.isLowerCase(arg.charAt(0))) {
          argTerms.add(new Variable(arg, pred.getArgumentTypes().get(i)));
        } else {
          argTerms.add(pred.getArgumentTypes().get(i).getConstant(arg));
        }
      } else if (args[i] instanceof Term) {
        argTerms.add((Term) args[i]);
      } else {
        throw new UnsupportedOperationException("args must be strings for now");
      }
    }
    atoms.add(factory.createAtom(pred, argTerms));
    return this;
  }

  /**
   * Adds the atoms created so far to the body of the clause to produce.
   *
   * @return this builder.
   */
  public ClauseBuilder body() {
    body.clear();
    body.addAll(atoms);
    atoms.clear();
    return this;
  }

  /**
   * Creates a clause with the atoms create until the last {@link
   * ClauseBuilder#body()} call as body and the ones created until the call of
   * this method as head.
   *
   * @return the produced clause.
   */
  public GeneralizedClause head() {
    GeneralizedClause result = factory.createClause(atoms, body);
    atoms.clear();
    return result;
  }


}
