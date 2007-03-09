package thebeast.pml.training;

import thebeast.pml.Evaluation;
import thebeast.pml.Model;
import thebeast.pml.GroundAtoms;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 07-Mar-2007 Time: 21:33:35
 */
public class AverageF1Loss implements LossFunction {

  private Evaluation evaluation;

  public AverageF1Loss(Model model){
    evaluation = new Evaluation(model);
  }
  public double loss(GroundAtoms gold, GroundAtoms guess) {
    evaluation.evaluate(gold, guess);
    return 1.0 - evaluation.getAverageF1();
  }
}
