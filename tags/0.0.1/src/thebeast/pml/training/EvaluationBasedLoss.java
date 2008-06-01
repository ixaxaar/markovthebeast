package thebeast.pml.training;

import thebeast.pml.*;

/**
 * @author Sebastian Riedel
 */
public abstract class EvaluationBasedLoss implements LossFunction {

  protected Evaluation evaluation;

  protected EvaluationBasedLoss(Model model) {
    this.evaluation = new Evaluation(model);
  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("restrict"))
      if ((Boolean)value){
        UserPredicate pred = evaluation.getModel().getSignature().getUserPredicate(name.getTail().getHead());
        evaluation.addRestrictionPattern(pred, name.getTail().getArguments());
      }
  }

  public double loss(GroundAtoms gold, GroundAtoms guess) {
    evaluation.evaluate(gold, guess);
    return loss(evaluation);
  }


  public Object getProperty(PropertyName name) {
    return null;
  }

}
