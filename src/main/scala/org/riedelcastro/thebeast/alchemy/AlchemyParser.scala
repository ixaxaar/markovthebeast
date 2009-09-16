package org.riedelcastro.thebeast.alchemy


import _root_.scala.util.parsing.combinator.{RegexParsers, JavaTokenParsers}
import collection.mutable.{HashSet, ArrayBuffer, HashMap}
import env._
import booleans.{BooleanFunApp, BooleanTerm}
import doubles.{AlchemyIndicator, DoubleConstant, DoubleTerm}
import java.io.{Reader}
import tuples.{TupleValues, TupleTerm, TupleValues3, TupleValues2}
import vectors._

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
    def subterms: Seq[Term] = Seq()

    lazy val allVariables: Set[Variable] = this match {
      case v: Variable => Set(v)
      case _ => subterms.foldLeft(Set[Variable]()) {(r, s) => r ++ s.allVariables}
    }
    lazy val allPlusVariables: Seq[PlusVariable] =
    allVariables.filter(_.isInstanceOf[PlusVariable]).map(_.asInstanceOf[PlusVariable]).toSeq
  }
  trait Variable extends Term {
    def name: String
  }
  case class Constant(value: String) extends Term
  case class VariableOrType(name: String) extends Variable
  case class ExclamationType(name: String) extends Term
  case class PlusVariable(name: String) extends Variable

  sealed trait Formula extends Expression {
    def subformulas: Seq[Formula] = Seq()

    lazy val allVariables: Set[Variable] = this match {
      case Atom(_, args) => args.foldLeft(Set[Variable]())(_ ++ _.allVariables)
      case _ => this.subformulas.foldLeft(Set[Variable]()) {_ ++ _.allVariables}
    }
    lazy val allPlusVariables =
    allVariables.filter(_.isInstanceOf[PlusVariable]).map(_.asInstanceOf[PlusVariable]).toSeq

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
          case Some(Predicate(varName, values)) =>
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
            val predicate:Predicate[Any] = types.size match {
              case 0 => error("Can't do 0 arguments")
              case 1 => Predicate(predName, new FunctionValues(types(0), Bools))
              case 2 => Predicate(predName, new FunctionValues(TupleValues2(types(0), types(1)), Bools))
              case 3 => Predicate(predName, new FunctionValues(TupleValues3(types(0), types(1), types(2)), Bools))
              case _ => error("Can't do more than 3 arguments yet")
            }
            predicates(predName) = predicate
          }
        }
      }
      case AlchemyParser.WeightedFormula(weight, formula) => {
        addFormula(formula, weight)
      }
      case f: AlchemyParser.Formula => {
        addFormula(f, 0.0)
      }
      case _ => error("Don't know how to handle " + expr)

    }
  }


  private def boundVariables(formula: Formula): Set[Variable] = {
    formula match {
      case f => {
        f.subformulas.foldLeft(Set[Variable]()) {(r, s) => r ++ boundVariables(s)}
      }
    }
  }

  private def createVectorSum(formula: VectorTerm, variables: Set[Var[Any]]): QuantifiedVectorSum[Any] = {
    if (variables.size == 0) error("No variable, can't quantify")
    val first = variables.elements.next
    if (variables.size == 1) QuantifiedVectorSum(first, formula)
    else QuantifiedVectorSum(first, createVectorSum(formula, variables - first))
  }

  private def createVectorFormula(formula: Formula): VectorTerm = {
    //first create equivalent boolean formula from formula
    val bool = new BoolTermBuilder().build(formula)
    //then wrap AlchemyIndicator around it (maps to double)
    val indicator = AlchemyIndicator(bool)
    //then multiply by unit vector that has the following index:
    //1_(FORMULA_ID, plus_var1, plus_var2,...)
    null
  }


  private class BoolTermBuilder {
    private val name2var = new HashMap[String, Var[Any]]

    def convertArgs(predName: String, args: List[Term]): Seq[env.Term[Any]] = {
      val domain = getPredicate(predName).values.asInstanceOf[FunctionValues[Any, Boolean]].domain
      args.size match {
        case 0 => error("Can't have zero arguments")
        case 1 => Seq(convertTerm(args(0), domain))
        case _ => for (i <- 0 until args.size) yield
          convertTerm(args(i), domain.asInstanceOf[TupleValues].productElement(i).asInstanceOf[Values[Any]])
      }
    }

    def build(formula: Formula): BooleanTerm = {
      formula match {
        case Atom(name, args) => BooleanFunApp(getPredicate(name), TupleTerm(convertArgs(name, args):_*))
        case And(lhs, rhs) => env.booleans.AndApp(build(lhs), build(rhs))
        case Implies(lhs, rhs) => env.booleans.ImpliesApp(build(lhs), build(rhs))
        case Equivalence(lhs, rhs) => env.booleans.EquivalenceApp(build(lhs), build(rhs))
        case _ => error("We don't support a " + formula + " formula yet")
      }
    }

    def convertTerm(term: Term, values: Values[Any]): env.Term[Any] = {
      term match {
        case Constant(text) => env.Constant(text)
        case PlusVariable(name) => name2var.getOrElseUpdate(name, Var(name, values))
        case VariableOrType(name) => name2var.getOrElseUpdate(name, Var(name, values))
        case _ => error("we don't support a " + term + " term yet")
      }
    }

    def convertKnownVar(variable: Variable) = name2var(variable.name)

    def convertKnownVars(variables: Iterable[Variable]): List[Var[Any]] = variables.map(convertKnownVar(_)).toList

  }

  private def getVariables(alchemyVars: Set[Variable]): Set[Var[Any]] = null

  private def addFormula(formula: Formula, weight: Double): VectorTerm = {
    //the id of this formula
    val id = formulaeIds.getOrElseUpdate(formula, "F" + formulaeIds.size)
    //formula builder
    val builder = new BoolTermBuilder
    //build boolean formula
    val bool = builder.build(formula)
    //creating mapping to real values according to AlchemyIndicator
    val indicator = AlchemyIndicator(bool)
    //multiply with unit vector indexed with formula id and plus variables
    val vectorTerm = VectorScalarApp(
      VectorOne((env.Constant(id) :: builder.convertKnownVars(formula.allPlusVariables): _*)),
      indicator)
    //now we find bound variables
    val bound = boundVariables(formula)
    //then we need to find all variables
    val all = formula.allVariables
    //this gives the unbound variables
    val unbound = all -- bound
    //we create a quantified vector sum with unbound variables
    val converted = if (unbound.size > 0) {
      createVectorSum(vectorTerm, Set(builder.convertKnownVars(unbound): _*))
    } else
      vectorTerm
    //add converted formula
    formulae += converted
    //return
    converted
  }

  def getType(typeName: String): Values[_] = {
    values.getOrElseUpdate(typeName, new MutableValues)
  }

  def getPredicate(name: String): Predicate[Any] = predicates(name)

  def getFormula(index:Int) = formulae(index)

  private val values = new HashMap[String, Values[_]]
  private val formulaeIds = new HashMap[Formula, String]
  private val predicates = new HashMap[String, Predicate[Any]]
  private val formulae = new ArrayBuffer[VectorTerm]
  private val weights = new Vector

}