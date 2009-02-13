package org.riedelcastro.thebeast

import scala.collection.mutable.HashMap
import org.scalatest.Suite
import org.scalatest.FunSuite

/**
 * @author Sebastian Riedel
 */

class FunctionSymbol[T, R](val name: String, val domain: collection.Set[T], val range: collection.Set[R])

object AllFunctions {
  def apply[T, R](domain: Iterable[T], range: Iterable[R]): Stream[Map[T, R]] = {
    domain.foldLeft(Stream.make(1, Map[T, R]()))
              {(result, d) => result.flatMap(f => range.map(r => f + (d -> r)))}
  }
}

object Cartesian {
  def cartesianProduct[T](args: Seq[Stream[T]]): Stream[Seq[T]] = {
    args.foldLeft(Stream.make(1, Seq[T]()))
              {(result, elements) => result.flatMap((tuple: Seq[T]) => elements.map(e => tuple ++ Seq(e)))}
  }

  
  def allWorlds(sig: Seq[FunctionSymbol[Any,Any]], obs: PartiallyObservedWorld): Stream[World] = {
    cartesianProduct(sig.map(f => AllFunctions(f.domain.filter(e => !obs.getFunction(f).getObservedDomain()(e)), f.range))).map(
      (tuple: Seq[Map[Any,Any]]) => {
        val world = new MutableWorld;
        for (i <- 0 until tuple.size) {
          world.setFunction(sig(i), WithBackoffObservation[Any, Any](tuple(i), obs.getFunction(sig(i))))
        }
        world
      }
      )
  }

}

trait Signature extends Seq[FunctionSymbol[Any, Any]]

class ExhaustiveMAPSolver {
  def solve(signature: Signature, observation: PartiallyObservedWorld, scorer: Scorer[Score]) {

    //iterate over all possible worlds defined by the signagure consistent with the observation
  }
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
    val f1 = new FunctionSymbol("f1", Set(1, 2, 3), Set(true, false))
    val scorer = new Apply(And, GroundAtomScorer(GroundAtom(f1, 1, true)), GroundAtomScorer(GroundAtom(f1, 2, false)))
    val world = new MutablePartiallyObservedWorld()
//    val observed = new HashMap[Int, Boolean]() with ClosedWorldFunction[Int, Boolean]
//    {val default = false; val domain = f1.domain}
    val observed = new HashMap[Int, Boolean]() with MapAsPartiallyObserved[Int,Boolean]
    observed += (1 -> true)
    world.setFunction(f1, observed)
    //println(scorer.score(world))

    val all = Cartesian.allWorlds(Seq(f1.asInstanceOf[FunctionSymbol[Any,Any]]),world)
    println(all.size)
    println(all.map(w=>scorer.score(w)).mkString(","))

  }
}

