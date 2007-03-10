package thebeast.pml.training;

import thebeast.pml.FeatureVector;
import thebeast.pml.SparseVector;
import thebeast.pml.Weights;
import thebeast.util.Profiler;
import thebeast.util.QP;
import thebeast.util.TreeProfiler;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Update weights according to (Single-best) MIRA as presented in McDonald et al. 2005.
 *
 * @author Sebastian Riedel
 */
public class MiraUpdateRule implements UpdateRule {

  private boolean enforceSigns = false;

  //private int[] mapping;
  //private Weights weights;

  private static Profiler profiler = new TreeProfiler();

  public void endEpoch() {

  }


  public void update(FeatureVector gold, List<FeatureVector> candidates, List<Double> losses, Weights weights) {
    profiler.start("build difs");

    ArrayList<SparseVector> allVectors = new ArrayList<SparseVector>(candidates.size());
    int[][] nnIndices = new int[candidates.size() + 1][];
    int[][] npIndices = new int[candidates.size() + 1][];
    nnIndices[0] = gold.getNonnegative().getIndexArray();
    npIndices[0] = gold.getNonpositive().getIndexArray();
    int c = 1;
    for (FeatureVector candidate : candidates) {
      allVectors.add(candidate.getAll());
      if (enforceSigns) {
        nnIndices[c] = candidate.getNonnegative().getIndexArray();
        npIndices[c] = candidate.getNonpositive().getIndexArray();
      }
      ++c;
    }
    List<SparseVector> diffVectors = weights.add(gold.getAll(), -1.0, allVectors);

    int nnCount = 0;
    int npCount = 0;

    SparseVector nnOld = null;
    SparseVector npOld = null;

    int rebasedSize = diffVectors.get(0).size();
    int base[] = null;

    if (enforceSigns) {
      base = diffVectors.get(0).getIndexArray();
      int[] nnAllIndices = weights.intersectIndices(nnIndices);
      int[] npAllIndices = weights.intersectIndices(npIndices);
      nnOld = weights.getSubWeights(base, nnAllIndices);
      npOld = weights.getSubWeights(base, npAllIndices);
      nnCount = nnAllIndices.length;
      npCount = npAllIndices.length;
    }

    double[][] a = new double[candidates.size()][];
    double[] b = new double[candidates.size()];
    double[] d = new double[candidates.size()];
    SparseVector[] diffs = new SparseVector[candidates.size()];

    for (int candidateIndex = 0; candidateIndex < candidates.size(); ++candidateIndex) {
      SparseVector diffVector = diffVectors.get(candidateIndex);
      diffs[candidateIndex] = diffVector;
      double diffScore = weights.dotProduct(diffVector);
      double loss = losses.get(candidateIndex);
      a[candidateIndex] = diffVector.getValueArray();
      b[candidateIndex] = loss - diffScore;
      d[candidateIndex] = diffScore;
    }

    if (enforceSigns) {
      double[] lb = new double[rebasedSize];
      double[] ub = new double[rebasedSize];
      for (int i = 0; i < rebasedSize; ++i) {
        lb[i] = Double.NEGATIVE_INFINITY;
        ub[i] = Double.POSITIVE_INFINITY;
      }
      int[] nnRebased = nnOld.getIndexArray();
      int[] npRebased = npOld.getIndexArray();
      double[] nnWeights = nnOld.getValueArray();
      double[] npWeights = npOld.getValueArray();
      for (int i = 0; i < nnCount; ++i) {
        lb[nnRebased[i]] = -nnWeights[i];
      }
      for (int i = 0; i < npCount; ++i) {
        ub[npRebased[i]] = -npWeights[i];
      }

      double[] x = QP.art2(a, b, lb, ub);
      weights.add(1.0, new SparseVector(base, x));

    } else {
      profiler.end();
      profiler.start("qp");
      double[] alpha = QP.runHildreth(a, b);
      profiler.end();
      profiler.start("update");
      for (int i = 0; i < alpha.length; ++i) {
        weights.add(alpha[i], diffs[i]);
      }
      profiler.end();
    }

    //to test
//    double[] newD = new double[candidates.size()];
//    double[] newW = new double[a[0].length];
//    for (int i = 0; i < newD.length; ++i){
//      for (int j = 0; j < newW.length; ++j)
//        newW[j] += a[i][j] * alpha[i];
//    }
//    for (int i = 0; i < newD.length; ++i){
//      newD[i] = 0;
//      for (int j = 0; j < newW.length; ++j)
//        newD[i] += newW[j] * a[i][j];
//    }
//
//    System.out.println("l: " + losses);
//    System.out.println("d: " + Arrays.toString(d));
//    System.out.println("b: " + Arrays.toString(b));
//    System.out.println("a: " + Arrays.toString(alpha));
//    System.out.println("n: " + Arrays.toString(newD));
    //System.out.println(profiler);
  }
}
