package org.riedelcastro.thebeast
/**
 * @author Sebastian Riedel
 */

trait Scorer[+S <: Score] {
  def score(world: World): S
}

case class GroundAtomScorer[T, R](val atom: GroundAtom[T, R]) extends Scorer[BoolScore] {
  def score(world: World): BoolScore = BoolScore(world.getFunction(atom.symbol)(atom.from) == atom.to)
}

case class Apply[A1 <: Score, A2 <: Score, R <: Score](val function: (A1, A2) => R,
                                                      val arg1: Scorer[A1],
                                                      val arg2: Scorer[A2]) extends Scorer[R]{
  def score(world: World) = function(arg1.score(world),arg2.score(world))
}

case class Fold[T <: Score, R <: Score](val f: (T, R) => T, scorers: Seq[Scorer[R]], val init: T) extends Scorer[T] {
  def score(world: World): T = scorers.foldLeft(init){(result, scorer) => f(result, scorer.score(world))}
}

case class FormulaScorer[S <: Score](val formula: Formula[S], val binding: Binding) extends Scorer[S] {
  def score(world: World): S = formula.ground(binding).score(world)
}

object And extends ((Score, Score) => BoolScore) {
  override def toString = "And"
  def apply(arg1: Score, arg2: Score): BoolScore = BoolScore(arg1.value > 0 && arg2.value > 0)
}

object Implies extends ((Score, Score) => BoolScore) {
  override def toString = "Implies"
  def apply(arg1: Score, arg2: Score): BoolScore = BoolScore(arg1.value <= 0 || arg2.value > 0)
}


object Count extends ((Score, Score) => IntScore) {
  def sign(value: Double): Int = if (value > 0.0) 1 else 0

  def apply(arg1: Score, arg2: Score): IntScore = IntScore(sign(arg1.value) + sign(arg2.value))
}
