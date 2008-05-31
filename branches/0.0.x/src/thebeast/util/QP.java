package thebeast.util;

/**
 * @author Sebastian Riedel
 */
public class QP {
  /**
   * Runs the Hildreth algorithm for solving a quadratic program of the following form:
   * <p/>
   * minimize | w |^2
   * such that:
   * <p/>
   * w T a_i >= b_i for all i
   * <p/>
   * Generally, we're using this for machine learning, where w is a vector of weight parameters,
   * a_i is a feature vector difference which we'd like to have bigger than b_i, which we use
   * to represent the loss.
   *
   * @param a are Counters representing the vector a in the constraints
   * @param b are the real values on the right
   * @return the alpha multipliers for each a vector, such that
   *         <p/>
   *         Code in this method taken directly from Ryan McDonald's
   *         MST parser implementation.
   */

  public static double[] runHildreth(double[][] a, double[] b) {

    int i;
    int max_iter = 10000;
    double eps = 0.00000001;
    double zero = 0.000000000001;

    double[] alpha = new double[b.length];

    double[] F = new double[b.length];
    double[] kkt = new double[b.length];
    double max_kkt = Double.NEGATIVE_INFINITY;

    int K = a.length;

    double[][] A = new double[K][K];
    boolean[] is_computed = new boolean[K];
    for (i = 0; i < K; i++) {
      A[i][i] = dotProduct(a[i], a[i]);
      is_computed[i] = false;
    }

    int max_kkt_i = -1;

    for (i = 0; i < F.length; i++) {
      F[i] = b[i];
      kkt[i] = F[i];
      if (kkt[i] > max_kkt) {
        max_kkt = kkt[i];
        max_kkt_i = i;
      }
    }

    int iter = 0;
    double diff_alpha;
    double try_alpha;
    double add_alpha;

    while (max_kkt >= eps && iter < max_iter) {

      diff_alpha = A[max_kkt_i][max_kkt_i] <= zero ? 0.0 : F[max_kkt_i] / A[max_kkt_i][max_kkt_i];
      try_alpha = alpha[max_kkt_i] + diff_alpha;
      add_alpha = 0.0;

      if (try_alpha < 0.0)
        add_alpha = -1.0 * alpha[max_kkt_i];
      else
        add_alpha = diff_alpha;

      alpha[max_kkt_i] = alpha[max_kkt_i] + add_alpha;

      if (!is_computed[max_kkt_i]) {
        for (i = 0; i < K; i++) {
          A[i][max_kkt_i] = dotProduct(a[i], a[max_kkt_i]); // for
          // version
          // 1
          is_computed[max_kkt_i] = true;
        }
      }

      for (i = 0; i < F.length; i++) {
        F[i] -= add_alpha * A[i][max_kkt_i];
        kkt[i] = F[i];
        if (alpha[i] > zero)
          kkt[i] = Math.abs(F[i]);
      }

      max_kkt = Double.NEGATIVE_INFINITY;
      max_kkt_i = -1;
      for (i = 0; i < F.length; i++)
        if (kkt[i] > max_kkt) {
          max_kkt = kkt[i];
          max_kkt_i = i;
        }

      iter++;
    }

    return alpha;
  }


  /**
   * Minimizes 0.5 ||x||^2 with constraints a_i^T x >= b_i and bounds lb_j <= x_j <= ub_j using
   * Herman and Lent, "A family of iterative quadratic optimization algorithms for pairs of Bayesian
   * analysis of image reconstruction". Found in Yair Censor, "row-action methids for huge and sparse
   * systems and their applications". We use cyclic control here (rows are processed one by one and once
   * at the end the first row will be used again).
   *
   * @param a  the constraints, the array i represents the vector a_i
   * @param b  the lower bounds for the constraints
   * @param lb lower bounds for variables
   * @param ub upper bounds for variables
   * @return an array with x
   */
  public static double[] art2(double[][] a, double[] b, double[] lb, double[] ub) {
    double eps = 0.00000001;
    int maxIterations = 100;
    double[] norms = new double[a.length];
    int nonZeroCount = 0;
    //precalcute norms of rows
    for (int i = 0; i < a.length; ++i){
      norms[i] = dotProduct(a[i], a[i]);
      //System.out.println("n: " + norms[i]);
      if (norms[i] > eps || norms[i] < -eps) ++nonZeroCount;
    }
    //remove zero rows
    int m = nonZeroCount;
    int n = lb.length;
    if (m == 0) return new double[n];
    double[][] newA = new double[m][];
    double[] newB = new double[m];
    double[] newNorms = new double[m];
    int dst = 0;
    for (int i = 0; i < a.length; ++i)
      if (norms[i] > eps || norms[i] < -eps) {
        newA[dst] = a[i];
        newB[dst] = b[i];
        newNorms[dst] = norms[i];
        ++dst;
      }
    a = newA;
    b = newB;
    norms = newNorms;

    double[] x_hat = new double[n];
    double[] x = new double[n];
    double[] z = new double[m];
    double c;

    double lastNorm = 0;
    //iterate
    for (int i = 0; i < maxIterations; ++i) {
      int i_k = i % m;
      for (int t = 0; t < n; ++t) {
        double x_hat_t = x_hat[t];
        x[t] = x_hat_t < lb[t] ? lb[t] : x_hat_t > ub[t] ? ub[t] : x_hat_t;
      }
      double quotient = (b[i_k] - dotProduct(a[i_k], x)) / norms[i_k];
      double z_ik = z[i_k];
      c = z_ik > quotient ? z_ik : quotient;
      for (int t = 0; t < n; ++t){
        x_hat[t] += c * a[i_k][t];
      }
      z[i_k] -= c;
      if (i % m == m -1){
        double norm = dotProduct(x_hat,x_hat);
        //System.out.println("norm: " + norm);
        if (Math.abs(norm - lastNorm) < eps) break;
        lastNorm = norm;
      }
    }
    return x;
  }

  public static double dotProduct(double[] x, double[] y) {
    double result = 0;
    for (int i = 0; i < x.length; ++i) result += x[i] * y[i];
    return result;
  }

}
