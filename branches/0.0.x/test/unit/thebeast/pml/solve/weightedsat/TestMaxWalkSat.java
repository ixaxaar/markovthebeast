package thebeast.pml.solve.weightedsat;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class TestMaxWalkSat extends TestCase {

  public void testSimple(){
    double[] scores = new double[]{1.0,0.5,2.0};

    MaxWalkSat maxWalkSat = new MaxWalkSat();
    maxWalkSat.setSeed(1);
    maxWalkSat.init();
    maxWalkSat.setPickFromUnsatisfied(false);
    maxWalkSat.addAtoms(scores);
    maxWalkSat.addClauses(
            new WeightedSatClause(-30, new int[][]{{0,1}}, new boolean[][]{{true,true}},null),
            new WeightedSatClause(2,  new int[][]{{1,2}}, new boolean[][]{{true,true}},null)
    );

    boolean[] result = maxWalkSat.solve();
    assertEquals(false, result[0]);
    assertEquals(false, result[1]);
    assertEquals(true, result[2]);
    System.out.println(Arrays.toString(result));


  }

  public void testWithNodeInClause3times(){
    double[] scores = new double[]{-1.0,0.5,2.0};

    MaxWalkSat maxWalkSat = new MaxWalkSat();

    maxWalkSat.init();
    maxWalkSat.setSeed(0);
    maxWalkSat.setMaxFlips(10);
    maxWalkSat.addAtoms(scores);
    maxWalkSat.addClauses(
            new WeightedSatClause(5, new int[][]{{0,0}}, new boolean[][]{{false,false}},null),
            new WeightedSatClause(-30, new int[][]{{1,1,1}}, new boolean[][]{{false, false,true}},null),
            new WeightedSatClause(2,  new int[][]{{1,2}}, new boolean[][]{{true,true}},null)
    );

    boolean[] result = maxWalkSat.solve();
    System.out.println(Arrays.toString(result));
    assertEquals(10.5, maxWalkSat.getBestScore());
    assertEquals(false, result[0]);
    assertEquals(true, result[1]);
    assertEquals(true, result[2]);
  }


  public void testCardinalityConstraint(){
    double[] scores = new double[]{2.0,-0.5,-0.3};

    MaxWalkSat maxWalkSat = new MaxWalkSat();
    maxWalkSat.setSeed(1);
    maxWalkSat.setMaxFlips(10);
    maxWalkSat.init();
    maxWalkSat.addAtoms(scores);
    maxWalkSat.addClauses(
            new WeightedSatClause(20, new int[][]{{0}}, new boolean[][]{{false}},
                    new WeightedSatClause.Constraint[][]{ new WeightedSatClause.Constraint[]
                            { new WeightedSatClause.Constraint(1, Integer.MAX_VALUE, new int[]{1,2})}}));

    boolean[] result = maxWalkSat.solve();
    assertEquals(true, result[0]);
    assertEquals(false, result[1]);
    assertEquals(true, result[2]);
    System.out.println(Arrays.toString(result));


  }

}
