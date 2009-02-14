package org.riedelcastro.thebeast
/**
 * @author Sebastian Riedel
 */
trait Score extends Ordered[Score]{
  def value(): Double;

  def compare(a: Score) = value compare a.value

  override def equals(obj: Any) = obj.isInstanceOf[Score] && (value == obj.asInstanceOf[Score].value)
}

case class DoubleScore(val score: Double) extends Score {
  def value = score
}

case class BoolScore(val score: Boolean) extends Score {
  def value = if (score) 1.0 else 0.0
}
case class IntScore(val score: Int) extends Score {
  def value = score
}