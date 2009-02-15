package org.riedelcastro.thebeast

import scala.collection.mutable.HashMap
import solver.{MAPProblem, ExhaustiveMAPSolver}
/**
 * @author Sebastian Riedel
 */
trait TheBeastApp {
  case class FunctionSymbolBuilder(val name: String) {
    def :=[T, R](domainRange: Tuple2[Iterable[T], Iterable[R]]): FunctionSymbol[T, R] = {
      new FunctionSymbol(name, new ValuesProxy(domainRange._1), new ValuesProxy(domainRange._2))
    }

  }
  implicit def string2FunctionSymbolBuilder(x: String) = new FunctionSymbolBuilder(x)
  implicit def value2Constant[T](x: T) = Constant(x)
  implicit def symbol12Helper[T,R](f: FunctionSymbol[T,R]) = FunctionSymbolHelper1(f)
  implicit def symbol22Helper[T1,T2,R](f: FunctionSymbol[Tuple2[T1,T2],R]) = FunctionSymbolHelper2(f)
  implicit def iterable2CartesianProduct1[T](i: Iterable[T]) = CartesianProduct1(i)
  implicit def atomBuilder2predicateAtom[T](builder:AtomBuilder[T,Boolean]) =
    Atom(builder.f,builder.t,Constant(true))

  def $[T](name: String): Variable[T] = Variable[T](name)
  private[this] var varCount : Int = 0;
  def freshVar[T] : Variable[T] = {varCount+=1; Variable[T]("x" +((varCount).toString))}
}

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


case class CartesianProduct1[T1](val _1:Iterable[T1]) extends Iterable[Tuple1[T1]] {
  def elements = _1.map(v => Tuple1(v)).elements
  def x[T2](_2 : Iterable[T2]) = CartesianProduct2(_1,_2)
}

case class CartesianProduct2[T1,T2](val _1:Iterable[T1], val _2:Iterable[T2])
        extends Iterable[Tuple2[T1,T2]] {
  def elements = toStream.elements
  override def toStream = Cartesian.cartesianProduct(Seq(_1.toStream, _2.toStream)).map(
      seq => Tuple2(seq(0).asInstanceOf[T1],seq(1).asInstanceOf[T2]))
}


case class GroundAtom[T, R](val symbol: FunctionSymbol[T, R], val from: T, val to: R);

case class Node[+T,+R] (val symbol:FunctionSymbol[T,R], val arg:T) extends (World=>R) {
  def apply(world:World) : R = world.getFunction(symbol)(arg)    
}

object Example extends TheBeastApp {
  def main(args: Array[String]) {
    val f1 = "f1" := Set(1, 2, 3) -> Set(true, false)
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
    val result = solver.solve(MAPProblem(Seq(f1), world, scorer))
    println(result.world.getFunction(f1)(1))
    println(result.score)

    val atom = f1($("x"))~>true
    println(atom)

    val f2 = "f2" := (Set(1,2) x Set(3.0,4.0)) -> Set(true,false)
    println(f2.toFullString)
    val x = $("x")
    val y,z,t1 = freshVar
    val formula = f2(x,2) & f2(1,x) |-> f1(x) & f1(2)
    println(formula)
    println((Set(1,2) x Set(3,4)).mkString(","))

  }
}

