package thebeast.pml.training;

import thebeast.pml.SparseVector;
import thebeast.pml.Weights;
import thebeast.pml.FeatureVector;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class PerceptronUpdateRule implements UpdateRule {

  private double learningRate = 1.0;
  private double decay = 1.0;
  private boolean enforceSigns = true;

  public void endEpoch() {
    learningRate *= decay;
  }

  public void update(FeatureVector gold, List<FeatureVector> candidates, List<Double> losses, Weights weights) {
    double scale = 1.0 / candidates.size();
    for (FeatureVector guess : candidates) {
      //FeatureVector guess = candidates.get(candidates.size() - 1);
      SparseVector diffFree = gold.getFree().add(-scale, guess.getFree());
      weights.add(learningRate, diffFree);
      SparseVector diffNN = gold.getNonnegative().add(-scale, guess.getNonnegative());
      if (enforceSigns) weights.add(learningRate, diffNN, true);
      else weights.add(learningRate, diffNN);
      SparseVector diffNP = gold.getNonpositive().add(-scale, guess.getNonpositive());
      if (enforceSigns) weights.add(learningRate, diffNP, false);
      else weights.add(learningRate, diffNP);
    }
  }
}
