package thebeast.pml.training;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Evaluation;
import thebeast.pml.Model;
import thebeast.pml.PropertyName;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 07-Mar-2007 Time: 21:33:35
 */
public class GlobalF1Loss extends EvaluationBasedLoss {


  public GlobalF1Loss(Model model){
    super(model);
  }

  public double loss(Evaluation evaluation) {
    return 1.0 - evaluation.getF1();
  }

}
