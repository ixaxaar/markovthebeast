package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public final class MemSearchAction {

  public enum Type {
    MULTI_INDEX, ALL, LT_SEQ, GT_SEQ, BETWEEN_SEQ, WRITE, INSERT, VALIDATE_WRITE, GEQ_SEQ
  }

  final Type type;
  final MemFunction[] functions;
  MemChunk args;
  MemColumnSelector cols;
  int indexNr;
  MemChunkSequentialIndex sequentialIndex;
  int col;

  public MemSearchAction(Type type, MemFunction ... functions) {
    this.type = type;
    this.functions = functions;
  }

  public MemSearchAction(Type type, int indexNr, MemColumnSelector cols, MemFunction ... functions) {
    this.type = type;
    this.indexNr = indexNr;
    this.functions = functions;
    this.cols = cols;
    MemDim dim = MemDim.create(cols.intCols.length, cols.doubleCols.length, cols.chunkCols.length);
    args = new MemChunk(1,1, dim);
  }

  public MemSearchAction(Type type, MemChunkSequentialIndex index,
                         int col,MemFunction function) {
    this.type = type;
    this.sequentialIndex = index;
    this.col = col;
    this.functions = new MemFunction[]{function};
    args = new MemChunk(1,1,MemDim.INT_DIM);
  }

  public int byteSize(){
    int size = 0;
    for (MemFunction f : functions)
      size += f.bytesize();
    if (args != null) size += args.byteSize();
    return size;
  }


}
