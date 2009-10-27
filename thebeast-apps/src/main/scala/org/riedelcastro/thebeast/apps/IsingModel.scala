package org.riedelcastro.thebeast.apps


import org.riedelcastro.thebeast.env.{Predicate, Ints, TheBeastEnv}
import org.riedelcastro.thebeast.env.doubles.Uniform

/**
 * @author Sebastian Riedel
 */

object IsingModel extends TheBeastEnv {
  def main(args: Array[String]) = {
    val n = 10
    val m = 10
    val Rows = Ints(0 until n)
    val Cols = Ints(0 until m)
    val node = Predicate("node", Rows x Cols)
    val ising = normalize(exp(sum(Rows, Cols) {(x, y) => $(node(x, y)) * Uniform(-2.0,2.0)} +
            sum(Ints(0 until n - 1), Cols) {(x, y) => $(node(x, y) <~> node(x + 1, y)) * 0.5} +
            sum(Rows, Ints(0 until m - 1)) {(x, y) => $(node(x, y) <~> node(x, y + 1)) * 0.5}))

    //now what? run sum product
    null


  }
}