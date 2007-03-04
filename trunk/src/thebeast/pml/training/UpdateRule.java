package thebeast.pml.training;

import thebeast.pml.SparseVector;
import thebeast.pml.Evaluation;
import thebeast.pml.Weights;

/**
 * An update rule is used for updating the weights given a gold output and a system output in an online learning
 * context.
 *
 * @author Sebastian Riedel
 */
public interface UpdateRule {
  public void endEpoch();

  public void update(SparseVector gold, SparseVector guess, Evaluation evaluation, Weights weights);
}

