package org.riedelcastro.thebeast

/**
 * @author Sebastian Riedel
 */

trait Scorer[+S <: Score] {
  def score(world: World): S

  def domain: Set[Node[Any, Any]]
  def max : S
}

case class GroundAtomScorer[T, R](val atom: GroundAtom[T, R]) extends Scorer[BoolScore] {
  def score(world: World): BoolScore = BoolScore(world.getFunction(atom.symbol)(atom.from) == atom.to)

  def domain = Set(Node(atom.symbol, atom.from))

  def max = BoolScore.TRUE

}

case class Apply[A1 <: Score, A2 <: Score, R <: Score](val combinator: Combinator[A1, A2, R],
                                                      val arg1: Scorer[A1],
                                                      val arg2: Scorer[A2]) extends Scorer[R] {
  def score(world: World) = combinator(arg1.score(world), arg2.score(world))

  def domain = arg1.domain ++ arg2.domain

  def max = combinator.max(arg1.max,arg2.max)
}

case class Fold[T <: Score, R <: Score](val f: Combinator[T, R, T], scorers: Seq[Scorer[R]], val init: T) extends Scorer[T] {
  def score(world: World): T = scorers.foldLeft(init){(result, scorer) => f(result, scorer.score(world))}

  def domain = scorers.foldLeft(Set[Node[Any, Any]]()){(result, scorer) => result ++ scorer.domain}


  def max = scorers.foldLeft(init) {(result,scorer) => f.max(result,scorer.max)}
}

case class FormulaScorer[S <: Score](val formula: Formula[S], val binding: Binding) extends Scorer[S] {
  def score(world: World): S = formula.ground(binding).score(world)

  def domain = formula.ground(binding).domain

  def max = formula.ground(binding).max
}

trait Combinator[-A1 <: Score, -A2 <: Score, +R <: Score] extends ((A1, A2) => R) {
  def max(max1: Score, max2: Score): R
}

object And extends Combinator[Score, Score, BoolScore] {
  override def toString = "And"

  def apply(arg1: Score, arg2: Score): BoolScore = BoolScore(arg1.value > 0 && arg2.value > 0)

  def max(max1: Score, max2: Score) = BoolScore.TRUE
}

object Plus extends Combinator[Score, Score, DoubleScore] {
  override def toString = "Plus"

  def apply(arg1: Score, arg2: Score): DoubleScore = DoubleScore(arg1.value + arg2.value)

  def max(max1: Score, max2: Score) = DoubleScore(max1.value + max2.value)
}

object Implies extends Combinator[Score, Score, BoolScore] {
  override def toString = "Implies"

  def apply(arg1: Score, arg2: Score): BoolScore = BoolScore(arg1.value <= 0 || arg2.value > 0)

  def max(max1: Score, max2: Score) = BoolScore.TRUE
}


object Count extends Combinator[Score, Score, IntScore] {
  def sign(value: Double): Int = if (value > 0.0) 1 else 0

  def max(max1: Score, max2: Score) = IntScore(2)

  def apply(arg1: Score, arg2: Score): IntScore = IntScore(sign(arg1.value) + sign(arg2.value))
}
