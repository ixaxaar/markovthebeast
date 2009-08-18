package org.riedelcastro.thebeast.solve


import env.doubles.DoubleTerm
import env.{MutableBeliefs, MutableEnv, Beliefs}
import util.{Trackable, Util}
/**
 * @author Sebastian Riedel
 */

trait MarginalInference {
  def infer(term: DoubleTerm): Beliefs

}

object ExhaustiveMarginalInference extends MarginalInference with Trackable {

  def infer(term: DoubleTerm) : Beliefs = {
    |**("Exhaustive marginal inference for " + term)

    val env = new MutableEnv
    val beliefs = new MutableBeliefs
    val domain = term.variables.toSeq
    val values = domain.map(v => v.values.toStream)
    var cartesian = Util.Cartesian.cartesianProduct(values)

    println(domain)

    for (tuple <- cartesian) {
      for (index <- 0 until domain.size) {
        env += (domain(index) -> tuple(index))
      }

      val score = env(term);

      for (variable <- domain) {
        beliefs.increaseBelief(variable,env(variable), score)
      }
      
    }
    **|
    beliefs
  }


}