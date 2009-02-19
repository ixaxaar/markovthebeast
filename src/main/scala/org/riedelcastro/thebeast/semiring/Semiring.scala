package org.riedelcastro.thebeast.semiring
/**
 * @author Sebastian Riedel
 */

trait Semiring {
  type Element
  val zero:Element
  val one:Element
  val plus:(Element,Element) => Element
  val times:(Element,Element) => Element
}

object TropicalSemiring extends Semiring {
  type Element = Double
  val one = 0.0
  val zero = Math.NEG_INF_DOUBLE
  val times = (x:Double,y:Double) => x + y
  val plus = (x:Double,y:Double) => Math.max(x,y)
}

object RealSemiring extends Semiring {
  type Element = Double
  val one = 1.0
  val zero = 0.0
  val times = (x:Double,y:Double) => x * y
  val plus = (x:Double,y:Double) => x + y
}

object BooleanSemiring extends Semiring {
  type Element = Boolean
  val one = true
  val zero = false
  val times = (x:Boolean,y:Boolean) => x && y
  val plus = (x:Boolean,y:Boolean) => x || y
}