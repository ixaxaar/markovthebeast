package thebeast.pml.training;

import thebeast.pml.SparseVector;
import thebeast.pml.Evaluation;
import thebeast.pml.Weights;
import thebeast.util.QP;

/**
 * @author Sebastian Riedel
 */
public class MiraUpdateRule implements UpdateRule {
  public void endEpoch() {

  }

  public void update(SparseVector gold, SparseVector guess, Evaluation evaluation, Weights weights) {
    //take the feature difference
    SparseVector diffFeature = gold.add(-1.0, guess);
    diffFeature.compactify();
    double diffScore = weights.dotProduct(diffFeature);
    double loss = 1.0 - evaluation.getF1();
    //int[] indices = diffFeature.getIndices();
    double[] values = diffFeature.getValues();
    double[][] a = new double[][]{values};
    double[] b = new double[]{loss - diffScore};
    double[] alpha = QP.runHildreth(a,b);
    weights.add(alpha[0],diffFeature);
    //find
  }
}
