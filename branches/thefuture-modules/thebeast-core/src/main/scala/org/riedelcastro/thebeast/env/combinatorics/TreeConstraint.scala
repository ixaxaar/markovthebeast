package org.riedelcastro.thebeast.env.combinatorics

import org.riedelcastro.thebeast.env.doubles.DoubleTerm
import org.riedelcastro.thebeast.env.ints.IntTerm
import org.riedelcastro.thebeast.env.{Values, Env, FunctionValue, Term}

/**
 *.A TreeConstraint is a term that maps graphs to 0 if they are trees, and to negative infinity otherwise.
 */
case class TreeConstraint[V](graph:Term[FunctionValue[(V,V),Boolean]], length:Term[Int]) extends DoubleTerm {
  def ground(env: Env):DoubleTerm = null
  def simplify:DoubleTerm = null
  def upperBound = 0.0
  def subterms = Seq(graph,length)
  def eval(env:Env):Option[Double] = Some(0.0)
  def values = Values(0.0, Math.NEG_INF_DOUBLE)
  def variables = graph.variables ++ length.variables

}