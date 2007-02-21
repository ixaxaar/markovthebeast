package thebeast.pml.training;

import thebeast.pml.SparseVector;
import thebeast.pml.Evaluation;
import thebeast.pml.Weights;

/**
 * @author Sebastian Riedel
 */
public class PerceptronUpdateRule implements UpdateRule {

  private double learningRate = 1.0;
  private double decay = 1.0;

  public void endEpoch() {
    learningRate *= decay;
  }

  public void update(SparseVector gold, SparseVector guess, Evaluation evaluation, Weights weights) {
    SparseVector diff = gold.add(-1.0, guess);
    weights.add(learningRate,diff);
  }
}
