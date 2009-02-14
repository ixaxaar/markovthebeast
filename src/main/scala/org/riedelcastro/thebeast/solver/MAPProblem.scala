package org.riedelcastro.thebeast.solver
/**
 * @author Sebastian Riedel
 */
case class MAPProblem(val signature: Seq[FunctionSymbol[Any, Any]],
                     val observation: PartiallyObservedWorld,
                     val scorer: Scorer[Score]);

case class MAPResult(val world: World, val score: Score) extends Ordered[MAPResult] {
  def compare(a: MAPResult) = score compare a.score
}
