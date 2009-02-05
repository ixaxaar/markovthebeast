package org.riedelcastro.thebeast.world

import scala.collection.mutable.HashMap
import org.scalatest.Suite

/**
 * @author Sebastian Riedel
 */

class FunctionSymbol[+T, +R](name: String, domain: Seq[T], range: Seq[R])

object AllFunctions {
  def apply[T, R](domain: Iterable[T], range: Iterable[R]): Stream[Map[T, R]] = {
    domain.foldLeft(Stream.make(1, Map[T, R]()))
              {(result, d) => result.flatMap(f => range.map(r => f + (d -> r)))}
  }
}

trait Signature extends Seq[FunctionSymbol[Any, Any]]

class PossibleWorld extends HashMap[FunctionSymbol[Any, Any], Any => Any] {
  def getFunction(symbol: FunctionSymbol[Any, Any]): Any => Any = apply(symbol)

  def setFunction(symbol: FunctionSymbol[Any, Any], function: Any => Any) =
    +=((symbol, function))

}

abstract class Formula {
  def ground(substitution: Binding): Scorer
}

case class Atom[T, R](symbol: FunctionSymbol[T, R], from: Term[T], to: Term[R]) extends Formula {
  def ground(binding: Binding): Scorer = {
    GroundAtom[T, R](symbol, from.resolve(binding), to.resolve(binding))
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
  def value() : Double;
}

case class DoubleScore(val score:Double) extends Score {
  def value = score
}

case class BoolOneScore(val score:Boolean) extends Score {
  def value = if (score) 1.0 else 0.0
}
case class IntScore(val score:Int) extends Score {
  def value = score
}

trait Scorer {
  def score(world: PossibleWorld): Double
}

case class GroundAtom[T, R](val symbol: FunctionSymbol[T, R], val from: T, to: R) extends Scorer {
  def score(world: PossibleWorld): Double = if (world.getFunction(symbol)(from) == to) 1.0 else 0.0
}

case class BinaryCombinator[L <: Scorer, R <: Scorer](val function: (Double, Double) => Double,
                                                     val left: L,
                                                     val right: R) extends Scorer {
  def score(world: PossibleWorld) = function(left.score(world), right.score(world))
}


case class Min[L <: Scorer, R <: Scorer](val left: L, val right: R) extends Scorer {
  def score(world: PossibleWorld): Double = Math.min(left.score(world), right.score(world))
}

case class FoldLeft(val f: (Double, Double) => Double, scorers: Seq[Scorer]) extends Scorer {
  def score(world: PossibleWorld) = scorers.foldLeft(0.0){(result, scorer) => f(result, scorer.score(world))}
}

case class FormulaScorer(val formula: Formula, val binding: Binding) extends Scorer {
  def score(world: PossibleWorld) = formula.ground(binding).score(world)
}

object Example {
  def main(args: Array[String]) {
    val set = new FunctionSymbol("set", Array(1, 2, 3), Array(true, false))
    val scorer = new Min(GroundAtom(set, 1, true), GroundAtom(set, 2, false))
    val world = new PossibleWorld()
    world.setFunction(set, Set(1))
    println(scorer.score(world))
  }
}

