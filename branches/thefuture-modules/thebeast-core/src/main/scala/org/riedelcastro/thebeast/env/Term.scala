package org.riedelcastro.thebeast.env

import booleans.Equality


/**
 * @author Sebastian Riedel
 */
trait Term[+T] {

  /**
   * todo: make use of this
   * The value type of the variables that can be contained in this term
   */
  type VariableType <: Any

  /**
   * The set of all variables that appear in the term. If possible, this
   * method may return function application variables. 
   */
  def variables: Set[EnvVar[Any]]

  /**
   * The values of a term are all objects the term can be evaluated to
   */
  def values: Values[T]

  /**
   * Returns a term where each function application of a constant function with a constant argument
   * is replaced by a constant representing the application result 
   */
  def simplify: Term[T]

  /**
   * Replace the free variables in this term which are set in the specified environment
   */
  def ground(env: Env): Term[T]

  /**
   * Evaluates this term with respect to the given environment.
   */
  def eval(env: Env): Option[T]

  /**
   * Return a term that evaluates to true iff this and that term evaluate to the same value
   */
  def ===[V>:T](that:Term[V]) = Equality(this,that)

  /**
   *  Are there no free variables in this term
   */
  def isGround: Boolean = subterms.forall(_.isGround)

  /**
   * All immediate subterms of this term
   */
  def subterms: Seq[Term[Any]]

  

  /**
   * todo: This method creates a clone of this object, but with the given set of subterms
   * which are provided in the order that the subterms method provides
   */
  //def cloneWithNewSubterms(subterms:Seq[Term[Any]]): Term[T]

}

/**
 * Grounded is a pattern matcher that matches terms which are ground (no variables), and extracts
 * the value the term evaluates to.
 */
object Grounded {
  def unapply[T](term: Term[T]):Option[T] =
    if (term.isGround) EmptyEnv.eval(term)
    else None
}



case class Constant[T](val value: T) extends Term[T] {
  def variables = Set.empty

  def values = Values(value)

  def simplify = this

  def ground(env: Env) = this

  override def eval(env: Env) = Some(value)

  override def toString = value.toString

  override def isGround = true

  def subterms = Seq()

  def cloneWithNewSubterms(subterms: Seq[Term[Any]]) = new Constant(value)
}




trait BoundedTerm[T] extends Term[T] {
  def upperBound: T
}


object EnvVarMatch {
  def unapply[T](term:Term[T]):Option[EnvVar[T]] = {
    term match {
      case x:EnvVar[_] => Some(x)
      case app @ FunApp(EnvVarMatch(f),Grounded(arg)) =>Some(app.asFunAppVar) 
      case _=> None
    }
  }
}


case class Var[+T](val name: String, override val values: Values[T]) extends Term[T] with EnvVar[T] {
  def variables =
    if (!values.isInstanceOf[FunctionValues[_, _]]) Set(this)
    else Set() ++ createAllFunAppVars(Seq(this), values.asInstanceOf[FunctionValues[_, _]])

  private def createAllFunAppVars(funVars: Iterable[EnvVar[_]], functionValues: FunctionValues[_, _]): Iterable[FunAppVar[_, _]] = {
    val funapps = funVars.flatMap(v => functionValues.domain.map(d => FunAppVar(v.asInstanceOf[EnvVar[Function1[Any, Any]]], d)))
    if (functionValues.range.isInstanceOf[FunctionValues[_, _]])
      createAllFunAppVars(funapps, functionValues.range.asInstanceOf[FunctionValues[_, _]])
    else
      funapps
  }

  def simplify = this

  override def toString = name

  def ground(env: Env) = {
    val x = env.eval(this);
    if (x.isDefined) Constant(x.get) else this
  }

  override def eval(env: Env) = env.resolveVar[T](this)

  override def isGround = false


  def subterms = Seq()


  def cloneWithNewSubterms(subterms: Seq[Term[Any]]) = this

  override def hashCode = 31 * name.hashCode + values.hashCode
  override def equals(obj: Any) = obj match {
    case Var(n,v)=> name == n && values == v
    case _=> false
  }

}

case class FunctionVar[T, R](override val name: String, override val values: FunctionValues[T, R]) extends Var(name, values) {
}

case class Predicate[T](override val name: String, domain: Values[T])
    extends FunctionVar(name, FunctionValues(domain, TheBeastImplicits.Bools))

case class FunApp[T, R](val function: Term[T => R], val arg: Term[T]) extends Term[R] {
  override def eval(env: Env) = {
    val evalFun = env.eval(function);
    val evalArg = env.eval(arg);
    if (evalFun.isDefined && evalArg.isDefined) Some(evalFun.get(evalArg.get)) else None
  }

  def variables = {
    //if we have something like f(1)(2)(3) we should create a funapp variable
    //todo: instead expand function value variables whenever they have no no conrete/constant arguments.
    if (isAtomic)
      Set(asFunAppVar)
    else
      function.variables ++ arg.variables
  }

  def values =
    function.values match {
      case functions: FunctionValues[_, _] => functions.range
      case _ => new ValuesProxy(function.values.flatMap(f => arg.values.map(v => f(v))))
    }


  def simplify =
    function.simplify match {
      case Constant(f) => arg.simplify match {
        case Constant(x) => Constant(f(x));
        case x => FunApp(Constant(f), x)
      }
      case f => FunApp(f, arg.simplify)
    }

  def isAtomic: Boolean = arg.simplify.isInstanceOf[Constant[_]] &&
                          (function.isInstanceOf[EnvVar[_]] ||
                           (function.isInstanceOf[FunApp[_, _]] && function.asInstanceOf[FunApp[_, _]].isAtomic))

  def asFunAppVar: FunAppVar[T, R] =
    if (function.isInstanceOf[EnvVar[_]])
      FunAppVar(function.asInstanceOf[EnvVar[T => R]], arg.simplify.asInstanceOf[Constant[T]].value)
    else
      FunAppVar(function.asInstanceOf[FunApp[Any, T => R]].asFunAppVar, arg.asInstanceOf[Constant[T]].value)


  def ground(env: Env): Term[R] = FunApp(function.ground(env), arg.ground(env))

  override def toString = function.toString + "(" + arg.toString + ")"

  def subterms = Seq(function, arg)


  def cloneWithNewSubterms(subterms: Seq[Term[Any]]) =
    FunApp(subterms(0).asInstanceOf[Term[T => R]], subterms(1).asInstanceOf[Term[T]])
}


case class Fold[R](val function: Term[R => (R => R)], val args: Seq[Term[R]], val init: Term[R]) extends Term[R] {
  def values =
    function.values match {
      case functions: FunctionValues[_, _] => functions.range match {
        case range: FunctionValues[_, _] => range.range.asInstanceOf[Values[R]]
        case _ => new ValuesProxy(recursiveValues(init.values, args))
      }
      case _ => new ValuesProxy(recursiveValues(init.values, args))
    }

  private[this] def recursiveValues(input: Iterable[R], args: Seq[Term[R]]): Iterable[R] = {
    if (args.isEmpty) input else {
      val myValues = for (f <- function.values; x <- input; a <- args(0).values) yield f(x)(a)
      recursiveValues(myValues, args.drop(1))
    }
  }

  def simplify = null

  def variables = function.variables ++ init.variables ++ args.flatMap(a => a.variables)

  def ground(env: Env) = Fold(function.ground(env), args.map(a => a.ground(env)), init.ground(env))

  override def toString = function.toString + "(" + init + "):" + args.mkString(",")


  override def eval(env: Env) = {
    if (args.isEmpty)
      env.eval(init)
    else
      env.eval(FunApp(FunApp(function, Fold(function, args.drop(1), init)), args(0)))
  }


  def subterms = Seq(function, init) ++ args

  def cloneWithNewSubterms(subterms: Seq[Term[Any]]) =
    subterms match {
      case Seq(function, init, args@_*) =>
        Fold(function.asInstanceOf[Term[R => (R => R)]],
            args.map(_.asInstanceOf[Term[R]]),
            init.asInstanceOf[Term[R]])
      case _ => error("subterms incompatible")
    }


}




case class Quantification[R, V](val function: Term[R => (R => R)], val variable: Var[V], val formula: Term[R], val init: Term[R])
    extends Term[R] {
  lazy val unroll = {
    val env = new MutableEnv
    Fold(function, variable.values.map(value => {env += variable -> value; formula.ground(env)}).toSeq, init)
  }

  def simplify = unroll.simplify

  def variables = unroll.variables

  def values = unroll.values

  def ground(env: Env) = unroll.ground(env)

  def eval(env: Env) = unroll.eval(env)

  override def isGround = {
    val env = new MutableEnv
    env += variable -> variable.values.defaultValue
    formula.ground(env).isGround
  }


  def subterms = Seq(formula)
}


trait EnvVar[+T] extends Term[T] {
  /**
   *   The values of a variables are all objects the variable can be assigned to
   */
  def values: Values[T]

}

case class FunAppVar[T, R](val funVar: EnvVar[T => R], val argValue: T)
    extends FunApp(funVar, Constant(argValue))
        with EnvVar[R] {

  override def hashCode = 31 * funVar.hashCode + argValue.hashCode
  override def equals(obj: Any) = obj match {
    case FunAppVar(f,a)=> funVar == f && argValue == a
    case _=> false
  }
}

case class ConditionedTerm[T, C](term: Term[T], condition: Term[C])

class SingletonClass extends Term[SingletonClass] with Values[SingletonClass] {
  def simplify = this

  override def isGround = true

  def variables = Set()

  def ground(env: Env) = this

  def eval(env: Env): Option[SingletonClass] = Some(this)

  def values = this

  def elements = Seq(this).elements

  def subterms = Seq()
}

object Singleton extends SingletonClass {
  override def toString = "Singleton"
}







