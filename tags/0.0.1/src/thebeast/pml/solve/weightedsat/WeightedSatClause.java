package thebeast.pml.solve.weightedsat;

import thebeast.util.Util;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class WeightedSatClause {
  public final int[][] atoms;
  public final boolean[][] signs;
  public final double score;
  public final Constraint[][] constraints;

  public static class Constraint {
    public final int lb, ub;
    public final int[] indices;

    public Constraint(int lb, int ub, int[] indices) {
      this.lb = lb;
      this.ub = ub;
      this.indices = indices;
    }
  }

  public WeightedSatClause(double score, int[][] atoms, boolean[][] signs, Constraint[][] constraints) {
    this.atoms = atoms;
    this.signs = signs;
    this.score = score;
    this.constraints = constraints;
  }

  /**
   * Returns true iff this clause contains cardinality constraints.
   *
   * @return true iff this clause contains cardinality constraints.
   */
  public boolean hasCardinalityConstraints() {
    if (constraints == null) return false;
    for (Constraint[] c : constraints) {
      if (c != null && c.length > 0) return true;
    }
    return false;
  }

  /**
   * @return true iff this clause is a conjunction (conjunction of disjunctions with single atoms).
   */
  public boolean isConjunction() {
    if (hasCardinalityConstraints()) return false;
    for (int[] dis : atoms)
      if (dis.length > 1) return false;
    return true;
  }

  /**
   * Creates one clause for each disjunction and distributes the score equally.
   *
   * @return an array with clauses, one clause per disjunction.
   */
  public WeightedSatClause[] separate() {
    if (atoms.length == 1) return new WeightedSatClause[]{this};
    WeightedSatClause[] result = new WeightedSatClause[atoms.length];
    for (int i = 0; i < atoms.length; ++i) {
      result[i] = new WeightedSatClause(score / (double) atoms.length,
              new int[][]{atoms[i]}, new boolean[][]{signs[i]}, new Constraint[][]{constraints[i]});
    }
    return result;
  }

  /**
   * Converts cardinality constraints into plain disjunctions. Works only for all <= constraints and >= {1,n}
   *
   * @return an equivalent clause without cardinality constraints
   */
  public WeightedSatClause expandCardinalityConstraints() {
    if (constraints == null) return this;
    ArrayList<int[]> allNewAtoms = new ArrayList<int[]>();
    ArrayList<boolean[]> allNewSigns = new ArrayList<boolean[]>();
    for (int i = 0; i < constraints.length; ++i) {
      Constraint[] constraints4disjunction = constraints[i];
      if (constraints4disjunction != null) {
        if (constraints4disjunction.length > 1)
          throw new RuntimeException("Can't do more than one card constraint per" +
                  "disjunction");
        Constraint constraint = constraints4disjunction[0];
        if (constraint.lb == Integer.MIN_VALUE) {
          int newDisSize = atoms[i].length + constraint.ub + 1;
          int[][] nBalls = Util.nBalls(constraint.ub + 1, constraint.indices);
          for (int dis = 0; dis < nBalls.length; ++dis) {
            int[] newDis = new int[newDisSize];
            boolean[] newSigns = new boolean[newDisSize];
            System.arraycopy(atoms[i], 0, newDis, 0, atoms[i].length);
            System.arraycopy(signs[i], 0, newSigns, 0, signs[i].length);
            System.arraycopy(nBalls[dis], 0, newDis, atoms[i].length, nBalls[dis].length);
            Arrays.fill(newSigns, signs[i].length, newDisSize, false);
            allNewAtoms.add(newDis);
            allNewSigns.add(newSigns);
          }

        } else if (constraint.ub == Integer.MAX_VALUE) {
          if (constraint.lb == 1) {
            int newDisSize = atoms[i].length + constraint.indices.length;
            int[] newDis = new int[newDisSize];
            boolean[] newSigns = new boolean[newDisSize];
            System.arraycopy(atoms[i], 0, newDis, 0, atoms[i].length);
            System.arraycopy(signs[i], 0, newSigns, 0, signs[i].length);
            System.arraycopy(constraint.indices, 0, newDis, atoms[i].length, constraint.indices.length);
            Arrays.fill(newSigns, signs[i].length, signs[i].length+ constraint.indices.length, true);
            allNewAtoms.add(newDis);
            allNewSigns.add(newSigns);
          } else if (constraint.lb == constraint.indices.length) {

            int newDisSize = atoms[i].length + 1;
            for (int index : constraint.indices) {
              int[] newDis = new int[newDisSize];
              boolean[] newSigns = new boolean[newDisSize];
              System.arraycopy(atoms[i], 0, newDis, 0, atoms[i].length);
              System.arraycopy(signs[i], 0, newSigns, 0, signs[i].length);
              newDis[atoms[i].length] = index;
              newSigns[signs[i].length] = true;
              allNewAtoms.add(newDis);
              allNewSigns.add(newSigns);
            }

          } else throw new RuntimeException("Can't do >= k with k not in {1,n}");

        } else throw new RuntimeException("Card constraint must be LEQ or GEQ, not EQ");

      } else {
        allNewAtoms.add(atoms[i]);
        allNewSigns.add(signs[i]);
      }
    }
    int[][] newAtomsArray = allNewAtoms.toArray(new int[][]{});
    boolean[][] newSignsArray = allNewSigns.toArray(new boolean[][]{});

    return new WeightedSatClause(score, newAtomsArray, newSignsArray, new Constraint[allNewAtoms.size()][]);
  }


  /**
   * Removes all disjunctions that contain a v not(a) and shortens disjunctions with a v a or not(a) v not(a).
   *
   * @return a compact version of this clause without any redundancies.
   */
  public WeightedSatClause normalize() {
    int newDisjunctionCount = 0;
    boolean[][] newDisjunctionSigns = new boolean[this.signs.length][];
    int[][] newDisjunctionAtoms = new int[this.signs.length][];

    for (int disjunction = 0; disjunction < this.signs.length; ++disjunction) {
      int[] atoms = this.atoms[disjunction];
      boolean[] signs = this.signs[disjunction];
      int length = atoms.length;
      if (length == 0) continue;
      boolean[] toSkip = new boolean[length];
      int toSkipCount = 0;
      boolean alwaysTrue = false;
      outer:
      for (int i = 0; i < length; ++i)
        for (int j = i + 1; j < length; ++j) {
          if (atoms[i] == atoms[j]) {
            if (signs[i] == signs[j]) {
              ++toSkipCount;
              toSkip[i] = true;
              continue outer;
            } else {
              alwaysTrue = true;
              break outer;
            }
          }
        }
      if (alwaysTrue) continue;
      int newLength = length - toSkipCount;
      int[] newAtoms = new int[newLength];
      boolean[] newSigns = new boolean[newLength];
      int pointer = 0;
      for (int i = 0; i < length; ++i) {
        if (!toSkip[i]) {
          newAtoms[pointer] = atoms[i];
          newSigns[pointer] = signs[i];
          ++pointer;
        }
      }
      newDisjunctionAtoms[newDisjunctionCount] = newAtoms;
      newDisjunctionSigns[newDisjunctionCount] = newSigns;
      ++newDisjunctionCount;

    }
    if (newDisjunctionCount == 0) return null;
    int[][] newAtoms = new int[newDisjunctionCount][];
    boolean[][] newSigns = new boolean[newDisjunctionCount][];
    System.arraycopy(newDisjunctionAtoms, 0, newAtoms, 0, newDisjunctionCount);
    System.arraycopy(newDisjunctionSigns, 0, newSigns, 0, newDisjunctionCount);
    return new WeightedSatClause(score, newAtoms, newSigns, new Constraint[newAtoms.length][]);
  }


}
