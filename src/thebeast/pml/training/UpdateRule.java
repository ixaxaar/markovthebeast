package thebeast.pml.training;

import thebeast.pml.SparseVector;
import thebeast.pml.Evaluation;
import thebeast.pml.Weights;
import thebeast.pml.FeatureVector;

import java.util.List;

/**
 * An update rule is used for updating the weights given a gold output and a system output in an online learning
 * context.
 *
 * @author Sebastian Riedel
 */
public interface UpdateRule {
  public void endEpoch();

  public void update(FeatureVector gold, List<FeatureVector> candidates, List<Double> losses, Weights weights);
}

