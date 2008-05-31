package thebeast.pml.training;

import thebeast.pml.FeatureVector;
import thebeast.pml.Weights;
import thebeast.pml.SparseVector;
import thebeast.pml.PropertyName;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class PassiveAggressiveUpdateRule implements UpdateRule {

  private boolean enforceSigns = false;
  private int type = 0;
  public static final int PA = 0;
  public static final int PA1 = 1;
  public static final int PA2 = 2;
  private double C = 0.01;

  public void endEpoch() {
  }

  public void update(FeatureVector gold, List<FeatureVector> candidates, List<Double> losses, Weights weights) {
    double goldScore = weights.score(gold);
    int index = 0;
    for (FeatureVector guess : candidates) {
      SparseVector diffLocal = gold.getLocal().add(-1.0, guess.getLocal());
      SparseVector diffNN = gold.getFalseVector().add(-1.0, guess.getFalseVector());
      SparseVector diffNP = gold.getTrueVector().add(-1.0, guess.getTrueVector());
      double sqNorm = diffLocal.norm() + diffNN.norm() + diffNP.norm();
      sqNorm *= sqNorm;
      double loss = losses.get(index++);
      double guessScore = weights.score(guess);
      double sufferLoss = guessScore - goldScore + loss;
      double scale = 0;
      switch (type) {
        case PA:
          scale = sqNorm != 0.0 ? sufferLoss / sqNorm : 0.0;
          break;
        case PA1:
          scale = sqNorm != 0.0 ? sufferLoss / sqNorm : 0.0;
          scale = Math.min(scale, C);
          break;
        case PA2:
          scale = sufferLoss / ( sqNorm + 0.5 * C) ;
          scale = Math.min(scale, C);
          break;
      }
      //System.out.println(scale);
      if (enforceSigns) {
        weights.add(scale, diffLocal);
        weights.add(scale, diffNN, true);
        weights.add(scale, diffNP, false);
        weights.enforceBound(gold.getLocalNonnegativeIndices(), true, 0.0);
        weights.enforceBound(gold.getLocalNonpositiveIndices(), false, 0.0);
      } else {
        weights.add(scale, diffLocal);
        weights.add(scale, diffNN);
        weights.add(scale, diffNP);
      }
    }

  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("signs"))
      enforceSigns = (Boolean) value;
    else if (name.getHead().equals("version"))
      type = (Integer) value;
    else if (name.getHead().equals("aggressiveness"))
      C = (Double) value;
  }

  public Object getProperty(PropertyName name) {
    return null;
  }
}
