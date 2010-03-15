package org.riedelcastro.thebeast.env.doubles

import org.riedelcastro.thebeast.env.vectors.{VectorDotApp, VectorTerm, VectorSum, QuantifiedVectorSum}

/**
 * @author sriedel
 */

object Factorizer {
  def toMultiplication(term: DoubleTerm): Multiplication[DoubleTerm] = {
    unroll(term).flatten match {
      case m:Multiplication[_] => m
      case Exp(Sum(args)) => Multiplication(args.map(Exp(_)))
      case Exp(v: VectorDotApp) => Multiplication(v.distribute.asInstanceOf[Sum[DoubleTerm]].args.map(Exp(_)))
      case x => Multiplication(Seq(x))
    }
  }

  private def unroll(term: DoubleTerm): DoubleTerm = term match {
    case Sum(args) => Sum(args.map(unroll(_)))
    case x: QuantifiedSum[_] => x.unroll
    case Normalize(x) => Normalize(unroll(x))
    case Exp(x) => Exp(unroll(x))
    case VectorDotApp(lhs, rhs) => VectorDotApp(unrollVectorTerm(lhs), unrollVectorTerm(rhs))
    case x => x
  }

  private def unrollVectorTerm(term: VectorTerm): VectorTerm = term match {
    case VectorSum(args) => VectorSum(args.map(unrollVectorTerm(_)))
    case QuantifiedVectorSum(v, f) => {
      val x = QuantifiedVectorSum(v, unrollVectorTerm(f)).unrollUncertain
      x
    }
    case x => x
  }

}