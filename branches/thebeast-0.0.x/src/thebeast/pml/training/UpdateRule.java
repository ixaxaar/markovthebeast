package thebeast.pml.training;

import thebeast.pml.*;

import java.util.List;

/**
 * An update rule is used for updating the weights given a gold output and a system output in an online learning
 * context.
 *
 * @author Sebastian Riedel
 */
public interface UpdateRule extends HasProperties {
  public void endEpoch();

  public void update(FeatureVector gold, List<FeatureVector> candidates, List<Double> losses, Weights weights);
}

