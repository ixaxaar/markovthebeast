package thebeast.pml.solve.weightedsat;

/**
 * @author Sebastian Riedel
 */
public class WeightedClause {
  public final int[][] atoms;
  public final boolean[][] signs;
  public final double score;

  public WeightedClause(int[][] atoms, boolean[][] signs, double score) {
    this.atoms = atoms;
    this.signs = signs;
    this.score = score;
  }
}
