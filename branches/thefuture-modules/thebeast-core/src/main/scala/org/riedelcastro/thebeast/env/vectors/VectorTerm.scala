package org.riedelcastro.thebeast.env.vectors


import collection.mutable.ArrayBuffer
import doubles.{Sum, QuantifiedSum, DoubleTerm}

/**
 * @author Sebastian Riedel
 */

trait VectorTerm extends Term[Vector] {
  def ground(env: Env) : VectorTerm

  def *(that:DoubleTerm) = VectorScalarApp(this,that)

  def +(that:VectorTerm) = {
    this match {
      case VectorAddApp(lhs,rhs) => that match {
        case VectorAddApp(lhs2,rhs2) => VectorSum(Seq(lhs,rhs,lhs2,rhs2))
        case VectorSum(args) => VectorSum(Seq(lhs,rhs) ++ args)
        case x => VectorSum(Seq(lhs,rhs,x))
      }
      case VectorSum(args) => that match {
        case VectorAddApp(lhs2,rhs2) => VectorSum(args ++ Seq(lhs2,rhs2))
        case VectorSum(args2) => VectorSum(args ++ args2)
        case x => VectorSum(args ++ Seq(x))
      }
      case _ => VectorAddApp(this,that)
    }

  }
  def dot(that:VectorTerm) = VectorDotApp(this,that)

  def dot(that:Vector) = VectorDotApp(this,VectorConstant(that))

}

case class VectorOne(key : Term[Any]*) extends VectorTerm {

  def ground(env: Env) = VectorOne(key.map(k => k.ground(env)):_*)

  def simplify = {
    if (!key.exists(k => !k.isInstanceOf[Constant[_]])) {
      val result = new Vector
      result.set(1.0, key.map(k => k.asInstanceOf[Constant[Any]].value):_*)
      VectorConstant(result)
    } else
      this
  }

  override def eval(env: Env): Option[Vector] = {
    val keyEvals = new ArrayBuffer[Any]
    for (k <- key) { val eval = k.eval(env); if (eval.isDefined) keyEvals += eval.get else return None }
    val result = new Vector
    result.set(1.0, keyEvals:_*)
    Some(result)
  }

  def variables = key.foldLeft(Set[EnvVar[_]]()){(set,k) => set ++ k.variables} // Set(key.flatMap(k => k.variables))

  def values = VectorSpace


  def isGround = key.forall(k => k.isGround)

  override def toString = "1_(" + key.mkString(",") + ")"

  def subterms = key.toSeq
}

case class VectorAddApp(lhs:VectorTerm, rhs:VectorTerm)
        extends FunApp(FunApp(Constant(VectorAdd),lhs),rhs) with VectorTerm {
  override def ground(env: Env) = VectorAddApp(lhs.ground(env),rhs.ground(env))


  override def toString = lhs + "+" + rhs
}

case class VectorDotApp(lhs:VectorTerm, rhs:VectorTerm)
        extends FunApp(FunApp(Constant(VectorDot),lhs),rhs) with DoubleTerm {
  override def ground(env: Env) = VectorDotApp(lhs.ground(env),rhs.ground(env))

  def upperBound = Math.POS_INF_DOUBLE

  def distribute : DoubleTerm = {
    lhs match {
      case VectorSum(args) => Sum(args.map(a => (a dot rhs).distribute))
      case QuantifiedVectorSum(variable,formula) => QuantifiedSum(variable, (formula dot rhs).distribute)
      case _ => rhs match {
        case VectorSum(args) => Sum(args.map(a => (lhs dot a).distribute))
        case QuantifiedVectorSum(variable,formula) => QuantifiedSum(variable, (lhs dot formula).distribute)
        case _ => this
      }
    }
  }


  override def toString = "<" + lhs + "," + rhs + ">"
}

case class VectorScalarApp(lhs:VectorTerm, rhs:DoubleTerm)
        extends FunApp(FunApp(Constant(VectorScalar),lhs),rhs) with VectorTerm {
  override def ground(env: Env) = VectorScalarApp(lhs.ground(env),rhs.ground(env))

  def upperBound = Math.POS_INF_DOUBLE
}

case class VectorConstant(override val value:Vector) extends Constant(value) with VectorTerm {
  override def ground(env: Env) = this
}

case class VectorSum(override val args:Seq[VectorTerm])
        extends Fold(Constant(VectorAdd),args,Constant(new Vector)) with VectorTerm {
  override def eval(env: Env) : Option[Vector] = {
    val result = new Vector;
    for (a <- args) {
      val eval = a.eval(env)
      if (eval.isDefined) result.addInPlace(eval.get,1.0) else return None
    }
    Some(result)
  }

  override def ground(env: Env) = VectorSum(args.map(a=>a.ground(env)))


  override def toString = "+." + args.mkString(",")
}

case class QuantifiedVectorSum[T](override val variable: Var[T], override val formula: VectorTerm)
        extends Quantification(Constant(VectorAdd), variable, formula, Constant(new Vector)) with VectorTerm {
  override lazy val unroll = {
    val env = new MutableEnv
    VectorSum(variable.values.map(value => {env += variable -> value; formula.ground(env)}).toSeq)
  }
  override def ground(env: Env) = unroll.ground(env)

}
