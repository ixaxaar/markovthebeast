package org.riedelcastro.thebeast.env.doubles

import collection.mutable.HashMap
import org.riedelcastro.thebeast.env.vectors.{Vector, VectorDotApp, VectorVar, VectorTerm}
import org.riedelcastro.thebeast.env.{Term, MutableEnv, EnvVar, Beliefs}
import org.riedelcastro.thebeast.solve.ExhaustiveMarginalInference

/**
 */
case class LogLinear(sufficient:VectorTerm, weights:VectorVar, bias:DoubleTerm)
  extends Exp(Sum(Seq(VectorDotApp(sufficient,weights),bias))) {

  def marginalizeLogLinear(incoming:Beliefs[Any,EnvVar[Any]], weights:Weights) : Beliefs[Any,Term[Any]] = {
    //default implementation
    val env = new MutableEnv
    //set weight variables in environment
    for (variable <- weights.keySet) env(variable) = weights(variable)
    //create the grounded term (that doesn't have weight variables)
    val grounded = ground(env)
    //the queries are the variables plus the sufficient statistics
    val queries = variables ++ Set(sufficient)
    ExhaustiveMarginalInference.marginalizeQueries(grounded,incoming,queries)
  }

}

class Weights extends HashMap[VectorVar,Vector]
