package thebeast.osi;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
public final class OsiSolverJNI implements OsiSolver {

  private long pointer;
  private static HashSet<OsiSolverJNI> instances = new HashSet<OsiSolverJNI>();
  private boolean exceptionThrown = false;
  private String message;
  private Implementation implementation;

  static {
    System.loadLibrary("osi");
  }

  private OsiSolverJNI(long pointer, Implementation implementation) {
    this.pointer = pointer;
    this.implementation = implementation;
  }

  public enum Implementation {
    CBC, CLP, CPLEX
  }

  public synchronized static OsiSolverJNI create(Implementation implementation) {
    OsiSolverJNI solver = new OsiSolverJNI(createImplementation(implementation.ordinal()),implementation);
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

  public void setLogLevel(int level) {
    if (implementation == Implementation.CBC)
      setCbcLogLevel(level);
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

  public void setObjCoeff(int elementIndex, double elementValue) {
    setObjCoeff(elementIndex, elementValue, pointer);
  }

  private native void setObjCoeff(int elementIndex, double elementValue, long pointer);

  public void addCol(int numberElements, int[] rows, double[] elements, double collb, double colub, double obj) {
    addCol(numberElements, rows, elements, collb, colub, obj, pointer);
  }

  public void addCols(int numcols, int[][] rows, double[][] elements, double[] collb, double[] colub, double[] obj) {
    int[] starts = new int[numcols];
    int count = rows[0].length;
    for (int i = 1; i < rows.length; ++i){
      starts[i] = rows[i-1].length;
      count += rows[i].length;
    }
    int[] flatRows = new int[count];
    int index = 0;
    for (int[] col : rows)
      for (int row : col)
        flatRows[index++] = row;

    double[] flatElements = new double[count];
    index = 0;
    for (double[] col : elements)
      for (double row : col)
        flatElements[index++] = row;

    addCols(numcols, starts, flatRows, flatElements, collb, colub, obj ,pointer);
  }

  public void addRows(int numRows, int[][] cols, double[][] elements, double[] rowlb, double[] rowub) {
    int[] ends = new int[numRows];
    int end = 0;
    for (int i = 0; i < cols.length; ++i){
      end += cols[i].length;
      ends[i] = end;
    }
    int[] flatRows = new int[end];
    int index = 0;
    for (int[] col : cols)
      for (int row : col)
        flatRows[index++] = row;

    double[] flatElements = new double[end];
    index = 0;
    for (double[] col : elements)
      for (double row : col)
        flatElements[index++] = row;

    addRows(numRows, ends, flatRows, flatElements, rowlb, rowub,pointer);
  }


  private native void addCols(int numCols, int[] columnStarts, int[] rows, double[] elements,
                              double[] collb, double[] colub, double[] obj, long ptr);

  private native void addRows(int numRows, int[] rowStarts, int[] cols, double[] elements,
                              double[] collb, double[] colub, long ptr);

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

  private native long reset(long pointer);

  public void reset(){
    pointer = reset(pointer);
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
    solver.intialSolve();
    solver.reset();
    solver.setHintParam(OsiSolver.OsiHintParam.OsiDoReducePrint, true, OsiSolver.OsiHintStrength.OsiHintTry);    
    //solver.addCol(0,new int[0],new double[0],0,1.5,1.0);
    //solver.addCol(0,new int[0],new double[0],0,1.5,1.0);
    solver.addCols(2, new int[2][0], new double[2][0], new double[]{0.0, 0.0}, new double[]{1.5,1.5}, new double[]{1.0,1.0});
    solver.addRows(2, new int[][]{new int[]{0,1}, new int[]{0}},
            new double[][]{new double[]{1.0,1.0}, new double[]{1.0}}, new double[]{-INF, 0.2}, new double[]{1.0,0.4});
    //solver.addRow(2,new int[]{0,1},new double[]{1.0,1.0},-INF,1.0);
    //solver.addRow(1,new int[]{0},new double[]{1.0},0.2,0.4);
    solver.setObjSense(-1);
    //solver.setInteger(0);
    System.out.println("solver.getNumCols() = " + solver.getNumCols());
    System.out.println("solver.getNumRows() = " + solver.getNumRows());
    //solver.intialSolve();
    solver.setCbcLogLevel(0);
    solver.branchAndBound();
    System.out.println("solver.getObjValue() = " + solver.getObjValue());
    solver.addRow(1,new int[]{1},new double[]{1.0},0,0.5);
    solver.setCbcLogLevel(0);
    solver.branchAndBound();
    System.out.println("solver.getObjValue() = " + solver.getObjValue());
    double[] result = solver.getColSolution();
    System.out.println(Arrays.toString(result));

    solver.delete();
    solver = OsiSolverJNI.create(Implementation.CBC);
    solver.setCbcLogLevel(0);    
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
