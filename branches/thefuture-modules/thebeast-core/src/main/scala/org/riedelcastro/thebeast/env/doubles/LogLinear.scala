package org.riedelcastro.thebeast.env.doubles

import collection.mutable.HashMap
import org.riedelcastro.thebeast.env.vectors.{Vector, VectorDotApp, VectorVar, VectorTerm}
import org.riedelcastro.thebeast.env.{EnvVar, Beliefs}

/**
 */
case class LogLinear(sufficient:VectorTerm, weights:VectorVar, bias:DoubleTerm)
  extends Exp(Sum(Seq(VectorDotApp(sufficient,weights),bias))) {

  def marginalizeLogLinear(incoming:Beliefs[Any,EnvVar[Any]], weights:Weights) : LogLinearBeliefs = {
    //default implementation
    //ground weight variables (throw error if not all weights are defined)
    //create auxiliary problem: $(s == suff) * exp(s dot weight)
    //problem: s would be real valued (multidimensional), so we can do exhaustive inference on it
    //so instead: write additional exhaustive method that evaluates one extra term in addition to the
    //score (i.e. get expectation? no) 

    null
  }

}

class Weights extends HashMap[VectorVar,Vector]

class LogLinearBeliefs