package org.riedelcastro.thebeast.env

/**
 * @author Sebastian Riedel
 */

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

  implicit def double2constant(value: Double) = DoubleConstant(value)


  case class FunAppVarBuilder[T, R](val funvar: EnvVar[T => R]) {
    def of(t: T) = FunAppVar(funvar, t)
  }

  case class TermBuilder[T](val term: Term[T]) {
    def ===(rhs: Term[T]): BooleanTerm = BooleanFunApp(FunApp(Constant(new EQ[T]), term), rhs)
  }

  case class FunctionValuesBuilder[T, R](domain: Values[T]) {
    def ->[R](range: Values[R]) = new FunctionValues(domain, range)
  }

  implicit def funvar2funAppVarBuilder[T, R](funvar: EnvVar[T => R]) = FunAppVarBuilder(funvar)

  implicit def term2funAppBuilder[T, R](fun: Term[T => R]) = new (Term[T] => FunApp[T, R]) {
    def apply(t: Term[T]) = FunApp(fun, t)
  }

  implicit def term2doubleFunAppBuilder[T](fun: Term[T => Double]) = new (Term[T] => DoubleFunApp[T]) {
    def apply(t: Term[T]) = DoubleFunApp(fun, t)
  }

  implicit def term2booleanFunAppBuilder[T](fun: Term[T => Boolean]) = new (Term[T] => BooleanFunApp[T]) {
    def apply(t: Term[T]) = BooleanFunApp(fun, t)
  }


//  implicit def term2arity2funAppBuilder[T1,T2, R](fun: Term[T1 => (T2=>R)]) = new ((Term[T1],Term[T2]) => FunApp[T2, R]) {
//    def apply(t: (Term[T1],Term[T2])) = FunApp(FunApp(fun, t._1), t._2)
//  }


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

  implicit def doubleTerm2DoubleTermBuilder(lhs: DoubleTerm) = new {
    def +(rhs: DoubleTerm) = AddApp(lhs,rhs)

    def *(rhs: DoubleTerm) = TimesApp(lhs,rhs)
  }


  implicit def boolTerm2BoolAppBuilder(lhs: BooleanTerm) = new {
    def @@ = BoolToDoubleCast(lhs)

    def &&(rhs: BooleanTerm) = AndApp(lhs,rhs)

    def ->(rhs: BooleanTerm) = ImpliesApp(lhs,rhs)
  }

  def $(term: BooleanTerm) = BoolToDoubleCast(term)


  def intSum[T](values: Values[T])(formula: Var[T] => Term[Int]) = {
    val variable = createVariable(values)
    Quantification(IntAdd, variable, formula(variable), 0)
  }

  def sum[T](values: Values[T])(formula: Var[T] => DoubleTerm) = {
    val variable = createVariable(values)
    QuantifiedSum(variable, formula(variable))
  }

  def vectorSum[T](values: Values[T])(formula: Var[T] => VectorTerm) = {
    val variable = createVariable(values)
    QuantifiedVectorSum(variable, formula(variable))
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
