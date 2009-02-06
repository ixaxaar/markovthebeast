package org.riedelcastro.thebeast

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
  private[this] val functions = new HashMap[FunctionSymbol[Any, Any], Any => Any]

  def getFunction[T, R](symbol: FunctionSymbol[T, R]): T => R =
    functions(symbol.asInstanceOf[FunctionSymbol[Any, Any]]).asInstanceOf[T => R]

  def setFunction[T, R](symbol: FunctionSymbol[T, R], function: T => R) =
    functions += (symbol.asInstanceOf[FunctionSymbol[Any, Any]] -> function.asInstanceOf[Any => Any])

}

class Observation extends PossibleWorld {
  def getHiddenDomain():Seq[Any] = {null}
  def getObservedDomain():Seq[Any] = {null}
  def observe(symbol: FunctionSymbol[Any,Any]) = {}
  def observeAll(symbol: FunctionSymbol[Any,Any]) = {}

}

class ExhaustiveMAPSolver {
   
}

class Binding {
  private[this] val bindings = HashMap[Variable[Any], Any]()

  def get[T](variable: Variable[T]): T = bindings(variable).asInstanceOf[T]

  def set[T](variable: Variable[T], value: T) = bindings += (variable -> value)
}

trait Type[T] {
  def elements(): Stream[T]
}

case class GroundAtom[T, R](val symbol: FunctionSymbol[T, R], val from: T, val to: R);



object Example {
  def main(args: Array[String]) {
    val set = new FunctionSymbol("set", Array(1, 2, 3), Array(true, false))
    val scorer = new Apply(And,GroundAtomScorer(GroundAtom(set, 1, true)), GroundAtomScorer(GroundAtom(set, 2, false)))
    val world = new PossibleWorld()
    world.setFunction(set, Set(1))
    println(scorer.score(world))
  }
}

