package thebeast.osi;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
public final class OsiSolverJNI implements OsiSolver {

  private int pointer;
  private static HashSet<OsiSolverJNI> instances = new HashSet<OsiSolverJNI>();

  static {
    System.loadLibrary("osi");
  }

  private OsiSolverJNI(int pointer) {
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

  private static native int createImplementation(int ordinal);


  public boolean setHintParam(OsiHintParam key, boolean yesNo, OsiHintStrength strength) {
    return setHintParam(key.ordinal(), yesNo,strength.ordinal(),pointer);
  }

  private native static boolean setHintParam(int key, boolean yesNo, int strength, int ptr);


  public void intialSolve() {
    initialSolve(pointer);
  }

  private native void initialSolve(int pointer);

  public void resolve() {
    resolve(pointer);
  }

  private native void resolve(int pointer);

  public void branchAndBound() {
    branchAndBound(pointer);
  }

  private native void branchAndBound(int pointer);

  public double[] getColSolution() {
    return getColSolution(pointer);
  }

  public int getNumCols() {
    return getNumCols(pointer);
  }

  private native int getNumCols(int pointer);

  public int getNumRows(){
    return getNumRows(pointer);
  }

  public void setCbcLogLevel(int level) {
    setCbcLogLevel(level,pointer);
  }

  private static native void setCbcLogLevel(int level, int ptr);

  private native int getNumRows(int pointer);

  private native double[] getColSolution(int pointer);

  public void setColLower(int elementIndex, double elementValue) {
    setColLower(elementIndex, elementValue, pointer);
  }

  private native void setColLower(int elementIndex, double elementValue, int pointer);

  public void setColUpper(int elementIndex, double elementValue) {
    setColUpper(elementIndex, elementValue, pointer);
  }

  private native void setColUpper(int elementIndex, double elementValue, int pointer);

  public void setObjCooeff(int elementIndex, double elementValue) {
    setObjCooeff(elementIndex, elementValue, pointer);
  }

  private native void setObjCooeff(int elementIndex, double elementValue, int pointer);

  public void addCol(int numberElements, int[] rows, double[] elements, double collb, double colub, double obj) {
    addCol(numberElements, rows, elements, collb, colub, obj, pointer);
  }

  private native void addCol(int numberElements, int[] rows, double[] elements, double collb, double colub, double obj, int pointer);

  public void addRow(int numberElements, int[] columns, double[] element, double rowlb, double rowub){
    addRow(numberElements,columns,element,rowlb,rowub,pointer);
  }

  private native void addRow(int numberElements, int[] columns, double[] elements, double rowlb, double rowub, int pointer);

  public void setInteger(int index){
    setInteger(index,pointer);  
  }

  public void setObjSense(double s) {
    setObjSense(s,pointer);
  }

  public double getObjValue() {
    return getObjValue(pointer);
  }

  private native void reset(int pointer);

  public void reset(){
    reset(pointer);
  }

  public synchronized void delete(){
    delete(pointer);
    instances.remove(this);
  }

  private native void delete(int pointer);

  private native double getObjValue(int pointer);

  private native void setObjSense(double s, int pointer);

  private native void setInteger(int index, int pointer);

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
