package org.riedelcastro.thebeast.world

import scala.collection.mutable.HashMap
import org.scalatest.Suite

/**
 * @author Sebastian Riedel
 */

class FunctionSymbol[+T, +R](name: String, domain: Seq[T], range: Seq[R])

object AllFunctions {
  def apply[T, R](domain: Iterable[T], range: Iterable[R]): Stream[Map[T, R]] = {
    domain.foldLeft(Stream.make(1,Map[T, R]()))
              {(result, d) => result.flatMap(f => range.map(r => f + (d -> r)))}
  }
}

trait Signature extends Seq[FunctionSymbol[Any, Any]]

class PossibleWorld extends HashMap[FunctionSymbol[Any, Any], Any => Any] {
  def getFunction(symbol: FunctionSymbol[Any, Any]): Any => Any = apply(symbol)

  def setFunction(symbol: FunctionSymbol[Any, Any], function: Any => Any) =
    +=((symbol, function))

}

trait Scorer {
  def score(world: PossibleWorld): Double
}

case class Atom[T, R](symbol: FunctionSymbol[T, R], from: T, to: R) extends Scorer {
  def score(world: PossibleWorld): Double = if (world.getFunction(symbol)(from) == to) 1.0 else 0.0
}

case class Min[L <: Scorer, R <: Scorer](left: L, right: R) extends Scorer {
  def score(world: PossibleWorld): Double = Math.min(left.score(world), right.score(world))
}

object Example {
  def main(args: Array[String]) {
    val set = new FunctionSymbol("set", Array(1, 2, 3), Array(true, false))
    val scorer = new Min(Atom(set, 1, true), Atom(set, 2, false))
    val world = new PossibleWorld()
    world.setFunction(set, Set(1))
    println(scorer.score(world))
  }
}

