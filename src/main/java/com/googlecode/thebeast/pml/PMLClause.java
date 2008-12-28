package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Atom;
import com.googlecode.thebeast.query.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 * A PMLClause maps a possible world to a feature vector. todo
 *
 * todo: fixing <p/> Let m be a PMLClause with query q, scale variable s and
 * index variable i, and w a possible world, and let r be the result of applying
 * the query q to the world w. Then m maps w to a feature vector f(w) that
 * contains one component for each possible binding b of the index variable i
 * and the value f_b(w) is defined as follows. For each nested substitution s in
 * r for which the outer substitution is consistent with b we add 1.0 to f_b(w)
 * if the outer conjunction of q is false in w when applied to s, and the value
 * of the first order operator applied to the set of ground atoms defined by the
 * target atom and the inner substitutions in s.
 *
 * @author Sebastian Riedel
 */
public class PMLClause {

  /**
   * The scale variable.
   */
  private final Variable scaleVariable;

  /**
   * The index variable.
   */
  private final Variable indexVariables;

  private final List<Atom> body = new ArrayList<Atom>();
  private final List<Atom> restriction = new ArrayList<Atom>();
  private final Atom head;

  private final FirstOrderOperator firstOrderOperator;

  public PMLClause(List<Atom> body,
                   Atom head,
                   List<Atom> restriction,
                   FirstOrderOperator operator,
                   Variable indexVariable,
                   Variable scaleVariable){
    this.body.addAll(body);
    this.head = head;
    this.restriction.addAll(restriction);
    this.firstOrderOperator = operator;
    this.indexVariables = indexVariable;
    this.scaleVariable = scaleVariable;
  }



}
