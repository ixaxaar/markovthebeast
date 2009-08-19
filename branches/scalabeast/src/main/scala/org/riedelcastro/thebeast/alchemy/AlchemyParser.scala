package org.riedelcastro.thebeast.alchemy


import collection.mutable.{ArrayBuffer, HashMap}
import env.doubles.DoubleTerm
import env.{Values, Var}
import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
 * @author Sebastian Riedel
 */

object AlchemyParser extends StandardTokenParsers {

  lexical.delimiters ++= List("(", ")", ",")
  lexical.reserved += ("buy", "sell", "shares", "at", "max", "min", "for", "trading", "account")

  def atom:Parser[Atom] = ident ~ "(" ~ termList ~ ")" ^^ {case s ~ "(" ~ terms ~ ")" => Atom(s,terms)}

  def termList:Parser[List[Term]] = repsep(term,",") ^^ {case t => t}

  def term:Parser[Term] = ident ^^ {s => Constant(s)}

  def test(test:String) = {
    println(AlchemyParser.atom(new lexical.Scanner(test)))    
  }

  trait Expression
  trait Term extends Expression
  case class Constant(value:String) extends Term
  case class Variable(name:String) extends Term

  trait Formula extends Expression
  case class Atom(predicate:String,args:List[Term]) extends Formula

}

object Test extends Application {
  val test = "Same(hallo,du,sau)"

  AlchemyParser.test(test)

}

class MLN {
  private val values = new HashMap[String,Values[_]]
  private val predicates = new HashMap[String,Var[_]]
  private val formulae = new ArrayBuffer[DoubleTerm]

}