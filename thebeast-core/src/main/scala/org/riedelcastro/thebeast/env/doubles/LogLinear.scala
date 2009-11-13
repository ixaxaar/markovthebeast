package org.riedelcastro.thebeast.env.doubles

import collection.mutable.HashMap
import org.riedelcastro.thebeast.env.vectors.{Vector, VectorDotApp, VectorVar, VectorTerm}
import org.riedelcastro.thebeast.solve.ExhaustiveMarginalInference
import org.riedelcastro.thebeast.env._

/**
 */
case class LogLinear(sufficient:VectorTerm, weights:VectorVar, bias:DoubleTerm)
  extends Exp(Sum(Seq(VectorDotApp(sufficient,weights),bias))) {

  def marginalizeLogLinear(incoming:Beliefs[Any,EnvVar[Any]], weightsValue:Vector) : Beliefs[Any,EnvVar[Any]] = {
    //default implementation
    val env = new MutableEnv
    //set weight variables in environment
    env(weights) = weightsValue
    //create the grounded term (that doesn't have weight variables)
    val grounded = ground(env)
    //exhaustive inference
    ExhaustiveMarginalInference.marginalizeQueries(grounded,incoming,Set(sufficient))
  }

}

class Weights extends HashMap[VectorVar,Vector]
