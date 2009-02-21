package org.riedelcastro.thebeast.scorer

import org.riedelcastro.thebeast.semiring.Log
import org.riedelcastro.thebeast.semiring.PositiveRealSemiring
import org.riedelcastro.thebeast.semiring.RealSemiring
import org.riedelcastro.thebeast.semiring.Semiring
import org.riedelcastro.thebeast.semiring.BooleanSemiring
import org.riedelcastro.thebeast.semiring.SemiringTransformation
import org.riedelcastro.thebeast.semiring.Soften
import org.riedelcastro.thebeast.semiring.ToFullReal
import org.riedelcastro.thebeast.semiring.TropicalSemiring
import term.{Term, Var, Env, EnvVar}

trait Scorer[E, S <: Semiring[E]] {
  def semiring: S
  def score(env: Env): E
  //def domain:Set[EnvVar[Any]]
}


trait BooleanScorer extends Scorer[Boolean, BooleanSemiring] {
  def &(that: Scorer[Boolean, BooleanSemiring]) = And(Seq(this, that))

  def |(that: Scorer[Boolean, BooleanSemiring]) = Or(Seq(this, that))
}

trait RealScorer extends Scorer[Double, RealSemiring] {
  def +(that: Scorer[Double, RealSemiring]) = RealPlus(Seq(this, that))

  def *(that: Scorer[Double, RealSemiring]) = RealTimes(Seq(this, that))
}


case class TermEq[T](lhs: Term[T], rhs: Term[T]) extends BooleanScorer {
  def semiring = BooleanSemiring
  def score(env: Env) = env(lhs) == env(rhs)

  def domain = null //lhs.domain ++
}

case class And(override val args: Seq[Scorer[Boolean, BooleanSemiring]]) extends Plus(BooleanSemiring, args) with BooleanScorer
case class Or(override val args: Seq[Scorer[Boolean, BooleanSemiring]]) extends Times(BooleanSemiring, args) with BooleanScorer
case class RealPlus(override val args: Seq[Scorer[Double, RealSemiring]]) extends Plus[Double, RealSemiring](RealSemiring,args)
case class RealTimes(override val args: Seq[Scorer[Double, RealSemiring]]) extends Times[Double, RealSemiring](RealSemiring,args) 



case class Weight(weight: Term[Double]) extends RealScorer {
  def semiring = RealSemiring
  def score(env: Env) = env(weight)
}

case class Transformation[E1, E2, S1 <: Semiring[E1], S2 <: Semiring[E2]](transformation: SemiringTransformation[E1, E2, S1, S2],
                                                                         argument: Scorer[E1, S1])
        extends Scorer[E2, S2] {
  def semiring = transformation.range
  def score(env: Env) = transformation(argument.score(env))
}

case class Plus[E, S <: Semiring[E]](semiring: S, args: Seq[Scorer[E, S]]) extends Scorer[E, S] {
  def score(env: Env) = args.foldLeft(semiring.zero){(result, arg) => semiring.plus(result, arg.score(env))}
}

case class Times[E, S <: Semiring[E]](semiring: S, args: Seq[Scorer[E, S]]) extends Scorer[E, S] {
  def score(env: Env) = args.foldLeft(semiring.one){(result, arg) => semiring.times(result, arg.score(env))}
}

case class Sum[E, S <: Semiring[E], T](semiring: S, variable: Var[T], scorer: Scorer[E, S]) extends Scorer[E, S] {
  def score(env: Env) = variable.values.foldLeft(semiring.zero)
            {(result, value) => semiring.plus(result, scorer.score(env))} //need to overlay env with variable mapping
}

case class Prod[E, S <: Semiring[E], T](semiring: S, variable: Var[T], scorer: Scorer[E, S]) extends Scorer[E, S] {
  def score(env: Env) = variable.values.foldLeft(semiring.one)
            {(result, value) => semiring.times(result, scorer.score(env))} //need to overlay env with variable mapping
}

case class ForAll[T](override val variable: Var[T], override val scorer: Scorer[Boolean, BooleanSemiring])
        extends Prod(BooleanSemiring, variable, scorer)

case class Exists[T](override val variable: Var[T], override val scorer: Scorer[Boolean, BooleanSemiring])
        extends Sum(BooleanSemiring, variable, scorer)




trait ScorerPredef {
  class Log(argument: Scorer[Double, PositiveRealSemiring]) extends Transformation(Log, argument)
  class Soften(argument: Scorer[Boolean, BooleanSemiring]) extends Transformation(Soften, argument) with RealScorer
  class Signed(argument: Scorer[Double, PositiveRealSemiring]) extends Transformation(ToFullReal, argument)

  //  def log(argument: Scorer[Double, PositiveRealSemiring]) = Log(argument)
  def $(argument: Scorer[Boolean, BooleanSemiring]) = new Soften(argument)
  def %(term: Term[Double]) = new Weight(term)
  //markov logic formula: Log(Sum(x,Times(Weight(w),Soften(Scorer...))

}
