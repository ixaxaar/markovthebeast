package thebeast.nodmem.mem;

import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class MemChunkInformation {

  public final int numIntCols;
  public final int numDoubleCols;
  public final int numChunkCols;

  public final MemColumnSelector allCols;
  private static HashMap<MemChunkInformation, MemChunkInformation>
          created = new HashMap<MemChunkInformation, MemChunkInformation>();


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemChunkInformation that = (MemChunkInformation) o;

    return numChunkCols == that.numChunkCols && numDoubleCols == that.numDoubleCols && numIntCols == that.numIntCols;

  }

  public int hashCode() {
    int result;
    result = numIntCols;
    result = 31 * result + numDoubleCols;
    result = 31 * result + numChunkCols;
    return result;
  }

  private MemChunkInformation(int numIntCols, int numDoubleCols, int numChunkCols, MemColumnSelector allCols) {
    this.numIntCols = numIntCols;
    this.numDoubleCols = numDoubleCols;
    this.numChunkCols = numChunkCols;
    this.allCols = allCols;
  }

  public MemChunkInformation create(int numIntCols, int numDoubleCols, int numChunkCols) {
    MemChunkInformation info = new MemChunkInformation(numIntCols, numDoubleCols,
            numChunkCols, new MemColumnSelector(numIntCols, numDoubleCols, numChunkCols));
    MemChunkInformation old = created.get(info);
    if (old != null) return old;
    created.put(info, info);
    return info;
  }


}
