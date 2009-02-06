package org.riedelcastro.thebeast.world

import scala.collection.mutable.HashMap
import org.scalatest.Suite

/**
 * @author Sebastian Riedel
 */

class FunctionSymbol[+T, +R](val name: String, val domain: Seq[T], val range: Seq[R])

object AllFunctions {
  def apply[T, R](domain: Iterable[T], range: Iterable[R]): Stream[Map[T, R]] = {
    domain.foldLeft(Stream.make(1, Map[T, R]()))
              {(result, d) => result.flatMap(f => range.map(r => f + (d -> r)))}
  }
}

trait Signature extends Seq[FunctionSymbol[Any, Nothing]]

class PossibleWorld {
  val functions = new HashMap[FunctionSymbol[Any, Any], Any => Any]

  def getFunction[T, R](symbol: FunctionSymbol[T, R]): T => R =
    functions(symbol.asInstanceOf[FunctionSymbol[Any, Any]]).asInstanceOf[T => R]

  def setFunction[T, R](symbol: FunctionSymbol[T, R], function: T => R) =
    functions += (symbol.asInstanceOf[FunctionSymbol[Any, Any]] -> function.asInstanceOf[Any => Any])

}

abstract class Formula[S <: Score] {
  def ground(substitution: Binding): Scorer[S]
}

case class Atom[T, R](symbol: FunctionSymbol[T, R], from: Term[T], to: Term[R]) extends Formula[BoolScore] {
  def ground(binding: Binding): Scorer[BoolScore] = {
    GroundAtomScorer[T, R](GroundAtom(symbol, from.resolve(binding), to.resolve(binding)))
  }
}

class Binding {
  private val bindings = HashMap[Variable[Any], Any]()

  def get[T](variable: Variable[T]): T = bindings(variable).asInstanceOf[T]

  def set[T](variable: Variable[T], value: T) = bindings += (variable -> value)
}

trait Type[T] {
  def elements(): Stream[T]
}

trait Term[+T] {
  def resolve(binding: Binding): T
}

case class Variable[+T](val name: String) extends Term[T] {
  def resolve(binding: Binding) = binding.get(this)
}

case class Constant[+T](val value: T) extends Term[T] {
  def resolve(binding: Binding) = value
}

case class FunApp[T, R](val f: T => R, val arg: Term[T]) extends Term[R] {
  def resolve(binding: Binding) = f(arg.resolve(binding))
}

trait Score {
  def value(): Double;
}

case class DoubleScore(val score: Double) extends Score {
  def value = score
}

case class BoolScore(val score: Boolean) extends Score {
  def value = if (score) 1.0 else 0.0
}
case class IntScore(val score: Int) extends Score {
  def value = score
}

trait Scorer[S <: Score] {
  def score(world: PossibleWorld): S
}

case class GroundAtom[T, R](val symbol: FunctionSymbol[T, R], val from: T, val to: R);

case class GroundAtomScorer[T, R](val atom: GroundAtom[T, R]) extends Scorer[BoolScore] {
  def score(world: PossibleWorld): BoolScore = BoolScore(world.getFunction(atom.symbol)(atom.from) == atom.to)
}

object And extends ((Score, Score) => BoolScore) {
  def apply(arg1: Score, arg2: Score): BoolScore = BoolScore(arg1.value > 0 && arg2.value > 0)
}

object Count extends ((Score, Score) => IntScore) {
  def sign(value: Double): Int = if (value > 0.0) 1 else 0

  def apply(arg1: Score, arg2: Score): IntScore = IntScore(sign(arg1.value) + sign(arg2.value))
}

case class Apply[A1 <: Score, A2 <: Score, R <: Score](val function: (A1, A2) => R,
                                                      val arg1: Scorer[A1],
                                                      val arg2: Scorer[A2]) extends Scorer[R]{
  def score(world: PossibleWorld) = function(arg1.score(world),arg2.score(world))
}

case class Fold[T <: Score, R <: Score](val f: (T, R) => T, scorers: Seq[Scorer[R]], val init: T) extends Scorer[T] {
  def score(world: PossibleWorld): T = scorers.foldLeft(init){(result, scorer) => f(result, scorer.score(world))}
}

case class FormulaScorer[S <: Score](val formula: Formula[S], val binding: Binding) extends Scorer[S] {
  def score(world: PossibleWorld): S = formula.ground(binding).score(world)
}

object Example {
  def main(args: Array[String]) {
    val set = new FunctionSymbol("set", Array(1, 2, 3), Array(true, false))
    val scorer = new Apply(And,GroundAtomScorer(GroundAtom(set, 1, true)), GroundAtomScorer(GroundAtom(set, 2, false)))
    val world = new PossibleWorld()
    world.setFunction(set, Set(1))
    println(scorer.score(world))
  }
}

