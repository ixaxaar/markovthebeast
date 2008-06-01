package thebeast.pml.training;

import thebeast.pml.Evaluation;
import thebeast.pml.Model;
import thebeast.pml.PropertyName;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 07-Mar-2007 Time: 21:33:35
 */
public class FPFNLoss extends EvaluationBasedLoss {

  private double fpScale = 1.0, fnScale = 1.0;

  public FPFNLoss(Model model) {
    super(model);
  }

  public double loss(Evaluation evaluation) {
    return evaluation.getFalsePositivesCount() * fpScale + evaluation.getFalseNegativesCount() * fnScale;
  }


  public void setProperty(PropertyName name, Object value) {
    super.setProperty(name, value);
    if (name.getHead().equals("fpScale"))
      fpScale = (Double) value;
    else if (name.getHead().equals("fnScale"))
      fnScale = (Double) value;
  }
}
