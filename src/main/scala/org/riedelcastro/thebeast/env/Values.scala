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

case class FunctionValues[T, R](val domain: Values[T], val range: Values[R]) extends Values[T => R] {
  def elements = Util.AllFunctions(domain.toStream, range.toStream).map(m => toFunctionValue(m)).elements

  override def defaultValue = (t: T) => range.defaultValue

  def toFunctionValue(map: Map[T, R]): FunctionValue[T, R] = {
    val f = new MutableFunctionValue[T, R](this)
    f ++= map
    f
  }

}


trait FunctionValue[T, R] extends (T => R) {
  def getSources(r:Option[R]) : Iterable[T] 
}

class MutableFunctionValue[T, R](val signature: FunctionValues[T, R])
        extends scala.collection.mutable.HashMap[T, R] with FunctionValue[T, R] {

  def getSources(r:Option[R]) : Iterable[T] = {
    r match {
      case Some(x) => signature.domain.filter(d => get(d) == Some(x))
      case None => signature.domain.filter(d => !isDefinedAt(d))
    }
  }

  private class ClosedMutableMap(var self: MutableFunctionValue[T, R], signature: FunctionValues[T, R])
          extends MutableFunctionValue(signature) with MapProxy[T, R] {

    override def default(a: T) = signature.range.defaultValue

    override def apply(a: T) = self.get(a) match {
      case Some(x: MutableFunctionValue[_, _]) => x.asInstanceOf[MutableFunctionValue[Any, Any]].close(
        signature.range.asInstanceOf[FunctionValues[Any, Any]]).asInstanceOf[R]
      case Some(_) => super.apply(a)
      case None => default(a)
    }

    override def getSources(r:Option[R]) : Iterable[T] = {
      r match {
        case Some(x) =>
          signature.domain.filter(d => get(d) == Some(x))
        case None => Set[T]()
      }
    }
    
  }

  def close: MutableFunctionValue[T, R] = {
    new ClosedMutableMap(this, signature)
  }

}