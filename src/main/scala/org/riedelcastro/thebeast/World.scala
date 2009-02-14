package org.riedelcastro.thebeast

import _root_.scala.collection.MapProxy
import _root_.scala.collection.mutable.HashMap

/**
 * @author Sebastian Riedel
 */

trait World {
  def getFunction[T, R](symbol: FunctionSymbol[T, R]): T => R
}

trait PartiallyObservedFunction[T, R] extends (T => R) {
  def getObservedDomain(): Iterable[T]
}

trait ConsistentWithObservation[T, R] extends (T => R) {
  val observation: PartiallyObservedFunction[T, R]

  abstract override def apply(v: T) = {
    if (observation.getObservedDomain().exists(t => t == v)) observation(v) else super.apply(v)
  }
}

case class WithBackoffObservation[T, R](val function: (T => R), val observation: PartiallyObservedFunction[T, R])
        extends (T => R) {
  override def apply(v: T) = {
    if (observation.getObservedDomain().exists(t => t == v)) observation(v) else function(v)
  }
}

trait MapAsPartiallyObserved[T, R] extends PartiallyObservedFunction[T, R] with scala.collection.Map[T, R] {
  def getObservedDomain(): Iterable[T] = keySet
}

trait PartiallyObservedWorld extends World {
  def getFunction[T, R](symbol: FunctionSymbol[T, R]): PartiallyObservedFunction[T, R]
}

trait ClosedWorldFunction[T, R] extends PartiallyObservedFunction[T, R] with scala.collection.Map[T, R] {
  val default: R
  val domain: Iterable[T]

  override def default(key: T) = default

  def getObservedDomain() = domain
}

case class ClosedWorldProxy[T,R] (val observedDomain:Iterable[T], val self:Map[T,R])
        extends PartiallyObservedFunction[T,R] with MapProxy[T,R] {
  def getObservedDomain() = observedDomain
}

case class OpenWorldProxy[T,R] (val self:Map[T,R])
        extends PartiallyObservedFunction[T,R] with MapProxy[T,R] {
  def getObservedDomain() = keySet
}


class MutableWorld extends World {
  private[this] val functions = new HashMap[FunctionSymbol[Any, Any], Any => Any]

  def setFunction[T, R](symbol: FunctionSymbol[T, R], function: T => R) =
    functions += (symbol.asInstanceOf[FunctionSymbol[Any, Any]] -> function.asInstanceOf[Any => Any])

  def getFunction[T, R](symbol: FunctionSymbol[T, R]): T => R =
    functions(symbol.asInstanceOf[FunctionSymbol[Any, Any]]).asInstanceOf[T => R]
}

class MutablePartiallyObservedWorld extends PartiallyObservedWorld {
  private[this] val functions = new HashMap[FunctionSymbol[Any, Any], PartiallyObservedFunction[Any, Any]]

  def setFunction[T, R](symbol: FunctionSymbol[T, R], function: PartiallyObservedFunction[T, R]) =
    functions += (symbol.asInstanceOf[FunctionSymbol[Any, Any]]
            -> function.asInstanceOf[PartiallyObservedFunction[Any, Any]])

  def getFunction[T, R](symbol: FunctionSymbol[T, R]): PartiallyObservedFunction[T, R] =
    functions(symbol.asInstanceOf[FunctionSymbol[Any, Any]]).asInstanceOf[PartiallyObservedFunction[T, R]]
}

