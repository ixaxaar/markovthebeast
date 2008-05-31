package thebeast.pml.training;

import thebeast.pml.Model;
import thebeast.pml.Evaluation;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 07-Mar-2007 Time: 21:33:35
 */
public class FalseNegativesLoss extends EvaluationBasedLoss {

  public FalseNegativesLoss(Model model){
    super(model);
  }

  public double loss(Evaluation evaluation) {
    return evaluation.getFalseNegativesCount();
  }


}
