package org.riedelcastro.thebeast.env.doubles

import org.riedelcastro.thebeast.env._
import booleans.{BooleanVar, BooleanLiteral, Conjunction, Disjunction}
import collection.mutable.HashSet

/**
 * Created by IntelliJ IDEA.
 * User: riedelcastro
 * Date: Oct 29, 2009
 * Time: 10:16:53 PM
 * To change this template use File | Settings | File Templates.
 */

case class WeightedDNF(val dnf: Disjunction[Conjunction[BooleanLiteral]], weight: Double)
    extends Multiplication(Seq(Indicator(dnf), DoubleConstant(weight))) {

  override def variables = Set() ++ (for (con <- dnf.args; lit <- con.args) yield lit.variable)

  override def marginalize(incoming: Beliefs[Any,EnvVar[Any]]) = {
    val incomingBoolVars = incoming.asInstanceOf[Beliefs[Boolean,EnvVar[Boolean]]]
    val result = new MutableBeliefs[Boolean,EnvVar[Boolean]]
    //iterate over conjunctions
    for (conjunction <- dnf.args) {
      //calculate total score that each literal gets (and each literal not mentioned, but for both states)
      var score = 1.0
      val variables = new HashSet[EnvVar[Boolean]]
      for (literal <- conjunction.args) {
        score *= incoming.belief(literal.variable).belief(!literal.negated)
        variables += literal.variable
      }
      //now go over literals in the incoming beliefs that are not part of the
      val remainingVariables = incomingBoolVars.terms.filter(!variables.contains(_)).toSeq
      var totalScore = 0.0
      for (assignmentNr <- 0 until Math.pow(2,remainingVariables.size).toInt){
        var partialScore = score
        for (varIndex <- 0 until remainingVariables.size) {
          partialScore *= incoming.belief(remainingVariables(varIndex)).belief(assignmentNr % Math.pow(2,varIndex) == 0)
        }
        totalScore += partialScore
      }
      //now we can add the total score to the involved beliefs (based on their sign)
      for (literal <- conjunction.args) {
        result.increaseBelief(literal.variable, !literal.negated, totalScore)
      }
      //and the non-involved beliefs)
      for (variable <- remainingVariables) {
        result.increaseBelief(variable, true, totalScore)
        result.increaseBelief(variable, false, totalScore)
      }
    }
    result.asInstanceOf[Beliefs[Any,EnvVar[Any]]]
  }
}