package thebeast.pml.training;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Evaluation;
import thebeast.pml.Model;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 07-Mar-2007 Time: 21:33:35
 */
public class GlobalF1Loss implements LossFunction {

  private Evaluation evaluation;

  public GlobalF1Loss(Model model){
    evaluation = new Evaluation(model);
  }
  public double loss(GroundAtoms gold, GroundAtoms guess) {
    evaluation.evaluate(gold, guess);
    return 1.0 - evaluation.getF1();
  }
}
