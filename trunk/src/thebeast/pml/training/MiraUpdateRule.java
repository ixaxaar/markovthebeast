package thebeast.pml.training;

import thebeast.pml.FeatureVector;
import thebeast.pml.PropertyName;
import thebeast.pml.SparseVector;
import thebeast.pml.Weights;
import thebeast.util.Profiler;
import thebeast.util.QP;
import thebeast.util.TreeProfiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Update weights according to (Single-best) MIRA as presented in McDonald et al. 2005.
 *
 * @author Sebastian Riedel
 */
public class MiraUpdateRule implements UpdateRule {

  private boolean enforceSigns = true;

  //private int[] mapping;
  //private Weights weights;

  private int epoch = 0;

  private static Profiler profiler = new TreeProfiler();

  public void endEpoch() {
    epoch = 0;
  }


  public void update(FeatureVector gold, List<FeatureVector> candidates, List<Double> losses, Weights weights) {
    profiler.start("build difs");

    ArrayList<SparseVector> allVectors = new ArrayList<SparseVector>(candidates.size());
    int[][] nnIndices = new int[candidates.size() + 1][];
    int[][] npIndices = new int[candidates.size() + 1][];
    nnIndices[0] = weights.unionIndices(gold.getNonnegative().getIndexArray(), gold.getLocalNonnegativeIndices());
    npIndices[0] = weights.unionIndices(gold.getNonpositive().getIndexArray(), gold.getLocalNonpositiveIndices());
    //System.out.println(Arrays.toString(gold.getLocalNonpositiveIndices()));
    int c = 1;
    for (FeatureVector candidate : candidates) {
      allVectors.add(candidate.getAll());
      if (enforceSigns) {
        nnIndices[c] = weights.unionIndices(candidate.getNonnegative().getIndexArray(),
                candidate.getLocalNonnegativeIndices());
        //System.out.println(Arrays.toString(candidate.getLocalNonpositiveIndices()));
        npIndices[c] = weights.unionIndices(candidate.getNonpositive().getIndexArray(),
                candidate.getLocalNonpositiveIndices());
      }
      ++c;
    }
    List<SparseVector> diffVectors = weights.add(gold.getAll(), -1.0, allVectors);

    //System.out.println(weights.toString(diffVectors.get(0)));

    int nnCount = 0;
    int npCount = 0;

    SparseVector nnOld = null;
    SparseVector npOld = null;

    int rebasedSize = diffVectors.get(0).size();
    int base[] = null;

    if (enforceSigns) {
      base = diffVectors.get(0).getIndexArray();
      int[] nnAllIndices = weights.unionIndices(nnIndices);
      int[] npAllIndices = weights.unionIndices(npIndices);
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
//      if (epoch == 0){
//        System.out.println("Diff " + candidateIndex);
//        System.out.println(weights.toString(diffVector));
//      }

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
      //System.out.println(Arrays.toString(a[0]));
      //System.out.println(Arrays.toString(b));
      weights.add(1.0, new SparseVector(base, x));
      //System.out.println(Arrays.toString(b));
      //System.out.println(Arrays.toString(x));
//      double lowest = Double.POSITIVE_INFINITY;
//      for (int i = 0; i < rebasedSize; ++i){
//        if (x[i] < lowest) lowest = x[i];
//      }
//      System.out.println(lowest);
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

    ++epoch;
  }

  public void setProperty(PropertyName name, Object value) {
    if (name.getHead().equals("signed"))
      enforceSigns = (Boolean) value;
  }

  public Object getProperty(PropertyName name) {
    return null;
  }
}
