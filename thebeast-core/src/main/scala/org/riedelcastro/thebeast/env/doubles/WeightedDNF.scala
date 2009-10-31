package org.riedelcastro.thebeast.env.doubles

import org.riedelcastro.thebeast.env._
import booleans._
import collection.mutable.{ArrayBuffer, HashSet}

/**
 * Created by IntelliJ IDEA.
 * User: riedelcastro
 * Date: Oct 29, 2009
 * Time: 10:16:53 PM
 * To change this template use File | Settings | File Templates.
 */

case class WeightedDNF(val dnf: DNF[BooleanLiteral], weight: Double)
    extends Multiplication(Seq(Indicator(dnf), DoubleConstant(weight))) {

  object TableRepresentation {
    val vars = variables.toArray.asInstanceOf[Array[EnvVar[Boolean]]]
    val rows = new ArrayBuffer[Array[Boolean]]
    for (con <- dnf.args) {
      val fixedPart = new Array[Boolean](vars.size)
      val argIndices = con.args.map(l=>vars.indexOf(l.variable))
      val noArgIndices = vars.filter(!con.args.map(_.variable).contains(_)).map(vars.indexOf(_))
      for (arg <- con.args) fixedPart(vars.indexOf(arg.variable)) = !arg.negated
      val numberOfNoArgs = vars.size - con.args.size
      val openPart = new Array[Boolean](numberOfNoArgs)
      for (assignmentId <- 0 until Math.pow(2,numberOfNoArgs).toInt){
        //generate/set the states of the open variables
        for (i <- 0 until numberOfNoArgs) 
          if (assignmentId % Math.pow(2,i).toInt == 0) openPart(i) = ! openPart(i)
        val row = new Array[Boolean](vars.size)
        fixedPart.copyToArray(row,0)
        for (i <- 0 until numberOfNoArgs) //noArgIndex <- noArgIndices)
          row(noArgIndices(i)) = openPart(i)     
        if (!rows.exists(_.deepEquals(row)))
          rows += row
      }
    }
  }
  //private lazy val binaryRows

  override def variables = Set() ++ (for (con <- dnf.args; lit <- con.args) yield lit.variable)

  override def marginalize(incoming: Beliefs[Any, EnvVar[Any]]) = {
    val incomingBoolVars = incoming.asInstanceOf[Beliefs[Boolean, EnvVar[Boolean]]]
    val result = new MutableBeliefs[Boolean, EnvVar[Boolean]]
    //iterate over conjunctions
    for (row <- TableRepresentation.rows) {
      var score = weight
      for (argIndex <- 0 until TableRepresentation.vars.size)
        score *= incoming.belief(TableRepresentation.vars(argIndex)).belief(row(argIndex))
      for (argIndex <- 0 until TableRepresentation.vars.size)
        result.increaseBelief(TableRepresentation.vars(argIndex),row(argIndex), score)
    }
    result.asInstanceOf[Beliefs[Any, EnvVar[Any]]]
  }
}