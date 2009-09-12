package org.riedelcastro.thebeast.alchemy


import _root_.scala.util.parsing.combinator.{RegexParsers, JavaTokenParsers}
import collection.mutable.{HashSet, ArrayBuffer, HashMap}
import env._
import doubles.{DoubleConstant, DoubleTerm}
import java.io.{Reader}
import tuples.{TupleValues3, TupleValues2}
import vectors.{QuantifiedVectorSum, VectorTerm, VectorSum}
/**
 * @author Sebastian Riedel
 */

object AlchemyParser extends JavaTokenParsers with RegexParsers {
  val LowerCaseID = """[a-z]([a-zA-Z0-9]|_[a-zA-Z0-9])*""" r
  val UpperCaseID = """[A-Z]([a-zA-Z0-9]|_[a-zA-Z0-9])*""" r
  val NumDouble = "-?\\d+(\\.\\d+)?" r
  val NumPosInt = "\\d+"
  val StringLit = "(\\w)*"


  val multiline = "(/\\*(?:.|[\\n\\r])*?\\*/)"

  override val whiteSpace = """(\s|//.+\n|(/\*(?:.|[\n\r])*?\*/))+""" r

  def constantTypeDefinition: Parser[ConstantTypeDefinition] =
    (LowerCaseID ~ "=" ~ "{" ~ repsep(UpperCaseID, ",") ~ "}") ^^
            {case name ~ "=" ~ "{" ~ constants ~ "}" => ConstantTypeDefinition(name, constants)}


  def integerTypeDefinition: Parser[IntegerTypeDefinition] =
    (LowerCaseID ~ "=" ~ "{" ~ NumPosInt ~ "," ~ "..." ~ "," ~ NumPosInt ~ "}") ^^
            {case name ~ "=" ~ "{" ~ from ~ "," ~ "..." ~ "," ~ to ~ "}" => IntegerTypeDefinition(name, from.toInt, to.toInt)}

  def include: Parser[Include] = ("#include" ~> stringLiteral) ^^ {s => Include(s)}

  def mln: Parser[List[Expression]] = rep(expression)

  def database: Parser[List[Atom]] = rep(atom)

  def atom: Parser[Atom] = UpperCaseID ~ "(" ~ termList ~ ")" ^^ {case s ~ "(" ~ terms ~ ")" => Atom(s, terms)}

  def and: Parser[And] = (atom ~ "^" ~ formula) ^^ {case lhs ~ "^" ~ rhs => And(lhs, rhs)}

  def formula: Parser[Formula] = (binary(minPrec) | atomic)

  def expression: Parser[Expression] =
    (weightedFormula | formula | integerTypeDefinition | constantTypeDefinition | include)

  def atomic: Parser[Formula] = (parens | atom)

  def weightedFormula: Parser[WeightedFormula] =
    (NumDouble ~ formula) ^^ {case weight ~ formula => WeightedFormula(weight.toDouble, formula)}

  def termList: Parser[List[Term]] = repsep(term, ",") ^^ {case t => t}

  def term: Parser[Term] = (variable | constant | exclType | plusVariable)

  def variable: Parser[VariableOrType] = LowerCaseID ^^ {s => VariableOrType(s)}

  def constant: Parser[Constant] = UpperCaseID ^^ {s => Constant(s)}

  def exclType: Parser[ExclamationType] = "!" ~> LowerCaseID ^^ {s => ExclamationType(s)}

  def plusVariable: Parser[PlusVariable] = "+" ~> LowerCaseID ^^ {s => PlusVariable(s)}

  def parens: Parser[Formula] = "(" ~> formula <~ ")"

  def deterministic: Parser[WeightedFormula] = formula <~ "." ^^ {f => WeightedFormula(Math.POS_INF_DOUBLE, f)}

  def binaryOp(level: Int): Parser[((Formula, Formula) => Formula)] = {
    level match {
      case 1 =>
        "v" ^^^ {(a: Formula, b: Formula) => Or(a, b)}
      case 2 =>
        "=>" ^^^ {(a: Formula, b: Formula) => Implies(a, b)} |
                "<=>" ^^^ {(a: Formula, b: Formula) => Equivalence(a, b)}
      case 3 =>
        "^" ^^^ {(a: Formula, b: Formula) => And(a, b)}
      case _ => throw new RuntimeException("bad precedence level " + level)
    }
  }

  val minPrec = 1
  val maxPrec = 3

  def binary(level: Int): Parser[Formula] =
    if (level > maxPrec) atomic
    else binary(level + 1) * binaryOp(level)


  def test(test: String) = {
    println(parse(expression, test))
    //println(AlchemyParser.formula(new scala.util.parsing.combinator.lexical.Scanner(test)))    
  }

  trait Expression
  trait Term extends Expression {
    def subterms:Seq[Term] = Seq()
    def allVariables:Set[Variable] = this match {
      case v:Variable => Set(v)
      case _ => subterms.foldLeft(Set[Variable]()) {(r,s) => r ++ s.allVariables} 
    }
  }
  trait Variable extends Term{
    def name:String
  }
  case class Constant(value: String) extends Term
  case class VariableOrType(name: String) extends Variable
  case class ExclamationType(name: String) extends Term
  case class PlusVariable(name: String) extends Variable

  sealed trait Formula extends Expression {
    def subformulas: Seq[Formula] = Seq()
    def allVariables: Set[Variable] = this match {
      case Atom(_,args) => args.foldLeft(Set[Variable]())(_ ++ _.allVariables)
      case _ => this.subformulas.foldLeft(Set[Variable]()) {_ ++ _.allVariables}
    }
  }
  case class WeightedFormula(weight: Double, formula: Formula) extends Formula {
    override def subformulas = Seq(formula)
  }
  case class Atom(predicate: String, args: List[Term]) extends Formula
  case class And(lhs: Formula, rhs: Formula) extends Formula {
    override def subformulas = Seq(lhs, rhs)
  }
  case class Or(lhs: Formula, rhs: Formula) extends Formula {
    override def subformulas = Seq(lhs, rhs)
  }
  case class Implies(lhs: Formula, rhs: Formula) extends Formula {
    override def subformulas = Seq(lhs, rhs)

  }
  case class Equivalence(lhs: Formula, rhs: Formula) extends Formula {
    override def subformulas = Seq(lhs, rhs)
  }

  case class IntegerTypeDefinition(name: String, from: Int, to: Int) extends Expression
  case class ConstantTypeDefinition(name: String, constants: Seq[String]) extends Expression

  case class Include(fileName: String) extends Expression

}


object Test extends Application {
  val test = "10.0 Same(+hallo,!po) /* Hallo\nDu Igel */ ^ \n (Popel(du,igel)) => Same(du, nuss)"

  AlchemyParser.test(test)
  AlchemyParser.test("#include \"Blah.mln\"")

}

class MLN {
  import env.TheBeastImplicits._
  import AlchemyParser._


  /**
   * This loads a set of atoms from the given reader. Note that this will modify
   * the types associated with the predicates mentioned in the reader/file. Also note
   * that before atoms can be loaded an MLN with the mentioned
   * predicates has to be loaded. 
   */
  def loadAtoms(reader: Reader): Env = {
    //this loads atoms from a database file and updates/adds types, predicates etc.
    val atoms = AlchemyParser.parse(AlchemyParser.database, reader)

    null
  }

  def loadMLN(reader: Reader) = {
    val expressions = AlchemyParser.parse(AlchemyParser.mln, reader) match {
      case AlchemyParser.Success(expr, _) => expr
      case _ => null
    }
    for (expr <- expressions) expr match {
      case AlchemyParser.ConstantTypeDefinition(typeName, constants) => {
        val newType = new MutableValues[String]
        newType ++= constants.elements
        values(typeName) = newType
      }
      case AlchemyParser.IntegerTypeDefinition(typeName, from, to) => {
        values(typeName) = new IntRangeValues(from, to)
      }
      case AlchemyParser.Atom(predName, args) => {
        predicates.get(predName) match {
          case Some(Var(varName, values)) =>
          case None => {
            val types = new ArrayBuffer[Values[_]]
            for (arg <- args) arg match {
              case AlchemyParser.VariableOrType(typeName) => {
                types += getType(typeName)
              }
              case AlchemyParser.ExclamationType(typeName) => {
                types += getType(typeName)
                //add uniqueness constraint
              }
            }
            val predicate: Var[_] = types.size match {
              case 0 => throw new RuntimeException("Can't do 0 arguments")
              case 1 => Var(predName, new FunctionValues(types(0), Bools))
              case 2 => Var(predName, new FunctionValues(TupleValues2(types(0), types(1)), Bools))
              case 3 => Var(predName, new FunctionValues(TupleValues3(types(0), types(1), types(2)), Bools))
              case _ => throw new RuntimeException("Can't do more than 3 arguments yet")
            }
            predicates(predName) = predicate
          }
        }
      }
      case AlchemyParser.WeightedFormula(weight, formula) => {
        formulae += toDoubleTerm(formula, weight)
      }
      case f: AlchemyParser.Formula => {
        formulae += toDoubleTerm(f, 1.0)
      }
      case _ =>

    }
  }


  private def boundVariables(formula: Formula): Set[Variable] = {
    formula match {
      case f => {
        f.subformulas.foldLeft(Set[Variable]()) {(r,s)=>r ++ boundVariables(s)}
      }
    }
  }

  private def createVectorSum(formula:VectorTerm,  values:Set[Values[Any]]):QuantifiedVectorSum[Any]  = {
    if (values.size == 0) error("No variable, can't quantify")
    if (values.size == 1) vectorSum(values.elements.next)(x=>formula)
    else vectorSum(values.elements.next){x=>createVectorSum(formula, values - values.elements.next)}
  }

  private def createVectorFormula(formula:Formula):VectorTerm = null

  private def getValues(alchemyVars:Set[Variable]): Set[Values[Any]] = null

  private def toDoubleTerm(formula: Formula, weight: Double): DoubleTerm = {
    //first we find bound variables
    val bound = boundVariables(formula)
    //then we need to find all variables
    val all = formula.allVariables
    //this gives the unbound variables
    val unbound = all -- bound
    //we create a vector sum with unbound variables
    val vectorSum = createVectorSum(createVectorFormula(formula),getValues(unbound))


    //then we create a corresponding vector sum
    //then we create an alchemy Indicator
    DoubleConstant(0.0)
  }

  private def getType(typeName: String): Values[_] = {
    values.getOrElseUpdate(typeName, new MutableValues)
  }

  private val values = new HashMap[String, Values[_]]
  private val predicates = new HashMap[String, Var[_]]
  private val formulae = new ArrayBuffer[DoubleTerm]

}