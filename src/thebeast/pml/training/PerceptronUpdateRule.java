package thebeast.pml.training;

import thebeast.pml.SparseVector;
import thebeast.pml.Weights;
import thebeast.pml.FeatureVector;
import thebeast.pml.PropertyName;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class PerceptronUpdateRule implements UpdateRule {

  private double learningRate = 1.0;
  private double decay = 1.0;
  private boolean enforceSigns = false;

  public void endEpoch() {
    learningRate *= decay;
  }

  public void update(FeatureVector gold, List<FeatureVector> candidates, List<Double> losses, Weights weights) {
    double scale = 1.0 / candidates.size();
    for (FeatureVector guess : candidates) {
      //FeatureVector guess = candidates.get(candidates.size() - 1);
      SparseVector diffFree = gold.getLocal().add(-scale, guess.getLocal());
      weights.add(learningRate, diffFree);
      if (enforceSigns) {
        weights.enforceBound(gold.getLocalNonnegativeIndices(), true, 0.0);
        weights.enforceBound(gold.getLocalNonpositiveIndices(), false, 0.0);
      }
      SparseVector diffNN = gold.getFalseVector().add(-scale, guess.getFalseVector());
      if (enforceSigns) weights.add(learningRate, diffNN, true);
      else weights.add(learningRate, diffNN);
      SparseVector diffNP = gold.getTrueVector().add(-scale, guess.getTrueVector());
      if (enforceSigns) weights.add(learningRate, diffNP, false);
      else weights.add(learningRate, diffNP);
    }
  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("signs"))
      enforceSigns = (Boolean) value;
  }

  public Object getProperty(PropertyName name) {
    return null;
  }
}
