package org.riedelcastro.thebeast.env


import solve.ExhaustiveSearch

/**
 * @author Sebastian Riedel
 */
sealed trait Term[+T] {

  /**
   * The domain of a term is the set of all variables that appear in the term. If possible, this
   * method may return function application variables. 
   */
  def domain: Iterable[EnvVar[Any]]

  /**
   * The values of a term are all objects the term can be evaluated to
   */
  def values: Values[T]

  /**
   * Returns a term where each function application of a constant function with a constant argument
   * is replaced by a constant representing the application result 
   */
  def simplify: Term[T]

}

case class Constant[+T](val value: T) extends Term[T] {
  def domain = Set.empty

  def values = Values(value)

  def simplify = this


  override def toString = value.toString
}

case class Var[+T](val name: String, override val values: Values[T]) extends Term[T] with EnvVar[T] {
  def domain: Iterable[EnvVar[T]] = Set(this)

  def simplify = this


  override def toString = name
}



case class FunApp[T, +R](val function: Term[T => R], val arg: Term[T]) extends Term[R] {
  def domain = {
    //if we have something like f(1)(2)(3) we should create a funapp variable
    if (isGround)
      Set(asFunAppVar)
    else
      function.domain ++ arg.domain
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

  def isGround: Boolean = arg.isInstanceOf[Constant[_]] &&
          (function.isInstanceOf[EnvVar[_]] ||
                  (function.isInstanceOf[FunApp[_, _]] && function.asInstanceOf[FunApp[_, _]].isGround))

  def asFunAppVar: FunAppVar[T, R] =
    if (function.isInstanceOf[EnvVar[_]])
      FunAppVar(function.asInstanceOf[EnvVar[T => R]], arg.asInstanceOf[Constant[T]].value)
    else
      FunAppVar(function.asInstanceOf[FunApp[Any, T => R]].asFunAppVar, arg.asInstanceOf[Constant[T]].value)


  override def toString = function.toString + "(" + arg.toString + ")"
}

case class Fold[T, R](val function: Term[R => (T => R)], val args: Seq[Term[T]], val init: Term[R]) extends Term[R] {
  def values =
    function.values match {
      case functions: FunctionValues[_, _] => functions.range match {
        case range: FunctionValues[_, _] => range.range.asInstanceOf[Values[R]]
        case _ => new ValuesProxy(recursiveValues(init.values, args))
      }
      case _ => new ValuesProxy(recursiveValues(init.values, args))
    }

  private[this] def recursiveValues(input: Iterable[R], args: Seq[Term[T]]): Iterable[R] = {
    if (args.isEmpty) input else {
      val myValues = for (f <- function.values; x <- input; a <- args(0).values) yield f(x)(a)
      recursiveValues(myValues, args.drop(1))
    }
  }

  def simplify = null

  def domain = function.domain ++ init.domain ++ args.flatMap(a => a.domain)

  override def toString = function.toString + "(" + init + "):" + args
}

case class Quantification[T, R, V](val function: Term[R => (T => R)], val variable: Var[V], val formula: Term[T], val init: Term[R])
        extends Term[R] {
  lazy val grounded = {
    val env = new MutableEnv
    Fold(function, variable.values.map(value => {env += variable -> value; env.ground(formula)}).toSeq, init)
  }

  def simplify = grounded.simplify

  def domain = grounded.domain

  def values = grounded.values
}


sealed trait EnvVar[+T] {
  /**
   * The values of a variables are all objects the variable can be assigned to
   */
  def values: Values[T]

}

case class FunAppVar[T, +R](val funVar: EnvVar[T => R], val arg: T) extends EnvVar[R] {
  def of[U](arg: U) = FunAppVar(this.asInstanceOf[EnvVar[U => Any]], arg)

  def values = range

  def range : Values[R] = {
    funVar match {
      case x:Var[_] => x.values.asInstanceOf[FunctionValues[T,R]].range
      case x:FunAppVar[_,_] => x.range.asInstanceOf[FunctionValues[T,R]].range
    }

  }

}




trait TheBeastEnv {
  private var varCount = 0;

  private def createVariable[T](values: Values[T]) = {
    varCount += 1;
    values.createVariable("x_" + varCount.toString)
  }

  implicit def string2varbuilder(name: String) = new {
    def in[T](values: Values[T]) = Var(name, values)

    //def in[T, R](values: FunctionValues[T, R]) = FunVar(name, values)
  }



  //def ground(variable:Var[T], t:T)

  implicit def termToTermBuilder[T](term: Term[T]) = TermBuilder(term)


  implicit def value2constant[T](value: T) = Constant(value)

  case class FunAppVarBuilder[T, R](val funvar: EnvVar[T => R]) {
    def of(t: T) = FunAppVar(funvar, t)
  }

  case class TermBuilder[T](val term: Term[T]) {
    def ===(rhs: Term[T]): Term[Boolean] = FunApp(FunApp(Constant(new EQ[T]), term), rhs)
  }

  case class FunctionValuesBuilder[T, R](domain: Values[T]) {
    def ->[R](range: Values[R]) = new FunctionValues(domain, range)
  }

  implicit def funvar2funAppVarBuilder[T, R](funvar: EnvVar[T => R]) = FunAppVarBuilder(funvar)

  implicit def term2funAppBuilder[T, R](fun: Term[T => R]) = new (Term[T] => FunApp[T, R]) {
    def apply(t: Term[T]) = FunApp(fun, t)
  }

  //  implicit def term2eqBuilder[T](lhs: Term[T]) = new {
  //    def ===(rhs: Term[T]) = TermEq(lhs, rhs)
  //  }

  implicit def values2FunctionValuesBuilder[T, R](domain: Values[T]): FunctionValuesBuilder[T, R] =
    FunctionValuesBuilder[T, R](domain)


  def ^[T](t: T) = Constant(t)

  //  implicit def term2envVar[T](env:Term[T]): EnvVar[T] = {
  //    env match {
  //      case FunApp(f,Constant(v)) => FunAppVar(term2envVar(f),v)
  //      case _=> null
  //    }
  //  }

  implicit def intTerm2IntAppBuilder(lhs: Term[Int]) = new {
    def +(rhs: Term[Int]) = FunApp(FunApp(Constant(IntAdd), lhs), rhs)
  }

  implicit def doubleTerm2DoubleTermBuilder(lhs: Term[Double]) = new {
    def +(rhs: Term[Double]) = FunApp(FunApp(Constant(Add), lhs), rhs)
    def *(rhs: Term[Double]) = FunApp(FunApp(Constant(Times), lhs), rhs)
  }


  implicit def boolTerm2BoolAppBuilder(lhs: Term[Boolean]) = new {
    def @@ = FunApp(Constant(CastBoolToDouble),lhs)

    def &&(rhs: Term[Boolean]) = FunApp(FunApp(Constant(And),lhs),rhs)
    def ->(rhs: Term[Boolean]) = FunApp(FunApp(Constant(Implies),lhs),rhs)
  }

  def $(term:Term[Boolean]) = FunApp(Constant(CastBoolToDouble),term)


  def intSum[T](values: Values[T])(formula: Var[T] => Term[Int]) = {
    val variable = createVariable(values)
    Quantification(IntAdd, variable, formula(variable), 0)
  }

  def sum[T](values: Values[T])(formula: Var[T] => Term[Double]) = {
    val variable = createVariable(values)
    Quantification(Add, variable, formula(variable), 0.0)
  }


  def forall[T](values: Values[T])(formula: Var[T] => Term[Boolean]) = {
    val variable = createVariable(values)
    Quantification(And, variable, formula(variable), true)
  }

  def forall[T1, T2](values1: Values[T1], values2: Values[T2])(formula: (Var[T1], Var[T2]) => Term[Boolean]) = {
    val v1 = createVariable(values1)
    val v2 = createVariable(values2)
    Quantification(And, v1, Quantification(And, v2, formula(v1, v2), true), true)
  }

  def exists[T](values: Values[T])(formula: Var[T] => Term[Boolean]) = {
    val variable = createVariable(values)
    Quantification(Or, variable, formula(variable), false)
  }

}

class EQ[T] extends (T => (T => Boolean)) {
  def apply(lhs: T): (T => Boolean) = (rhs: T) => lhs == rhs;
  override def toString = "Equals"
}

object IntAdd extends (Int => (Int => Int)) {
  def apply(arg1: Int): (Int => Int) = (arg2: Int) => arg1 + arg2
  override def toString = "IntAdd"
}

object Add extends (Double => (Double => Double)) {
  def apply(arg1: Double): (Double => Double) = (arg2: Double) => arg1 + arg2
  override def toString = "Add"
}

object Times extends (Double => (Double => Double)) {
  def apply(arg1: Double): (Double => Double) = (arg2: Double) => arg1 * arg2
  override def toString = "Times"
}


object And extends (Boolean => (Boolean => Boolean)) {
  def apply(arg1: Boolean): (Boolean => Boolean) = (arg2: Boolean) => arg1 && arg2

  override def toString = "And"
}

object Or extends (Boolean => (Boolean => Boolean)) {
  def apply(arg1: Boolean): (Boolean => Boolean) = (arg2: Boolean) => arg1 || arg2

  override def toString = "Or"
}

object Implies extends (Boolean => (Boolean => Boolean)) {
  def apply(arg1: Boolean): (Boolean => Boolean) = (arg2: Boolean) => !arg1 || arg2

  override def toString = "Implies"
}


object CastBoolToDouble extends (Boolean => Double) {

  def apply(bool:Boolean) = if (bool) 1.0 else 0.0
  override def toString = "B2D"  
}


object Example extends Application with TheBeastEnv {
  val Ints = Values[Int](1, 2, 3)
  val Bools = Values(true, false)
  val b = "b" in Bools
  val x = "x" in Ints
  val f = "f" in Ints -> Ints
  val pred = "pred" in Ints -> Bools
  val k = "k" in Ints -> (Ints -> Ints)
  val env = new MutableEnv
  println(env.eval(x))
  env += x -> 1
  env += (f of 1) -> 2
  env += (f of 2) -> 3
  env += ((k of 1) of 2) -> 3
  println(env.eval(x))
  println(env(FunApp(f, 1)))
  println(env(f(f(x))))
  println(env(k(1)(2)))
  println(env(IntAdd))
  println(env(^(IntAdd)(x)(1)))
  println(env(^(1) + x))

  println(Fold(IntAdd, Seq[Term[Int]](1, 2, x + 3, 4), 0))

  println((k(1)(2) + x).domain)
  println(env(k(1)(2) + x))

  println(Quantification(IntAdd, x, f(x), 0).grounded)
  println(intSum(Ints) {x => f(x)})

  println(forall(Ints) {x => f(x) === 1})
  println(sum(Ints) {x => {f(x) === 1}@@})
  println((forall(Ints) {x => f(x) === 1}).domain)
  println(forall(Ints) {x => forall(Ints) {y => k(x)(y) === 1}})
  println(forall(Ints, Ints) {(x, y) => k(x)(y) === 1})
  println((forall(Ints) {x => forall(Ints) {y => k(x)(y) === 1}}).grounded)

  println(f(x).domain)
  //val env = MutableEnv
  //val f = "f" in FunctionValues(Set(1,2,3),Set(1,2))
  //env += (f->Map(1->2))
  //env += (f(1)->2)

  println(ExhaustiveSearch.search(f(x)).eval(f(x)))
  println(ExhaustiveSearch.argmax(sum(Ints){x => ${f(x) === 1} * 0.1}).result.eval(f(2)))

  val Persons = Values("Anna","Peter","Nick","Ivan")
  val smokes = "smokes" in Persons->Bools;
  val cancer = "cancer" in Persons->Bools;
  val friends = "friends" in Persons->(Persons->Bools);

  val f1 = sum(Persons) {x=> ${smokes(x)->cancer(x)} * 0.1}
  val f2 = sum(Persons) {x=> sum(Persons) {y => ${friends(x)(y) && smokes(x) -> smokes(y)} * 0.1}}

  val mln = f1 + f2

  
}