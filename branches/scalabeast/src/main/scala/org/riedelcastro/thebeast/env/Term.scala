package org.riedelcastro.thebeast.env


import runtime.RichDouble
import solve.ExhaustiveSearch

/**
 * @author Sebastian Riedel
 */
sealed trait Term[+T] {

  /**
   * The domain of a term is the set of all variables that appear in the term. If possible, this
   * method may return function application variables. 
   */
  def variables: Iterable[EnvVar[Any]]

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
  def variables = Set.empty

  def values = Values(value)

  def simplify = this


  override def toString = value.toString
}


trait BoundedTerm[T] extends Term[T] {
  def upperBound : T
}

trait DoubleTerm extends BoundedTerm[Double]
trait BooleanTerm extends BoundedTerm[Boolean]

trait IntTerm extends BoundedTerm[Int]


case class Var[+T](val name: String, override val values: Values[T]) extends Term[T] with EnvVar[T] {
  def variables: Iterable[EnvVar[T]] = Set(this)

  def simplify = this

  override def toString = name
}


case class FunApp[T, +R](val function: Term[T => R], val arg: Term[T]) extends Term[R] {
  def variables = {
    //if we have something like f(1)(2)(3) we should create a funapp variable
    if (isGround)
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

  override def toString = function.toString + "(" + init + "):" + args
}

case class Quantification[R, V](val function: Term[R => (R => R)], val variable: Var[V], val formula: Term[R], val init: Term[R])
        extends Term[R] {
  lazy val grounded = {
    val env = new MutableEnv
    Fold(function, variable.values.map(value => {env += variable -> value; env.ground(formula)}).toSeq, init)
  }

  def simplify = grounded.simplify

  def variables = grounded.variables

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

  def range: Values[R] = {
    funVar match {
      case x: Var[_] => x.values.asInstanceOf[FunctionValues[T, R]].range
      case x: FunAppVar[_, _] => x.range.asInstanceOf[FunctionValues[T, R]].range
    }

  }

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

  println((k(1)(2) + x).variables)
  println(env(k(1)(2) + x))

  println(Quantification(IntAdd, x, f(x), 0).grounded)
  println(intSum(Ints) {x => f(x)})

  println(forall(Ints) {x => f(x) === 1})
  println(sum(Ints) {x => {f(x) === 1} @@})
  println((forall(Ints) {x => f(x) === 1}).variables)
  println(forall(Ints) {x => forall(Ints) {y => k(x)(y) === 1}})
  println(forall(Ints, Ints) {(x, y) => k(x)(y) === 1})
  println((forall(Ints) {x => forall(Ints) {y => k(x)(y) === 1}}).grounded)

  println(f(x).variables)
  //val env = MutableEnv
  //val f = "f" in FunctionValues(Set(1,2,3),Set(1,2))
  //env += (f->Map(1->2))
  //env += (f(1)->2)

  println(ExhaustiveSearch.search(f(x)).eval(f(x)))
  println(ExhaustiveSearch.argmax(sum(Ints) {x => $ {f(x) === 1} * 0.1}).result.eval(f(2)))

  val Persons = Values("Anna", "Peter", "Nick", "Ivan")
  val smokes = "smokes" in Persons -> Bools;
  val cancer = "cancer" in Persons -> Bools;
  val friends = "friends" in Persons -> (Persons -> Bools);

  val f1 = sum(Persons) {x => $ {smokes(x) -> cancer(x)} * 0.1}
  val f2 = sum(Persons) {x => sum(Persons) {y => $ {friends(x)(y) && smokes(x) -> smokes(y)} * 0.1}}

  val mln = f1 + f2


}