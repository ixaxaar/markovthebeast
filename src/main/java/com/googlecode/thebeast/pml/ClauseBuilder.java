package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Atom;
import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.UserPredicate;

import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class ClauseBuilder {

  private ArrayList<Atom> atoms = new ArrayList<Atom>();
  private ArrayList<Atom> body = new ArrayList<Atom>();
  private ArrayList<Atom> restriction = new ArrayList<Atom>();
  private Atom head;

  private QueryFactory factory;

  public ClauseBuilder(QueryFactory factory) {
    this.factory = factory;
  }

  public ClauseBuilder atom(Predicate pred, Object... args) {
    atoms.add(factory.createAtom(pred, args));
    return this;
  }

  public ClauseBuilder body() {
    body.addAll(atoms);
    atoms.clear();
    return this;
  }

  public ClauseBuilder retrict() {
    restriction.addAll(atoms);
    atoms.clear();
    return this;
  }

  public ClauseBuilder head(UserPredicate pred, Object... args) {
    head = factory.createAtom(pred, args);
    return this;
  }

  public PMLClause clause(FirstOrderOperator operator,
                          Variable indexVariable,
                          Variable scaleVariable) {

    PMLClause result = new PMLClause(body, head, restriction,
      operator, indexVariable, scaleVariable);
    body.clear();
    head = null;
    restriction.clear();
    atoms.clear();
    return result;
  }


}
