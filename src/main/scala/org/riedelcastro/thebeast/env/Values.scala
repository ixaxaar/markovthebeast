package org.riedelcastro.thebeast.env


import collection.mutable.MapProxy
import util.Util

/**
 * @author Sebastian Riedel
 */
trait Values[+T] extends Iterable[T] {
  def defaultValue: T = elements.next

  def randomValue: T = {val seq = toSeq; seq(new scala.util.Random().nextInt(seq.size))}

  def createVariable(name: String): Var[T] = new Var(name, this)


}

object Values {
  def apply[T](values: T*) =
    new ValuesProxy(values.foldLeft(Set.empty[T]) {(result, v) => result ++ Set(v)})
}

class ValuesProxy[T](override val self: Iterable[T]) extends Values[T] with IterableProxy[T]

case class TupleValues2[T1, T2](_1: Values[T1], _2: Values[T2]) extends Values[Tuple2[T1, T2]] {
  def elements = Util.Cartesian.cartesianProduct(Seq(_1.toStream, _2.toStream)).map(
    seq => Tuple2(seq(0).asInstanceOf[T1], seq(1).asInstanceOf[T2])).elements

  def x[T3](_3: Values[T3]) = TupleValues3(_1, _2, _3)

}
case class TupleValues3[T1, T2, T3](_1: Values[T1], _2: Values[T2], _3: Values[T3]) extends Values[Tuple3[T1, T2, T3]] {
  def elements = Util.Cartesian.cartesianProduct(Seq(_1.toStream, _2.toStream, _3.toStream)).map(
    seq => Tuple3(
      seq(0).asInstanceOf[T1],
      seq(1).asInstanceOf[T2],
      seq(2).asInstanceOf[T3]))
          .elements

  def x[T4](_4: Values[T4]) = TupleValues4(_1, _2, _3, _4)

}

case class TupleValues4[T1, T2, T3, T4](_1: Values[T1], _2: Values[T2], _3: Values[T3], _4: Values[T4])
        extends Values[Tuple4[T1, T2, T3, T4]] {
  def elements = Util.Cartesian.cartesianProduct(Seq(_1.toStream, _2.toStream, _3.toStream, _4.toStream)).map(
    seq => Tuple4(
      seq(0).asInstanceOf[T1],
      seq(1).asInstanceOf[T2],
      seq(2).asInstanceOf[T3],
      seq(3).asInstanceOf[T4]))
          .elements

  def x[T5](_5: Values[T5]) = TupleValues5(_1, _2, _3, _4, _5)

}

case class TupleValues5[T1, T2, T3, T4, T5](_1: Values[T1], _2: Values[T2], _3: Values[T3], _4: Values[T4], _5: Values[T5])
        extends Values[Tuple5[T1, T2, T3, T4, T5]] {
  def elements = Util.Cartesian.cartesianProduct(Seq(_1.toStream, _2.toStream, _3.toStream, _4.toStream, _5.toStream)).map(
    seq => Tuple5(
      seq(0).asInstanceOf[T1],
      seq(1).asInstanceOf[T2],
      seq(2).asInstanceOf[T3],
      seq(3).asInstanceOf[T4],
      seq(4).asInstanceOf[T5]))
          .elements
}


case class FunctionValues[T, R](val domain: Values[T], val range: Values[R]) extends Values[FunctionValue[T, R]] {
  def elements = Util.AllFunctions(domain.toStream, range.toStream).map(m => toFunctionValue(m)).elements

  override lazy val defaultValue = SingletonFunction(this, range.defaultValue)

  def toFunctionValue(map: Map[T, R]): FunctionValue[T, R] = {
    val f = new MutableFunctionValue[T, R](this)
    f ++= map
    f
  }
}

case class SingletonFunction[T, R](val signature: FunctionValues[T, R], val value: R) extends FunctionValue[T, R] {
  def getSources(r: Option[R]) = {
    r match {
      case Some(value) => signature.domain
      case _ => Set()
    }
  }

  def apply(t: T) = value
}


trait FunctionValue[T, R] extends (T => R) {
  def getSources(r: Option[R]): Iterable[T]

  def signature: FunctionValues[T, R]

  def countMatches(that: FunctionValue[T, R]): R => Int = {
    r => this.getSources(Some(r)).foldLeft(0) {
      (count, t) => count + (if (that.getSources(Some(r)).exists(x => x == t)) 1 else 0)
    }
  }
}

class MutableFunctionValue[T, R](val signature: FunctionValues[T, R])
        extends scala.collection.mutable.HashMap[T, R] with FunctionValue[T, R] {
  def getSources(r: Option[R]): Iterable[T] = {
    r match {
      case Some(x) => signature.domain.filter(d => get(d) == Some(x))
      case None => signature.domain.filter(d => !isDefinedAt(d))
    }
  }

  override def clone: MutableFunctionValue[T, R] = {
    val result = new MutableFunctionValue(signature)
    copyTo(result)
    result
  }

  def copyTo(result: scala.collection.mutable.HashMap[T, R]) = {
    foreach {
      case (key, value) =>
        if (value.isInstanceOf[MutableFunctionValue[_, _]])
          result += (key -> value.asInstanceOf[MutableFunctionValue[Any, Any]].clone.asInstanceOf[R])
        else
          result += (key -> value)
    }
  }


  private class ClosedMutableMap(var self: MutableFunctionValue[T, R])
          extends MutableFunctionValue(self.signature) with MapProxy[T, R] {
    override def default(a: T) = signature.range.defaultValue

    override def apply(a: T) = self.get(a) match {
      case Some(x: MutableFunctionValue[_, _]) =>
        x.asInstanceOf[MutableFunctionValue[Any, Any]].close.asInstanceOf[R]

      case Some(_) => super.apply(a)
      case None => default(a)
    }

    override def get(a: T) = {
      self.get(a) match {
        case Some(x: MutableFunctionValue[_, _]) =>
          Some(x.asInstanceOf[MutableFunctionValue[Any, Any]].close.asInstanceOf[R])

        case Some(_) => super.get(a)
        case None => Some(default(a))
      }
    }

    override def clone: ClosedMutableMap = {
      new ClosedMutableMap(self.clone)
    }

    override def getSources(r: Option[R]): Iterable[T] = {
      r match {
        case Some(x) =>
          signature.domain.filter(d => get(d) == Some(x))
        case None => Set[T]()
      }
    }

    override def close = this
  }

  def close: MutableFunctionValue[T, R] = {
    new ClosedMutableMap(this)
  }

}