package org.riedelcastro.thebeast

/**
 * @author Sebastian Riedel
 */

trait Formula[S <: Score] {
  def ground(binding: Binding): Scorer[S]
  def &[T <: Score](that:Formula[T]) : ApplyFormula[S,T,BoolScore] = ApplyFormula(And,this,that)
  def |->[T <: Score](that:Formula[T]) : ApplyFormula[S,T,BoolScore] = ApplyFormula(Implies,this,that)
}

case class Atom[T, R](symbol: FunctionSymbol[T, R], from: Term[T], to: Term[R]) extends Formula[BoolScore] {
  def ground(binding: Binding): Scorer[BoolScore] = {
    GroundAtomScorer[T, R](GroundAtom(symbol, from.resolve(binding), to.resolve(binding)))
  }
}

case class ApplyFormula[A1 <: Score, A2 <: Score, R <: Score](val combine:(A1,A2)=>R,
                                                             val arg1:Formula[A1],
                                                             val arg2:Formula[A2]) extends Formula[R] {
  def ground(binding: Binding): Scorer[R] = {
    Apply(combine, arg1.ground(binding), arg2.ground(binding))
  }
}

case class AtomBuilder[T, R](val f: FunctionSymbol[T, R], val t: Term[T]) {
  def ~>(r: Term[R]): Atom[T, R] = {Atom(f, t, r)}
}
