package org.riedelcastro.thebeast.semiring
/**
 * @author Sebastian Riedel
 */

trait SemiringTransformation[E1,E2,S1 <: Semiring[E1],S2 <: Semiring[E2]] extends (E1 => E2) {
  def domain:S1
  def range:S2
}

trait Homomorphism[E1,E2,S1 <: Semiring[E1],S2 <: Semiring[E2]] extends SemiringTransformation[E1,E2,S1,S2]

sealed class Log extends Homomorphism[Double,Double,PositiveRealSemiring, TropicalSemiring] {
  def apply(arg:Double) = Math.log(arg)

  def domain = PositiveRealSemiring

  def range = TropicalSemiring
}

sealed class ToFullReal extends Homomorphism[Double,Double,PositiveRealSemiring, RealSemiring] {
  def apply(arg:Double) = arg
  def domain = PositiveRealSemiring
  def range = RealSemiring
}

object ToFullReal extends ToFullReal

object Log extends Log

sealed class Soften extends SemiringTransformation[Boolean,Double, BooleanSemiring, RealSemiring] {
  def apply(arg:Boolean) = if (arg) 1.0 else 0.0

  def domain = BooleanSemiring
  def range = RealSemiring
}

object Soften extends Soften

