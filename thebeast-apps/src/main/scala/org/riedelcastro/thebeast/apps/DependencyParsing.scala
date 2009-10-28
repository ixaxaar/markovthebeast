package org.riedelcastro.thebeast.apps

import org.riedelcastro.thebeast.env._
import combinatorics.SpanningTreeConstraint
import vectors.{VectorVar}

/**
 * Created by IntelliJ IDEA.
 * User: riedelcastro
 * Date: Oct 26, 2009
 * Time: 10:55:30 PM
 * To change this template use File | Settings | File Templates.
 */

object DependencyParsing extends TheBeastEnv {
  def main(args: Array[String]): Unit = {
    val maxLength = 200
    val Tokens = Ints(0 until maxLength)
    val Words = new MutableValues[String]()
    val Tags = new MutableValues[String]()
    val length = Var("length", Ints(1 until maxLength))
    val link = Predicate("link", Tokens x Tokens)
    val word = Predicate("word", Tokens x Words)
    val pos = Predicate("pos", Tokens x Tags)

    //first order formulae
    val bias = vectorSum(Tokens, Tokens) {(h, m) =>
      $(link(h, m)) * one_("bias")}
    val wordPair = vectorSum(Tokens, Tokens, Words, Words) {(h, m, h_word, m_word) =>
      $(word(h, h_word) && word(m, m_word) ~> link(h, m)) * one_(h_word, m_word)}
    val posPair = vectorSum(Tokens, Tokens, Words, Words) {(h, m, h_pos, m_pos) =>
      $(pos(h, h_pos) && pos(m, m_pos) ~> link(h, m)) * one_(h_pos, m_pos)}

    val treeConstraint = SpanningTreeConstraint(link, length)

    val weights = VectorVar("weights")
    val linearModel = ((wordPair + posPair + bias) dot weights) + treeConstraint

    //some example data
    val sentence1 = new MutableEnv
    sentence1(length) = 5
    //sentence1.atoms(word) += (0,"The")
    //sentence1.atoms(word) ++= ((1,"man"),(2,"is"))
    sentence1.atoms(word) ++= List("The", "man", "is", "fast").zipWithIndex.map(_.swap)
    sentence1.atoms(pos) ++= List("DT",  "NN",  "VB", "AD").zipWithIndex.map(_.swap)
    println(Words.mkString(","))
    println(Tags.mkString(","))



    null
  }

}