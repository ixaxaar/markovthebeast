package org.riedelcastro.thebeast
/**
 * @author Sebastian Riedel
 */

abstract class Formula[S <: Score] {
  def ground(substitution: Binding): Scorer[S]
}

case class Atom[T, R](symbol: FunctionSymbol[T, R], from: Term[T], to: Term[R]) extends Formula[BoolScore] {
  def ground(binding: Binding): Scorer[BoolScore] = {
    GroundAtomScorer[T, R](GroundAtom(symbol, from.resolve(binding), to.resolve(binding)))
  }
}
