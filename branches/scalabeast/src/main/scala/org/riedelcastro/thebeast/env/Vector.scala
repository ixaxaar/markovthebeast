package org.riedelcastro.thebeast.env


import collection.mutable.{ArrayBuffer, HashMap}

/**
 * @author Sebastian Riedel
 */

class Vector {

  private val store = new HashMap[Any,Double]

  def set(value:Double, keys:Any*){
    store += (keys -> value)
  }

  def get(keys:Any*) : Double = store.getOrElse(keys,0.0)

  def add(that:Vector, scale:Double) : Vector = {
    val result = new Vector
    result.addInPlace(this, 1.0)
    result.addInPlace(that, scale)
    result
  }

  def addInPlace(that:Vector, scale:Double) : Unit = {
    for (entry <- store.elements)
      set(entry._2 + scale * that.get(entry._1), entry._1)
    for (entry <- that.store.elements)
      if (!store.keySet.contains(entry._1)) store += (entry._1 -> entry._2 * scale)
  }

  def dot(that:Vector) : Double = {
    store.foldLeft(0.0) {(score,keyValue)=>  score + keyValue._2 * that.get(keyValue._1)} 
  }

}

trait VectorTerm extends Term[Vector] {
  def ground(env: Env) : VectorTerm

  def +(that:VectorTerm) = VectorAddApp(this,that)
  def dot(that:VectorTerm) = VectorDotApp(this,that)
}

case class VectorOne(key : Term[Any]*) extends VectorTerm {
  def ground(env: Env) = null

  def simplify = this

  override def eval(env: Env): Option[Vector] = {
    val keyEvals  = new ArrayBuffer[Any]
    for (k <- key) { val eval = k.eval(env); if (eval.isDefined) keyEvals += eval.get else return None }
    val result = new Vector
    result.set(1.0, keyEvals)
    Some(result)
  }

  def variables = key.flatMap(k => k.variables)

  def values = null
}

case class VectorAddApp(lhs:VectorTerm, rhs:VectorTerm)
        extends FunApp(FunApp(Constant(VectorAdd),lhs),rhs) with VectorTerm {
  override def ground(env: Env) = VectorAddApp(lhs.ground(env),rhs.ground(env))
}

case class VectorDotApp(lhs:VectorTerm, rhs:VectorTerm)
        extends FunApp(FunApp(Constant(VectorDot),lhs),rhs) with DoubleTerm {
  override def ground(env: Env) = VectorDotApp(lhs.ground(env),rhs.ground(env))

  def upperBound = Math.POS_INF_DOUBLE
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
}

object VectorAdd extends (Vector=>(Vector=>Vector)){
  def apply(lhs:Vector) = (rhs:Vector) => lhs.add(rhs,1.0)
}

object VectorDot extends (Vector=>(Vector=>Double)){
  def apply(lhs:Vector) = (rhs:Vector) => lhs.dot(rhs)
}