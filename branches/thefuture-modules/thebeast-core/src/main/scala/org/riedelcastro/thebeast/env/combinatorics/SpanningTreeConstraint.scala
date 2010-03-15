package org.riedelcastro.thebeast.env.combinatorics

import org.riedelcastro.thebeast.env.doubles.DoubleTerm
import org.riedelcastro.thebeast.env.{Values, Env, FunctionValue, Term}

/**
 *.A SpanningTreeConstraint is a term that maps graphs to 1 if they are
 * spanning trees over length-number-of nodes, and to 0 otherwise.
 */
case class SpanningTreeConstraint[V](edges:Term[FunctionValue[(V,V),Boolean]],
                                     vertices:Term[FunctionValue[Int,Boolean]]) extends DoubleTerm {
  def ground(env: Env):DoubleTerm = null
  def simplify:DoubleTerm = null
  def upperBound = 0.0
  def subterms = Seq(edges,vertices)

  def eval(env:Env):Option[Double] = {
    //get edges map
    val V = env(vertices).getSources(Some(true))
    val E = env(edges).getSources(Some(true))
    


    Some(0.0)
  }
  def values = Values(0.0, 1.0)
  def variables = edges.variables ++ vertices.variables

}