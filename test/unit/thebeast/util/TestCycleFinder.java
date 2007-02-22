package thebeast.util;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
public class TestCycleFinder extends TestCase {

  public void testSimpleCycle1() {
    int[][] graph = new int[][]{
            new int[]{1, 2},
            new int[]{2, 3},
            new int[]{2, 4},
            new int[]{0, 1},
            new int[]{3, 0},
    };
    int[][] cycles = CycleFinder.findCycleVertices(graph, 5);
    for (int[] cycle : cycles) {
      System.out.println("Arrays.toString(cycle) = " + Arrays.toString(cycle));
    }
    HashSet<Integer> expected = new HashSet<Integer>();
    expected.add(3);
    expected.add(2);
    expected.add(1);
    expected.add(0);
    assertEquals(1, cycles.length);
    HashSet<Integer> actual = new HashSet<Integer>();
    for (int i : cycles[0]) actual.add(i);
    assertEquals(expected, actual);
  }

  public void testSimpleCycle2() {
    int[][] graph = new int[][]{
            new int[]{2, 4},
            new int[]{3, 0},
            new int[]{2, 3},
            new int[]{1, 2},
            new int[]{0, 1},
    };
    int[][] cycles = CycleFinder.findCycleVertices(graph, 5);
    for (int[] cycle : cycles) {
      System.out.println("Arrays.toString(cycle) = " + Arrays.toString(cycle));
    }
    HashSet<Integer> expected = new HashSet<Integer>();
    expected.add(3);
    expected.add(2);
    expected.add(1);
    expected.add(0);
    assertEquals(1, cycles.length);
    HashSet<Integer> actual = new HashSet<Integer>();
    for (int i : cycles[0]) actual.add(i);
    assertEquals(expected, actual);
  }

  public void testSimpleCycle3() {
    int[][] graph = new int[][]{
            new int[]{0, 1},
            new int[]{1, 2},
            new int[]{2, 0},
            new int[]{2, 3},
            new int[]{3, 4},
            new int[]{4, 3},
    };
    int[][] cycles = CycleFinder.findCycleVertices(graph, 5);
    for (int[] cycle : cycles) {
      System.out.println("Arrays.toString(cycle) = " + Arrays.toString(cycle));
    }
    HashSet<Integer> expected1 = new HashSet<Integer>();
    expected1.add(2);
    expected1.add(1);
    expected1.add(0);
    HashSet<Integer> expected2 = new HashSet<Integer>();
    expected2.add(3);
    expected2.add(4);
    HashSet<HashSet<Integer>> allExpected = new HashSet<HashSet<Integer>>();
    allExpected.add(expected1);
    allExpected.add(expected2);

    assertEquals(2, cycles.length);
    HashSet<HashSet<Integer>> allActual = new HashSet<HashSet<Integer>>();
    HashSet<Integer> actual1 = new HashSet<Integer>();
    for (int i : cycles[0]) actual1.add(i);
    HashSet<Integer> actual2 = new HashSet<Integer>();
    for (int i : cycles[1]) actual2.add(i);
    allActual.add(actual1);
    allActual.add(actual2);
    assertEquals(allExpected, allActual);
  }

  public void testSimpleCycle4() {
    int[][] graph = new int[][]{
            new int[]{2, 0},
            new int[]{4, 0},
            new int[]{3, 4},
            new int[]{2, 3},
            new int[]{0, 1},
            new int[]{3, 0},
            new int[]{0, 3},
            new int[]{3, 5},
    };
    int[][] cycles = CycleFinder.findCycleVertices(graph, 6);
    for (int[] cycle : cycles) {
      System.out.println("Arrays.toString(cycle) = " + Arrays.toString(cycle));
    }
    assertEquals(2, cycles[0].length);
    assertEquals(0, cycles[0][0]);
    assertEquals(3, cycles[0][1]);
    assertEquals(3, cycles[1].length);
    assertEquals(4, cycles[1][0]);
    assertEquals(0, cycles[1][1]);
    assertEquals(3, cycles[1][2]);

  }

  public void testSimpleCycle5() {
    int[][] graph = new int[][]{
            new int[]{0, 1},
            new int[]{0, 3},
            new int[]{3, 5},
            new int[]{2, 3},
            new int[]{3, 4},
            new int[]{3, 0},
            new int[]{4, 0},
            new int[]{2, 0},
    };
    int[][] cycles = CycleFinder.findCycleVertices(graph, 6);
    for (int[] cycle : cycles) {
      System.out.println("Arrays.toString(cycle) = " + Arrays.toString(cycle));
    }
    assertEquals(2, cycles[0].length);
    assertEquals(0, cycles[0][0]);
    assertEquals(3, cycles[0][1]);
    assertEquals(3, cycles[1].length);
    assertEquals(4, cycles[1][0]);
    assertEquals(0, cycles[1][1]);
    assertEquals(3, cycles[1][2]);

  }

  public void testSimpleCycle6() {
    int[][] graph = new int[][]{
            new int[]{0, 0},
            new int[]{0, 1},
            new int[]{1, 1},
            new int[]{0, 2},
            new int[]{1, 2},
            new int[]{2, 2},
            new int[]{2, 1},
            new int[]{2, 0},
            new int[]{1, 0},
    };
    int[][] cycles = CycleFinder.findCycleVertices(graph, 3);
    for (int[] cycle : cycles) {
      System.out.println("Arrays.toString(cycle) = " + Arrays.toString(cycle));
    }
    assertEquals(1, cycles[0].length);
    assertEquals(0, cycles[0][0]);
    assertEquals(1, cycles[1].length);
    assertEquals(1, cycles[1][0]);
    assertEquals(1, cycles[2].length);
    assertEquals(2, cycles[2][0]);
  }

  public void testSimpleCycle7() {
    int[][] graph = new int[][]{
            new int[]{4, 12},
            new int[]{3, 2},
            new int[]{17, 18},
            new int[]{9, 10},
            new int[]{10, 12},
            new int[]{6, 9},
            new int[]{4, 6},
            new int[]{2, 4},
            new int[]{12, 3},
    };
    int[][] cycles = CycleFinder.findCycleVertices(graph, 21);
    for (int[] cycle : cycles) {
      System.out.println("Arrays.toString(cycle) = " + Arrays.toString(cycle));
    }
    assertEquals(4, cycles[0].length);
    assertEquals(2, cycles[0][0]);
    assertEquals(4, cycles[0][1]);
    assertEquals(12, cycles[0][2]);
    assertEquals(3, cycles[0][3]);
    assertEquals(7, cycles[1].length);
    assertEquals(6, cycles[1][0]);
    assertEquals(9, cycles[1][1]);
    assertEquals(10, cycles[1][2]);
    assertEquals(12, cycles[1][3]);
    assertEquals(3, cycles[1][4]);
    assertEquals(2, cycles[1][5]);
    assertEquals(4, cycles[1][6]);

  }

  public static String toArrayDef(int[][] graph) {
    StringBuffer result = new StringBuffer("int[][] graph = new int[][]{");
    for (int[] edge : graph)
      result.append("\nnew int[] {").append(edge[0]).append(", ").append(edge[1]).append("},");
    result.append("};");
    return result.toString();
  }

}
