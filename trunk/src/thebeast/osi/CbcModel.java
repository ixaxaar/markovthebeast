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




  public void branchAndBound(){
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

  public void saveReferenceSolver(){
    saveReferenceSolver(pointer);
  }

  private native static void saveReferenceSolver(long pointer);


  public void resetToReferenceSolver(){
    resetToReferenceSolver(pointer);
  }

  public native static void resetToReferenceSolver(long pointer);

  public double[] bestSolution(){
    return bestSolution(pointer);
  }

  private native double[] bestSolution(long pointer);

  public void reset(){
    osiSolver.reset();  
  }

  public void delete(){
    delete(pointer); 
  }

  private native void delete(long ptr);

  public void setMaximumSeconds(double seconds){
    setMaximumSeconds(seconds,pointer);
  }

  private native void setMaximumSeconds(double seconds, long pointer);


  public void setLogLevel(int level){
    setLogLevel(level,pointer);  
  }

  public native void setLogLevel(int level, long pointer);

  public static void main(String[] args) {
    OsiSolverJNI solver = OsiSolverJNI.create(OsiSolverJNI.Implementation.CLP);
    CbcModel model = new CbcModel(solver);
    solver = model.referenceSolver();
    solver.addCol(0,new int[0],new double[0],0,1.5,1.0);
    solver.addCol(0,new int[0],new double[0],0,1.5,1.0);
//    solver.addCols(2, new int[2][0], new double[2][0], new double[]{0.0, 0.0}, new double[]{1.5,1.5}, new double[]{1.0,1.0});
//    solver.addRows(2, new int[][]{new int[]{0,1}, new int[]{0}},
//            new double[][]{new double[]{1.0,1.0}, new double[]{1.0}}, new double[]{-INF, 0.2}, new double[]{1.0,0.4});
    solver.addRow(2,new int[]{0,1},new double[]{1.0,1.0},-OsiSolverJNI.INF,1.0);
    solver.setObjSense(-1);
    //model.setLogLevel(0);
    model.resetToReferenceSolver();
    //model.setLogLevel(0);
    model.branchAndBound();
    double[] solution = model.bestSolution();
    System.out.println(Arrays.toString(solution));
    solver.addRow(1,new int[]{0},  new double[]{1.0}, 0.2,0.4);
    model.resetToReferenceSolver();
    //model.setLogLevel(0);
    model.branchAndBound();
    solution = model.bestSolution();
    System.out.println(Arrays.toString(solution));
    solver.reset();

  }


}
