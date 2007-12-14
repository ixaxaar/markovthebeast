package thebeast.osi;

import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class CbcModel {
  private OsiSolverJNI osiSolver;
  private long pointer;

  static {
    System.loadLibrary("cbc");
  }

  public CbcModel(OsiSolverJNI osiSolver) {
    pointer = createCbcModel(osiSolver.getPointer());
  }

  private native static long createCbcModel(long osiPointer);


  public void branchAndBound() {
    branchAndBound(pointer);
  }

  private native void branchAndBound(long pointer);


  public OsiSolverJNI solver() {
    return OsiSolverJNI.create(solver(pointer));
  }

  private native static long solver(long pointer);

  public OsiSolverJNI referenceSolver() {
    return OsiSolverJNI.create(referenceSolver(pointer));
  }

  private native static long referenceSolver(long pointer);

  public void saveReferenceSolver() {
    saveReferenceSolver(pointer);
  }

  private native static void saveReferenceSolver(long pointer);


  public void resetToReferenceSolver() {
    resetToReferenceSolver(pointer);
  }

  public native static void resetToReferenceSolver(long pointer);

  public double[] bestSolution() {
    return bestSolution(pointer);
  }

  private native double[] bestSolution(long pointer);

  public void reset() {
    osiSolver.reset();
  }

  public void delete() {
    delete(pointer);
  }

  private native void delete(long ptr);

  public void setMaximumSeconds(double seconds) {
    setMaximumSeconds(seconds, pointer);
  }

  private native void setMaximumSeconds(double seconds, long pointer);


  public void setLogLevel(int level) {
    setLogLevel(level, pointer);
  }

  public native void setLogLevel(int level, long pointer);


  public void addCglProbing(boolean useObjective, int maxPass, int maxPassRoot,
                            int maxProbe, int maxProbeRoot, int maxLook, int maxLookRoot,
                            int maxElements, int rowCuts) {
    addCglProbing(useObjective, maxPass, maxPassRoot, maxProbe,
            maxProbeRoot, maxLook, maxLookRoot, maxElements, rowCuts, pointer);
  }

  private native void addCglProbing(boolean useObjective, int maxPass, int maxPassRoot,
                                    int maxProbe, int maxProbeRoot, int maxLook, int maxLookRoot,
                                    int maxElements, int rowCuts, long pointer);


  public void addCglGomory(int limit) {
    addCglGomory(limit, pointer);
  }

  private native void addCglGomory(int limit, long pointer);

  public void addCglKnapsackCover() {
    addCglKnapsackCover(pointer);
  }

  private native void addCglKnapsackCover(long pointer);

  public void addCglRedsplit(int limit) {
    addCglRedsplit(limit, pointer);
  }

  private native void addCglRedsplit(int limit, long pointer);


  public void addCglClique(boolean starCliqueReport, boolean rowCliqueReport) {
    addCglClique(starCliqueReport, rowCliqueReport, pointer);
  }

  private native void addCglClique(boolean starCliqueReport, boolean rowCliqueReport, long pointer);

  public void addCglMixedIntegerRounding2() {
    addCglMixedIntegerRounding2(pointer);
  }

  private native void addCglMixedIntegerRounding2(long pointer);

  public void addCglFlowCover() {
    addCglFlowCover(pointer);
  }

  private native void addCglFlowCover(long pointer);

  public boolean setAllowableGap(double value){
    return setAllowableGap(value,pointer);
  }

  private native boolean setAllowableGap(double value, long pointer);

  public boolean setAllowableFractionGap(double value){
    return setAllowableFractionGap(value,pointer);
  }

  private native boolean setAllowableFractionGap(double value, long pointer);


  public boolean setAllowablePercentageGap(double value){
    return setAllowablePercentageGap(value,pointer);
  }

  private native boolean setAllowablePercentageGap(double value, long pointer);

  public boolean setMaximumNodes(int value){
    return setMaximumNodes(value,pointer);
  }

  private native static boolean setMaximumNodes(int value, long pointer);

  public static void main(String[] args) {
    OsiSolverJNI solver = OsiSolverJNI.create(OsiSolverJNI.Implementation.CLP);
    CbcModel model = new CbcModel(solver);
    solver = model.referenceSolver();
    solver.reset();
    solver.addCol(0, new int[0], new double[0], 0, 1.5, 1.0);
    solver.addCol(0, new int[0], new double[0], 0, 1.5, 1.0);
    solver.addRow(2, new int[]{0, 1}, new double[]{1.0, 1.0}, -OsiSolverJNI.INF, 1.0);
    solver.setObjSense(-1);
    solver.setInteger(1);

    //model.setLogLevel(0);
    model.resetToReferenceSolver();
    //model.setLogLevel(0);
    model.branchAndBound();
    double[] solution = model.bestSolution();
    System.out.println(Arrays.toString(solution));
    solver.addRow(1, new int[]{0}, new double[]{1.0}, 0.2, 0.4);
    //solver.setInteger(1);
    model.resetToReferenceSolver();
    //model.addCglProbing(true, 1, 5, 10, 1000, 50, 500, 200, 3);
    model.addCglGomory(300);
    model.addCglKnapsackCover();
    model.addCglMixedIntegerRounding2();
    //model.addCglFlowCover();
    model.addCglClique(false, false);
    model.addCglRedsplit(200);
    //model.setLogLevel(0);
    model.branchAndBound();
    solution = model.bestSolution();
    System.out.println(Arrays.toString(solution));
    solver.reset();

  }


}

/*
 generator1.setUsingObjective(true);
  generator1.setMaxPass(1);
  generator1.setMaxPassRoot(5);
  // Number of unsatisfied variables to look at
  generator1.setMaxProbe(10);
  generator1.setMaxProbeRoot(1000);
  // How far to follow the consequences
  generator1.setMaxLook(50);
  generator1.setMaxLookRoot(500);
  // Only look at rows with fewer than this number of elements
  generator1.setMaxElements(200);
  generator1.setRowCuts(3);
*/