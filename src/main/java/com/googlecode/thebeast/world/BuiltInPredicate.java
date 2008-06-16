package com.googlecode.thebeast.world;

import java.util.List;

/**
 * A BuiltInPredicate represents a predicate for the interpretation is fixed and
 * already given. That is, if the predicate <code>pred</code> is a
 * BuiltInPredicate than for any sequence of arguments <code>arg1,arg2,...
 * </code> the truth value of <code>pred(arg1,arg2,...)</code> in every possible
 * world is the same and known in advance (i.e. built into thebeast).
 *
 * <p>Examples are the equality relation on constant symbols and natural
 * orderings between numbers etc.
 *
 * @author Sebastian Riedel
 */
public abstract class BuiltInPredicate extends AbstractSymbol
  implements Predicate {

  /**
   * Create a new BuiltInPredicate with the given name and in the given
   * signature.
   *
   * @param name      the name of the predicate
   * @param signature the signature this predicate should belong to.
   */
  protected BuiltInPredicate(final String name, final Signature signature) {
    super(name, signature);
  }

  /**
   * This method has to return true if the relation of this built-in predicate
   * contains the specified argument tuple.
   *
   * @param arguments the arguments <code>arg1,arg2,... </code> in a list.
   * @return the truth value of <code>pred(arg1,arg2,...)</code> in every
   *         possible world.
   */
  public abstract boolean evaluate(final List<Constant> arguments);

}
