package thebeast.pml.solve.weightedsat;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Sep-2007 Time: 20:30:50
 */
public class TestMaxProduct extends TestCase {

  public void testMaxProduct(){
    MaxProduct mp = new MaxProduct();
    mp.addAtoms(new double[]{3,-3,0});
    mp.addClauses(
            new WeightedSatClause(20, new int[][]{new int[]{0,1}}, new boolean[][]{new boolean[]{false,false}},null),
            new WeightedSatClause(20, new int[][]{new int[]{1,2}}, new boolean[][]{new boolean[]{false,false}},null),
            new WeightedSatClause(20, new int[][]{new int[]{0,2}}, new boolean[][]{new boolean[]{false,false}},null));
    mp.setMaxEps(0.01);
    mp.setMaxIterations(1000);
    boolean[] result = mp.solve();
    System.out.println(mp.getIterationCount());
    System.out.println(Arrays.toString(result));
    


  }
}
