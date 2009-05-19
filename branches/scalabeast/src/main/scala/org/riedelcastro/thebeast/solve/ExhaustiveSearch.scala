package org.riedelcastro.thebeast.solve


import env.{Var, MutableEnv, Env, Term}
import java.util.Comparator
import reflect.Manifest
import util.Util

/**
 * @author Sebastian Riedel
 */

object ExhaustiveSearch extends ArgmaxSolver with SatisfiabilitySolver {
  def argmax(term: Term[Double]) =
    new ArgmaxResult(search(term.asInstanceOf[Term[Double]], (x: Double, y: Double) => x > y, Math.MIN_DOUBLE))

  def satisfy(term: Term[Boolean]) =
    search(term.asInstanceOf[Term[Boolean]], (x: Boolean, y: Boolean) => x && !y, false)

  def search[T](term: Term[T])(implicit m: Manifest[T]): Env = {
      m.toString match {
        case "int" => search(term.asInstanceOf[Term[Int]], (x: Int, y: Int) => x > y, Math.MIN_INT)
        case "double" => search(term.asInstanceOf[Term[Double]], (x: Double, y: Double) => x > y, Math.MIN_DOUBLE)
        case "boolean" => search(term.asInstanceOf[Term[Boolean]], (x: Boolean, y: Boolean) => x && !y, false)
        case _ => null
      }
  }

  def search[T](term: Term[T], larger: (T, T) => Boolean, init: T): Env = {
    val env = new MutableEnv
    var max: T = init
    var best = new MutableEnv
    val domain = term.domain.toSeq
    val values = domain.map(v => v.values.toStream)
    var cartesian = Util.Cartesian.cartesianProduct(values)

    for (tuple <- cartesian) {
      for (index <- 0 until domain.size) {
        env += (domain(index) -> tuple(index))
      }
      val result = env(term)
      if (max == null || larger(result, max)) {
        max = result
        best = env.clone
      }
    }
    best
  }

}