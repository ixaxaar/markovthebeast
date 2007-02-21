package thebeast.pml.training;

import thebeast.pml.SparseVector;
import thebeast.pml.Evaluation;
import thebeast.pml.Weights;

/**
 * @author Sebastian Riedel
 */
public interface UpdateRule {
  public void endEpoch();

  public void update(SparseVector gold, SparseVector guess, Evaluation evaluation, Weights weights);
}

