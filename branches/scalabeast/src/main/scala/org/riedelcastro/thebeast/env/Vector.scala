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

  def scalar(scale:Double) : Vector = {
    val result = new Vector
    result.addInPlace(this, scale)
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


  override def toString =
    store.elements.foldLeft("") {(s,e)=>
            s + e._1.asInstanceOf[Collection[_]].mkString(",")+ "\t" + e._2.toString + "\n" } 
}

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
    val keyEvals  = new ArrayBuffer[Any]
    for (k <- key) { val eval = k.eval(env); if (eval.isDefined) keyEvals += eval.get else return None }
    val result = new Vector
    result.set(1.0, keyEvals)
    Some(result)
  }

  def variables = key.flatMap(k => k.variables)

  def values = VectorSpace
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
}

case class QuantifiedVectorSum[T](override val variable: Var[T], override val formula: VectorTerm)
        extends Quantification(Constant(VectorAdd), variable, formula, Constant(new Vector)) with VectorTerm {
  override lazy val unroll = {
    val env = new MutableEnv
    VectorSum(variable.values.map(value => {env += variable -> value; formula.ground(env)}).toSeq)
  }
  override def ground(env: Env) = unroll.ground(env)

}


object VectorAdd extends (Vector=>(Vector=>Vector)){
  def apply(lhs:Vector) = (rhs:Vector) => lhs.add(rhs,1.0)

}

object VectorDot extends (Vector=>(Vector=>Double)){
  def apply(lhs:Vector) = (rhs:Vector) => lhs.dot(rhs)
}

object VectorScalar extends (Vector=>(Double=>Vector)){
  def apply(lhs:Vector) = (rhs:Double) => lhs.scalar(rhs)
}

object VectorSpace extends Values[Vector] {
  def elements = throw new Error("Can't iterate over all vectors")
  override def defaultValue = VectorZero
  override def randomValue = throw new Error("Space too large for randomly drawing an element")
}

object VectorZero extends Vector {
  override def addInPlace(that: Vector, scale: Double) = throw new Error("Cannot change the zero vector")
}

object VectorDemo extends Application with TheBeastEnv {

  val vector = new Vector
  vector.set(2.0, "blah", 1)
  vector.set(-1.0, 200, "pups", true)

  println(vector)

  val Bools = Values(true, false)  
  val Persons = Values("Anna", "Peter", "Nick", "Ivan")
  val smokes = "smokes" in Persons -> Bools;
  val cancer = "cancer" in Persons -> Bools;
  val friends = "friends" in Persons -> (Persons -> Bools);

  val weights = "w" in VectorSpace

  val f1 = sum(Persons) {x => $ {smokes(x) -> cancer(x)} * 0.1}
  val f2 = sum(Persons) {x => sum(Persons) {y => $ {friends(x)(y) && smokes(x) -> smokes(y)} * 0.1}}
  val f3 = vectorSum(Persons) {x => $ {smokes(x) -> cancer(x)} * VectorOne(x)}
  //val mln = f3 dot weights

  //it should be possible to move the dot product into the summation, replacing VectorOne(x) with weights(x)

  
}