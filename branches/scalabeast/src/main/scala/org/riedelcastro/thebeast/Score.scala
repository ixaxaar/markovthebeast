package org.riedelcastro.thebeast
/**
 * @author Sebastian Riedel
 */
trait Score {
  def value(): Double;
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