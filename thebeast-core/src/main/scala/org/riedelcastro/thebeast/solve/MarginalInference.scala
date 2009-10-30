package org.riedelcastro.thebeast.solve

import org.riedelcastro.thebeast._
import env._
import env.doubles._
import util.{Trackable}
/**
 * @author Sebastian Riedel
 */

trait MarginalInference {
  def infer(term: DoubleTerm): Beliefs[Any,EnvVar[Any]]

}

object ExhaustiveMarginalInference extends MarginalInference with Trackable {


  def infer(term: DoubleTerm) : Beliefs[Any,EnvVar[Any]] = term match {
    case Normalize(x) => inferExhaustively(x).normalize
    case _ => inferExhaustively(term)
  }

  private def inferExhaustively(term: DoubleTerm) : Beliefs[Any,EnvVar[Any]] = {
    |**("Exhaustive marginal inference for " + term)

    val beliefs = new MutableBeliefs[Any,EnvVar[Any]]
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

object OptimizedMarginalizer {
  def marginalize(term:DoubleTerm, incoming: Beliefs[Any,EnvVar[Any]]): Beliefs[Any,EnvVar[Any]] = term match {
    case Exp(Multiplication(Seq(Indicator(x),y:DoubleTerm ))) => null
    case x => defaultMarginalize(x,incoming) 
  }
  def defaultMarginalize(term:DoubleTerm, incoming: Beliefs[Any,EnvVar[Any]]): Beliefs[Any,EnvVar[Any]] = {
    val productTerm = term * Multiplication(term.variables.map(v => BeliefTerm(incoming.belief(v), v)).toSeq)
    ExhaustiveMarginalInference.infer(productTerm)
  }
}