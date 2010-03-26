package org.riedelcastro.thebeast.env.doubles

import collection.mutable.HashMap
import org.riedelcastro.thebeast.env.vectors.{Vector, VectorDotApp, VectorVar, VectorTerm}
import org.riedelcastro.thebeast.solve.ExhaustiveMarginalInference
import org.riedelcastro.thebeast.env._


/**
 */
case class LogLinear(sufficient: VectorTerm, weights: VectorVar, bias: DoubleTerm)
        extends Exp(Sum(Seq(VectorDotApp(sufficient, weights), bias))) {
  def marginalizeLogLinear(incoming: Beliefs[Any, EnvVar[Any]], weightsValue: Vector): Beliefs[Any, EnvVar[Any]] = {
    //default implementation
    val env = new MutableEnv
    //set weight variables in environment
    env(weights) = weightsValue
    //create the grounded term (that doesn't have weight variables)
    val grounded = ground(env)
    //exhaustive inference
    ExhaustiveMarginalInference.marginalizeQueries(grounded, incoming, Set(sufficient))
  }

}

object LogLinearMatch {
  def unapply(term:DoubleTerm):Option[(VectorTerm,VectorVar,DoubleTerm)] = term match {
    case LogLinear(sufficient,weights,bias) => Some((sufficient,weights,bias))
    case _ => None
  }
}

/**
 * A Featurized term is a term that deterministically depends on
 * the value of a feature-vector * weight dot product. Very close
 * to general linear models, but does not require normalization.
 */
trait Featurized extends DoubleTerm {
  /**
   * The feature vector for the given world/env.
   */
  def features(env: Env): Vector

  /**
   * The means/expectations of features given some beliefs for the
   * free variable in the term, and assuming that these beliefs are independent.  
   */
  def means(incoming: Beliefs[Any, EnvVar[Any]]): Vector

  /**
   * The weight vector term.
   */
  def weights: VectorTerm
}

class Weights extends HashMap[VectorVar, Vector]
