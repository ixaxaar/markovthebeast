package thebeast.pml.training;

import thebeast.pml.SparseVector;
import thebeast.pml.Weights;
import thebeast.pml.FeatureVector;
import thebeast.util.QP;

import java.util.List;

/**
 * Update weights according to (Single-best) MIRA as presented in McDonald et al. 2005.
 *
 * @author Sebastian Riedel
 */
public class MiraUpdateRule implements UpdateRule {
  public void endEpoch() {
    
  }

  public void update(FeatureVector gold, List<FeatureVector> candidates, List<Double> losses, Weights weights) {

    SparseVector.Indices indices = new SparseVector.Indices();
    for (FeatureVector candidate : candidates){
      indices.add(candidate.getFree().getIndices());
      indices.add(candidate.getNonnegative().getIndices());
      indices.add(candidate.getNonpositive().getIndices());
    }
    int[] indexArray = indices.getIndexArray();

    SparseVector goldVector = new SparseVector(indexArray, 0.0);
    goldVector.add(1.0, gold.getFree());
    goldVector.add(1.0, gold.getNonnegative());
    goldVector.add(1.0, gold.getNonpositive());

    double[][] a = new double[candidates.size()][];
    double[] b = new double[candidates.size()];
    SparseVector[] diffs = new SparseVector[candidates.size()];
    int candidateIndex = 0;
    for (FeatureVector candidate : candidates){
      SparseVector guessVector = new SparseVector(indexArray, 0.0);
      guessVector.add(1.0, candidate.getFree());
      guessVector.add(1.0, candidate.getNonnegative());
      guessVector.add(1.0, candidate.getNonpositive());
      SparseVector diffVector = goldVector.add(-1.0, guessVector);
      diffs[candidateIndex] = diffVector;
      double diffScore = weights.dotProduct(diffVector);
      double loss = losses.get(candidateIndex);
      a[candidateIndex] = diffVector.getValueArray();
      b[candidateIndex] = loss - diffScore;
      ++candidateIndex;
    }
    double[] alpha = QP.runHildreth(a, b);
    for (int i = 0; i < alpha.length; ++i){
      weights.add(alpha[i], diffs[i]);
    }
//
//    FeatureVector guess = candidates.get(0);
//    //take the feature difference
//    SparseVector diffFeatureFree = gold.getFree().add(-1.0, guess.getFree());
//    diffFeatureFree.compactify();
//    double diffScore = weights.dotProduct(diffFeatureFree);
//    double loss = 1.0 - losses.getF1();
//    //int[] indices = diffFeature.getIndices();
//    double[] values = diffFeatureFree.getValueArray();
//    double[][] a = new double[][]{values};
//    double[] b = new double[]{loss - diffScore};
//    double[] alpha = QP.runHildreth(a, b);
//    weights.add(alpha[0], diffFeatureFree);
  }
}
