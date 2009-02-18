package org.riedelcastro.thebeast.term

/**
 * @author Sebastian Riedel
 */

trait Values[+T] extends Iterable[T]

trait AtomicValues[+T] extends Values[T]

object Values {
  def apply[T](values: T*) =
    new ValuesProxy(values.foldLeft(Set.empty[T]){(result, v) => result ++ Set(v)})
}

class ValuesProxy[+T](override val self: Iterable[T]) extends AtomicValues[T] with IterableProxy[T]

class FunctionValues[T, +R](val domain: Values[T], val range: Values[R]) extends Values[T => R] {
  def elements = AllFunctions(domain.toStream, range.toStream).elements
}

sealed trait Term[+T] {
  def values: Values[T]
}

trait Fun[T, +R] extends Term[T => R] {
  def values: FunctionValues[T, R]
}

case class Constant[+T](val value: T) extends Term[T] {
  def values = new ValuesProxy(Set(value))
}

case class Var[+T](val name: String, override val values: Values[T]) extends Term[T] with EnvVar[T] {
}

case class FunConstant[T, +R](override val value: T => R) extends Constant[T => R](value) with Fun[T, R] {
  override def values = null
}

case class FunVar[T, +R](override val name: String, override val values: FunctionValues[T, R])
        extends Var[T => R](name, values) with Fun[T, R]

case class FunApp[T, +R](val function: Fun[T, R], val arg: Term[T]) extends Term[R] {
  def values = function.values.range
}

sealed trait EnvVar[+T] {
}

case class FunAppVar[T, +R](val funVar: EnvVar[T => R], val arg: T) extends EnvVar[R]

trait Env {
  def apply[T](term: Term[T]): T = eval(term).get

  def eval[T](term: Term[T]): Option[T] = {
    term match {
      case Constant(x) => Some(x)
      case v: Var[_] => resolveVar[T](v)
      case FunApp(funTerm, argTerm) =>
        {
          val fun = eval(funTerm);
          val arg = eval(argTerm);
          if (fun.isDefined && arg.isDefined) Some(fun.get(arg.get)) else None
        }
    }
  }

  def resolveVar[T](variable: Var[T]): Option[T]
}

class MutableEnv extends Env {
  private[this] type MutableMap = scala.collection.mutable.HashMap[Any, Any]
  private[this] val values = new MutableMap

  def resolveVar[T](variable: Var[T]) = values.get(variable).asInstanceOf[Option[T]]

  private[this] def getMap(variable: EnvVar[Any]): MutableMap = {
    variable match {
      case v: Var[_] => values.getOrElseUpdate(v, new MutableMap()).asInstanceOf[MutableMap]
      case FunAppVar(funVar, arg) => getMap(funVar).getOrElseUpdate(arg, new MutableMap()).asInstanceOf[MutableMap]
    }
  }

  def set[T](variable: EnvVar[T], value: T) {
    variable match {
      case v: Var[_] => values += Tuple2[Any, Any](v, value)
      case FunAppVar(funVar, arg) => getMap(funVar) += Tuple2[Any, Any](arg, value)
    }
  }

  def +=[T](mapping: Tuple2[EnvVar[T], T]) = set(mapping._1, mapping._2)

}

trait TheBeastEnv {
  implicit def string2varbuilder(name: String) = new {
    def in[T](values: AtomicValues[T]) = Var(name, values)

    def in[T, R](values: FunctionValues[T, R]) = FunVar(name, values)
  }


  implicit def value2constant[T](value: T) = Constant(value)

  case class FunAppVarBuilder[T, R](val funvar: FunVar[T, R]) {
    def of(t: T) = FunAppVar(funvar, t)
  }

  implicit def funvar2funAppVarBuilder[T, R](funvar: FunVar[T, R]) = FunAppVarBuilder(funvar)

  implicit def funvar2funAppBuilder[T, R](funvar: FunVar[T, R]) = new (Term[T] => FunApp[T, R]) {
    def apply(t: Term[T]) = FunApp(funvar, t)
  }

  implicit def values2FunctionValuesBuilder[T,R](domain:Values[T]):FunctionValuesBuilder[T,R] = 
    FunctionValuesBuilder[T,R](domain)

  case class FunctionValuesBuilder[T, R](domain: Values[T]) {
    def ->[R](range: Values[R]) = new FunctionValues(domain, range)
  }


  //  implicit def term2envVar[T](term:Term[T]): EnvVar[T] = {
  //    term match {
  //      case FunApp(f,Constant(v)) => FunAppVar(term2envVar(f),v)
  //      case _=> null
  //    }
  //  }

}


object Example extends Application with TheBeastEnv {
  val Ints = Values(1, 2, 3)
  val x = "x" in Ints
  val f = "f" in Ints -> Ints
  val k = "k" in Ints -> (Ints -> Ints)
  val env = new MutableEnv
  println(env.eval(x))
  env += x -> 1
  env += (f of 1) -> 2
  env += (f of 2) -> 3
  //env += ((f of 1) of 2) -> 3
  println(env.eval(x))
  println(env(FunApp(f, 1)))
  println(env(f(f(x))))
  //println(env(k()))
  //val env = MutableEnv
  //val f = "f" in FunctionValues(Set(1,2,3),Set(1,2))
  //env += (f->Map(1->2))
  //env += (f(1)->2)
}