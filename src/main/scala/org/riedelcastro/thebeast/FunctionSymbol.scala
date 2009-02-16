package org.riedelcastro.thebeast


/**
 * @author Sebastian Riedel
 */

class FunctionSymbol[+T, +R](val name: String, val domain: Values[T], val range: Values[R]) {
  override def toString = name
  def toFullString = name + " := " + domain + " -> " + range
}

trait Values[+T] extends Iterable[T] {
  def length = toSeq.length
  def get(index:Int) = toSeq(index)
  def draw(rand:scala.util.Random) = get(rand.nextInt(length))
}

class ValuesProxy[+T](val self:Iterable[T]) extends Values[T] with IterableProxy[T]

case class FunctionSymbolHelper1[T,R](val f:FunctionSymbol[T,R])
        extends (Term[T] => AtomBuilder[T, R]) {
  def apply(t: Term[T]): AtomBuilder[T, R] = AtomBuilder(f,t)
}
case class FunctionSymbolHelper2[T1,T2,R](val f:FunctionSymbol[Tuple2[T1,T2],R])
        extends ((Term[T1],Term[T2]) => AtomBuilder[Tuple2[T1,T2], R]) {
  def apply(t1: Term[T1], t2: Term[T2]): AtomBuilder[Tuple2[T1,T2], R] = AtomBuilder(f,TupleTerm2(t1,t2))
}

