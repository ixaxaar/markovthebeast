package thebeast.pml.training;

import thebeast.pml.Model;
import thebeast.pml.GroundAtoms;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 07-Mar-2007 Time: 21:33:35
 */
public class ExactMatchLoss extends EvaluationBasedLoss {

  public ExactMatchLoss(Model model){
    super(model);
  }
  public double loss(GroundAtoms gold, GroundAtoms guess) {
    evaluation.evaluate(gold, guess);
    return evaluation.getNumErrors() == 0 ? 0.0 : 1.0;
  }


}
