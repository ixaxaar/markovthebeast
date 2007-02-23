package thebeast.util;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Feb-2007 Time: 17:43:24
 */
public class TestQP extends TestCase {

  public void testHildreth(){
    double[][] a = new double[][]{new double[]{1}};
    double[] b = new double[]{10-1000};
    double[] alpha = QP.runHildreth(a, b);
    assertEquals(0.0, alpha[0]);
    System.out.println(Arrays.toString(alpha));

  }
}
