package thebeast.pml.solve.weightedsat;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class TestMaxWalkSat extends TestCase {

  public void testSimple(){
    double[] scores = new double[]{1.0,0.5,2.0};
    boolean[] states = new boolean[]{true, false, false};

    MaxWalkSat maxWalkSat = new MaxWalkSat();

    maxWalkSat.init();
    maxWalkSat.addAtoms(states, scores);
    maxWalkSat.addClauses(
            new WeightedSatClause(-30, new int[][]{{0,1}}, new boolean[][]{{true,true}}),
            new WeightedSatClause(2,  new int[][]{{1,2}}, new boolean[][]{{true,true}})
    );

    boolean[] result = maxWalkSat.solve();
    assertEquals(false, result[0]);
    assertEquals(false, result[1]);
    assertEquals(true, result[2]);
    System.out.println(Arrays.toString(result));


  }

}
