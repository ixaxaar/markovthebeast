package org.riedelcastro.thebeast.solver

/**
 * @author Sebastian Riedel
 */
case class MAPProblem(val signature: Seq[FunctionSymbol[Any, Any]],
                     val observation: PartiallyObservedWorld,
                     val scorer: Scorer[Score]);

trait Status
object Status {
  case object Solved extends Status
  case object CantDo extends Status
  case object Infeasible extends Status
}


case class MAPResult(val world: World, val score: Score, val status: Status) extends Ordered[MAPResult] {
  def compare(a: MAPResult) = score compare a.score
}

object MAPResult {
  val CantDo = MAPResult(null, Score.NEGINF ,Status.CantDo)
}
