package thebeast.pml.solve.weightedsat;

/**
 * @author Sebastian Riedel
 */
public class WeightedSatClause {
  public final int[][] atoms;
  public final boolean[][] signs;
  public final double score;

  public WeightedSatClause(double score, int[][] atoms, boolean[][] signs) {
    this.atoms = atoms;
    this.signs = signs;
    this.score = score;
  }

  public WeightedSatClause normalize(){
    
    return null;
  }

}
