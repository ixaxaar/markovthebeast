package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public class MemSearchPlan {

  final MemSearchAction[] actions;
  MemDim resultDim;
  final MemChunk valid = new MemChunk(1,1,MemDim.INT_DIM);
  int[] currentPointers;
  int[] currentSizes;
  int[] currentRows;

  int[][] currentSpaces;

  boolean[] filled;
  boolean[] incremental;



  public MemSearchPlan(MemDim resultDim, MemSearchAction ... actions) {
    this.resultDim = resultDim;
    this.actions = actions;
    int chunkCount = actions.length - 1;
    currentPointers = new int[chunkCount];
    currentSizes = new int[chunkCount];
    currentRows = new int[chunkCount];
    currentSpaces = new int[chunkCount][];
    filled = new boolean[chunkCount];
    incremental = new boolean[chunkCount];
  }

  public MemSearchPlan(MemSearchAction... actions) {
    this.actions = actions;
    resultDim = MemDim.EMPTY;
  }

  



}
