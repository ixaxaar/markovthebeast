package thebeast.osi;

/**
 * @author Sebastian Riedel
 */
public interface OsiSolver {

  public static final double INF = 1E100;

  enum OsiHintParam {
    OsiDoPresolveInInitial, OsiDoDualInInitial, OsiDoPresolveInResolve, OsiDoDualInResolve,
    OsiDoScale, OsiDoCrash, OsiDoReducePrint, OsiDoInBranchAndCut,
    OsiLastHintParam
  }

  enum OsiHintStrength {
    OsiHintIgnore, OsiHintTry, OsiHintDo, OsiForceDo
  }

  boolean setHintParam(OsiHintParam key, boolean yesNo, OsiHintStrength strength);

  void intialSolve();

  void resolve();

  void branchAndBound();

  double[] getColSolution();

  int getNumCols();

  void setColLower(int elementIndex, double elementValue);

  void setColUpper(int elementIndex, double elementValue);

  void setObjCooeff(int elementIndex, double elementValue);

  void addCol(int numberElements, int[] rows, double[] elements, double collb, double colub, double obj);

  void addRow(int numberElements, int[] columns, double[] element, double rowlb, double rowub);

  void setInteger(int index);

  void setObjSense(double s);

  double getObjValue();

  void reset();

  int getNumRows();

  void setCbcLogLevel(int level);

}
