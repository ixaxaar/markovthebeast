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