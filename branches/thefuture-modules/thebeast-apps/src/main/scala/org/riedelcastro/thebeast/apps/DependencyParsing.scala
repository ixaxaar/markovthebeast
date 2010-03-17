package org.riedelcastro.thebeast.apps

import org.riedelcastro.thebeast.env._
import org.riedelcastro.thebeast.util._
import combinatorics.SpanningTreeConstraint
import vectors.{Vector, VectorVar}
import org.riedelcastro.thebeast.solve.SumProductBeliefPropagation
import org.riedelcastro.thebeast.env.TheBeastImplicits._

/**
 * Simple Dependency Parsing model
 */
object DependencyParsing extends TheBeastEnv {
  def main(args: Array[String]): Unit = {

    Logging.level = Logging.DEBUG

    val maxLength = 5

    val Tokens = Ints(0 until maxLength)
    val Words = new MutableValues[String]()
    val Tags = new MutableValues[String]()
    val length = Var("length", Ints(1 until maxLength))
    val link = Predicate("link", Tokens x Tokens)
    val word = Predicate("word", Tokens x Words)
    val pos = Predicate("pos", Tokens x Tags)
    val token = Predicate("token", Tokens)


    //first order formulae
    val bias = vectorSum(Tokens, Tokens) {(h, m) =>
      $(link(h, m)) * unit("bias")}
    val wordPair = vectorSum(Tokens, Tokens, Words, Words) {(h, m, h_word, m_word) =>
      $(word(h, h_word) && word(m, m_word) && link(h, m)) * unit(h_word, m_word)}
    val posPair = vectorSum(Tokens, Tokens, Tags, Tags) {(h, m, h_pos, m_pos) =>
      $(pos(h, h_pos) && pos(m, m_pos) && link(h, m)) * unit(h_pos, m_pos)}

    val treeConstraint = SpanningTreeConstraint(link, token, 0, LessThan(Tokens))

    val weightVar = VectorVar("weights")
    //val linearModel = ((wordPair + posPair + bias) dot weightVar) + treeConstraint
    val linearModel = ((wordPair + posPair + bias) dot weightVar) 
    val probModel = normalize(exp(linearModel))

    //some example data
    val sentence1 = new MutableEnv
    sentence1(length) = 5
    sentence1.atoms(word) ++= List("Root", "The", "man", "is", "fast").zipWithIndex.map(_.swap)
    sentence1.atoms(pos) ++=  List("Root", "DT",  "NN",  "VB", "AD").zipWithIndex.map(_.swap)
    sentence1.atoms(link) ++= List((0,3),(3,2),(3,4),(2,1))
    sentence1.close(word,true)
    sentence1.close(pos,true)
    sentence1.close(link,true)

    val weights = new Vector
    weights("bias") = -2.0
    weights("NN","DT") = 1.0
    weights("VB","NN") = 1.0
    weights("Root", "VB") = 1.0
    weights("VB", "AD") = 1.0

    sentence1(weightVar) = weights

    println(Words.mkString(","))
    println(Tags.mkString(","))

    println(sentence1(linearModel))

    //run inference
    val bp = new SumProductBeliefPropagation
    val marginals = bp.infer(probModel.ground(sentence1.mask(Set(link))))


    println(marginals)
  }

}