package org.riedelcastro.thebeast.solve


import env.doubles.{Normalize, DoubleTerm}
import env.{Env, MutableBeliefs, MutableEnv, Beliefs}
import util.{Trackable, Util}
/**
 * @author Sebastian Riedel
 */

trait MarginalInference {
  def infer(term: DoubleTerm): Beliefs

}

object ExhaustiveMarginalInference extends MarginalInference with Trackable {


  def infer(term: DoubleTerm) : Beliefs = term match {
    case Normalize(x) => inferExhaustively(x).normalize
    case _ => inferExhaustively(term)
  }

  private def inferExhaustively(term: DoubleTerm) : Beliefs = {
    |**("Exhaustive marginal inference for " + term)

    val beliefs = new MutableBeliefs
    val domain = term.variables.toSeq

    Env.forall(term.variables) {
      env => {
        val score = env(term);
        for (variable <- domain) {
          beliefs.increaseBelief(variable,env(variable), score)
        }
      }
    }

    **|
    beliefs
  }


}