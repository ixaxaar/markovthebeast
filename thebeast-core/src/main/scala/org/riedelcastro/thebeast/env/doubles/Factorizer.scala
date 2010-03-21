package org.riedelcastro.thebeast.env.doubles

import org.riedelcastro.thebeast.env.vectors.{VectorDotApp, VectorTerm, VectorSum, QuantifiedVectorSum}
import org.riedelcastro.thebeast.env.booleans.{BooleanTerm, Disjunction, Conjunction}

/**
 * @author sriedel
 */

object Factorizer {
  def toMultiplication(term: DoubleTerm): Multiplication[DoubleTerm] = {
    unroll(term).flatten match {
      case Multiplication(args) => Multiplication(args.flatMap(toMultiplication(_).args))
      case Exp(Sum(args)) => Multiplication(args.map(Exp(_)))
      case Exp(v: VectorDotApp) => Multiplication(v.distribute.asInstanceOf[Sum[DoubleTerm]].args.map(Exp(_)))
      case Indicator(Conjunction(args)) => Multiplication(args.map(Indicator(_)))
      case x => Multiplication(Seq(x))
    }
  }

  //todo: this should all be done by a generic unroll method and the generic builder framework
  private def unroll(term: DoubleTerm): DoubleTerm = term match {
    case Multiplication(args) => Multiplication(args.map(unroll(_)))
    case Sum(args) => Sum(args.map(unroll(_)))
    case x: QuantifiedSum[_] => x.unroll
    case Normalize(x) => Normalize(unroll(x))
    case Exp(x) => Exp(unroll(x))
    case Indicator(arg) => Indicator(unrollBoolean(arg))
    case VectorDotApp(lhs, rhs) => VectorDotApp(unrollVectorTerm(lhs), unrollVectorTerm(rhs))
    case x => x
  }

  private def unrollBoolean(term:BooleanTerm):BooleanTerm = term match {
    case Conjunction(args) => Conjunction(args.map(unrollBoolean(_)))
    case Disjunction(args) => Disjunction(args.map(unrollBoolean(_)))
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