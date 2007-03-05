package thebeast.osi;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
public final class OsiSolverJNI implements OsiSolver {

  private long pointer;
  private static HashSet<OsiSolverJNI> instances = new HashSet<OsiSolverJNI>();

  static {
    System.loadLibrary("osi");
  }

  private OsiSolverJNI(long pointer) {
    this.pointer = pointer;
  }

  public enum Implementation {
    CBC, CLP, CPLEX
  }

  public synchronized static OsiSolverJNI create(Implementation implementation) {
    OsiSolverJNI solver = new OsiSolverJNI(createImplementation(implementation.ordinal()));
    instances.add(solver);
    return solver;
  }

  public static void deleteAllSolvers(){
    for (OsiSolverJNI solver : instances)
      solver.delete(solver.pointer);
  }

  private static native long createImplementation(int ordinal);


  public boolean setHintParam(OsiHintParam key, boolean yesNo, OsiHintStrength strength) {
    return setHintParam(key.ordinal(), yesNo,strength.ordinal(),pointer);
  }

  private native static boolean setHintParam(int key, boolean yesNo, int strength, long ptr);


  public void intialSolve() {
    initialSolve(pointer);
  }

  private native void initialSolve(long pointer);

  public void resolve() {
    resolve(pointer);
  }

  private native void resolve(long pointer);

  public void branchAndBound() {
    branchAndBound(pointer);
  }

  private native void branchAndBound(long pointer);

  public double[] getColSolution() {
    return getColSolution(pointer);
  }

  public int getNumCols() {
    return getNumCols(pointer);
  }

  private native int getNumCols(long pointer);

  public int getNumRows(){
    return getNumRows(pointer);
  }

  public void setCbcLogLevel(int level) {
    setCbcLogLevel(level,pointer);
  }

  private static native void setCbcLogLevel(int level, long ptr);

  private native int getNumRows(long pointer);

  private native double[] getColSolution(long pointer);

  public void setColLower(int elementIndex, double elementValue) {
    setColLower(elementIndex, elementValue, pointer);
  }

  private native void setColLower(int elementIndex, double elementValue, long pointer);

  public void setColUpper(int elementIndex, double elementValue) {
    setColUpper(elementIndex, elementValue, pointer);
  }

  private native void setColUpper(int elementIndex, double elementValue, long pointer);

  public void setObjCooeff(int elementIndex, double elementValue) {
    setObjCooeff(elementIndex, elementValue, pointer);
  }

  private native void setObjCooeff(int elementIndex, double elementValue, long pointer);

  public void addCol(int numberElements, int[] rows, double[] elements, double collb, double colub, double obj) {
    addCol(numberElements, rows, elements, collb, colub, obj, pointer);
  }

  private native void addCol(int numberElements, int[] rows, double[] elements, double collb, double colub, double obj, long pointer);

  public void addRow(int numberElements, int[] columns, double[] element, double rowlb, double rowub){
    addRow(numberElements,columns,element,rowlb,rowub,pointer);
  }

  private native void addRow(int numberElements, int[] columns, double[] elements, double rowlb, double rowub, long pointer);

  public void setInteger(int index){
    setInteger(index,pointer);  
  }

  public void setObjSense(double s) {
    setObjSense(s,pointer);
  }

  public double getObjValue() {
    return getObjValue(pointer);
  }

  private native void reset(long pointer);

  public void reset(){
    reset(pointer);
  }

  public synchronized void delete(){
    delete(pointer);
    instances.remove(this);
  }

  private native void delete(long pointer);

  private native double getObjValue(long pointer);

  private native void setObjSense(double s, long pointer);

  private native void setInteger(int index, long pointer);

  public static void main(String[] args) {
    OsiSolverJNI solver = OsiSolverJNI.create(Implementation.CBC);
    solver.addCol(0,new int[0],new double[0],0,1.5,1.0);
    solver.addCol(0,new int[0],new double[0],0,1.5,1.0);
    solver.addRow(2,new int[]{0,1},new double[]{1.0,1.0},-INF,1.0);
    solver.setObjSense(-1);
    solver.setInteger(0);
    System.out.println("solver.getNumCols() = " + solver.getNumCols());
    System.out.println("solver.getNumRows() = " + solver.getNumRows());
    //solver.intialSolve();
    solver.branchAndBound();
    System.out.println("solver.getObjValue() = " + solver.getObjValue());
    solver.addRow(1,new int[]{1},new double[]{1.0},0,0.5);
    solver.branchAndBound();
    System.out.println("solver.getObjValue() = " + solver.getObjValue());
    double[] result = solver.getColSolution();
    System.out.println(Arrays.toString(result));

    solver.delete();
    solver = OsiSolverJNI.create(Implementation.CBC);
    //solver.reset();
    solver.addCol(0,new int[0],new double[0],0,1.5,1.0);
    solver.addCol(0,new int[0],new double[0],0,1.5,1.0);
    solver.addRow(2,new int[]{0,1},new double[]{1.0,1.0},1.0,INF);
    solver.setObjSense(-1);
    solver.setInteger(0);
    solver.branchAndBound();
    System.out.println("solver.getObjValue() = " + solver.getObjValue());
  }


}
