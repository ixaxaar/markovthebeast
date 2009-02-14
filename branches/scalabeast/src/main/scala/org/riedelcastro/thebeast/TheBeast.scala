package org.riedelcastro.thebeast

import scala.collection.mutable.HashMap

/**
 * @author Sebastian Riedel
 */

class FunctionSymbol[+T, +R](val name: String, val domain: Iterable[T], val range: Iterable[R])

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

  def removeAll[T](i1: Iterable[T], i2: Iterable[T]): Iterable[T] =
    i1.filter(e => !i2.exists(_ == e))

  def allWorlds(sig: Seq[FunctionSymbol[Any, Any]], obs: PartiallyObservedWorld): Stream[World] = {
    cartesianProduct(sig.map(
      f => AllFunctions(removeAll(f.domain, obs.getFunction(f).getObservedDomain()), f.range))).map(
      (tuple: Seq[Map[Any, Any]]) => {
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

case class MAPProblem(val signature: Seq[FunctionSymbol[Any, Any]],
                     val observation: PartiallyObservedWorld,
                     val scorer: Scorer[Score]);

case class MAPResult(val world: World, val score: Score) extends Ordered[MAPResult] {
  def compare(a: MAPResult) = score compare a.score
}




case class GroundAtom[T, R](val symbol: FunctionSymbol[T, R], val from: T, val to: R);


object Example {
  def main(args: Array[String]) {
    val f1 = new FunctionSymbol("f1", Set(1, 2, 3), Set(true, false))
    val scorer = new Apply(And, GroundAtomScorer(GroundAtom(f1, 1, true)), GroundAtomScorer(GroundAtom(f1, 2, false)))
    val world = new MutablePartiallyObservedWorld()
    //    val observed = new HashMap[Int, Boolean]() with ClosedWorldFunction[Int, Boolean]
    //    {val default = false; val domain = f1.domain}
    val observed = new HashMap[Int, Boolean]() with MapAsPartiallyObserved[Int, Boolean]
    observed += (1 -> true)
    world.setFunction(f1, observed)
    //println(scorer.score(world))

    val all = Cartesian.allWorlds(Seq(f1), world)
    println(all.size)
    println(all.map(w => scorer.score(w)).mkString(","))

    val solver = new ExhaustiveMAPSolver
    val result = solver.solve(MAPProblem(Seq(f1),world,scorer))
    println(result.world.getFunction(f1)(1))
    println(result.score)
  }
}

