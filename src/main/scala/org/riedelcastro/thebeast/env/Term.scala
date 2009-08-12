package org.riedelcastro.thebeast.env

import functions._


/**
 * @author Sebastian Riedel
 */
trait Term[+T] {

  /**
   * The domain of a term is the set of all variables that appear in the term. If possible, this
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
   * Are there no free variables in this term 
   */
  def isGround: Boolean

}

case class Constant[T](val value: T) extends Term[T] {
  def variables = Set.empty

  def values = Values(value)

  def simplify = this

  def ground(env: Env) = this

  override def eval(env: Env) = Some(value)

  override def toString = value.toString

  def isGround = true
}


trait TupleTerm extends scala.Product {
  def isGround : Boolean = {    
    for (i <- 0 until productArity) if (!productElement(i).asInstanceOf[Term[Any]].isGround) return false;
    return true;
  }
}

case class TupleTerm2[T1,T2](_1:Term[T1],_2:Term[T2]) extends Term[Tuple2[T1,T2]] with TupleTerm {
  def eval(env: Env) = {
    val arg1: Option[T1] = _1.eval(env)
    val arg2: Option[T2] = _2.eval(env)
    if (arg1 != None && arg2 != None) Some(Tuple2(arg1.get, arg2.get)) else None
  }

  def values = TupleValues2(_1.values,_2.values)

  def ground(env: Env) = TupleTerm2(_1.ground(env),_2.ground(env))

  def simplify = if (_1.isInstanceOf[Constant[_]] && _2.isInstanceOf[Constant[_]])
    Constant(Tuple2(_1.asInstanceOf[Constant[T1]].value,_2.asInstanceOf[Constant[T2]].value ))
    else this

  def variables = _1.variables ++ _2.variables
}

case class TupleTerm3[T1,T2,T3](_1:Term[T1],_2:Term[T2],_3:Term[T3]) extends Term[Tuple3[T1,T2,T3]] with TupleTerm {
  def eval(env: Env) = {
    val arg1: Option[T1] = _1.eval(env)
    val arg2: Option[T2] = _2.eval(env)
    val arg3: Option[T3] = _3.eval(env)
    if (arg1 != None && arg2 != None && arg3 != None) Some(Tuple3(arg1.get, arg2.get,arg3.get)) else None
  }

  def values = TupleValues3(_1.values,_2.values, _3.values)

  def ground(env: Env) = TupleTerm3(_1.ground(env),_2.ground(env), _3.ground(env))

  def simplify = if (_1.isInstanceOf[Constant[_]] && _2.isInstanceOf[Constant[_]] && _3.isInstanceOf[Constant[_]])
    Constant(Tuple3(_1.asInstanceOf[Constant[T1]].value,_2.asInstanceOf[Constant[T2]].value, _3.asInstanceOf[Constant[T3]].value))
    else this

  def variables = _1.variables ++ _2.variables ++ _3.variables
}



trait BoundedTerm[T] extends Term[T] {
  def upperBound: T
}


trait DoubleTerm extends BoundedTerm[Double] {
  def +(rhs: DoubleTerm) = AddApp(this, rhs)

  def *(rhs: DoubleTerm) = TimesApp(this, rhs)

  def *(rhs: VectorTerm) = VectorScalarApp(rhs, this)

  //def ground(env:Env) : DoubleTerm = super.ground(env).asInstanceOf[DoubleTerm]

  def ground(env: Env): DoubleTerm
}
trait BooleanTerm extends BoundedTerm[Boolean] {
  def @@ = BoolToDoubleCast(this)

  def &&(rhs: BooleanTerm) = AndApp(this, rhs)

  def ~>(rhs: BooleanTerm) = ImpliesApp(this, rhs)

  def ground(env: Env): BooleanTerm


}


trait IntTerm extends BoundedTerm[Int] {
}


case class Var[+T](val name: String, override val values: Values[T]) extends Term[T] with EnvVar[T] {
  def variables = Set(this)

  def simplify = this

  override def toString = name

  def ground(env: Env) = {
    val x = env.eval(this);
    if (x.isDefined) Constant(x.get) else this
  }

  override def eval(env: Env) = env.resolveVar[T](this)


  def isGround = false
}

case class FunApp[T, R](val function: Term[T => R], val arg: Term[T]) extends Term[R] {
  override def eval(env: Env) = {
    val evalFun = env.eval(function);
    val evalArg = env.eval(arg);
    if (evalFun.isDefined && evalArg.isDefined) Some(evalFun.get(evalArg.get)) else None
  }

  def variables = {
    //if we have something like f(1)(2)(3) we should create a funapp variable
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

  def isGround = arg.isGround && function.isGround

  def isAtomic: Boolean = arg.simplify.isInstanceOf[Constant[_]] &&
          (function.isInstanceOf[EnvVar[_]] ||
                  (function.isInstanceOf[FunApp[_, _]] && function.asInstanceOf[FunApp[_, _]].isAtomic))

  def asFunAppVar: FunAppVar[T, R] =
    if (function.isInstanceOf[EnvVar[_]])
      FunAppVar(function.asInstanceOf[EnvVar[T => R]], arg.simplify.asInstanceOf[Constant[T]].value)
    else
      FunAppVar(function.asInstanceOf[FunApp[Any, T => R]].asFunAppVar, arg.asInstanceOf[Constant[T]].value)


  def ground(env: Env) = FunApp(function.ground(env), arg.ground(env))

  override def toString = function.toString + "(" + arg.toString + ")"
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

  override def toString = function.toString + "(" + init + "):" + args


  override def eval(env: Env) = {
    if (args.isEmpty)
      env.eval(init)
    else
      env.eval(FunApp(FunApp(function, Fold(function, args.drop(1), init)), args(0)))
  }


  def isGround = function.isGround && init.isGround && args.forall(x => x.isGround)
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


  def isGround = {
    val env = new MutableEnv
    env += variable -> variable.values.defaultValue
    formula.ground(env).isGround
  }
}


sealed trait EnvVar[+T] {
  /**
   *  The values of a variables are all objects the variable can be assigned to
   */
  def values: Values[T]

}

case class FunAppVar[T, +R](val funVar: EnvVar[T => R], val arg: T) extends EnvVar[R] {
  def of[U](arg: U) = FunAppVar(this.asInstanceOf[EnvVar[U => Any]], arg)

  def values = range

  def range: Values[R] = {
    funVar match {
      case x: Var[_] => x.values.asInstanceOf[FunctionValues[T, R]].range
      case x: FunAppVar[_, _] => x.range.asInstanceOf[FunctionValues[T, R]].range
    }

  }


  
}




