package thebeast.util;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Feb-2007 Time: 17:43:24
 */
public class TestQP extends TestCase {

  public void testHildreth() {
    double[][] a = new double[][]{new double[]{1}};
    double[] b = new double[]{10 - 1000};
    double[] alpha = QP.runHildreth(a, b);
    assertEquals(0.0, alpha[0]);
    System.out.println(Arrays.toString(alpha));

  }

  public void testArt2() {
    double[] a_1 = new double[]{1, 1, 1};
    double[] a_2 = new double[]{0, 1, 1};
    double[] a_3 = new double[]{0, 0, 1};
    double[] b = new double[]{1, 1, 1};
    double inf = Double.POSITIVE_INFINITY;
    double[] lb = new double[]{1.0, -inf, -inf};
    double[] ub = new double[]{inf, -1.0, inf};

    double[][] a = new double[][]{a_1, a_2, a_3};
    double[] x = QP.art2(a, b, lb, ub);
    System.out.println(Arrays.toString(x));
  }

  public void testHildreth2() {
    double[] a_1 = new double[]{1, 1, 1};
    double[] a_2 = new double[]{0, 1, 1};
    double[] a_3 = new double[]{0, 0, 1};
    double[][] a = new double[][]{a_1, a_2, a_3};
    double[] b = new double[]{1, 1, -1};
    System.out.println(Arrays.toString(QP.runHildreth(a,b)));
  }

  public void testNegativity() {
    double[][] a = new double[3][];
    a[0] = new double[]{-1.0, 0,   0};
    a[1] = new double[]{0.0, 1.0, 0};
    a[2] = new double[]{0.0, 0,   1.0};
    double[] b = new double[]{1, 1, 1};
    double[] alpha = QP.runHildreth(a, b);
    System.out.println(Arrays.toString(alpha));
  }
}
