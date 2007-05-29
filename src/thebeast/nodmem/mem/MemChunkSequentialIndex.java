package thebeast.nodmem.mem;

import thebeast.util.HeapIntSorter;

/**
 * @author Sebastian Riedel
 */
public final class MemChunkSequentialIndex {

  public int[] sortedRows;
  public int highest, lowest;

  public void update(MemChunk chunk, int column) {
    int numIntCols = chunk.dim.xInt;
    int[] tmpValues = new int[chunk.size];
    sortedRows = new int[chunk.size];
    int index = column;
    boolean ordered = true;
    for (int row = 0; row < chunk.size; ++row) {
      tmpValues[row] = chunk.intData[index];
      sortedRows[row] = row;
      if (ordered && row > 0) ordered = tmpValues[row - 1] <= tmpValues[row];
      index += numIntCols;
    }
    if (!ordered) {
      HeapIntSorter sorter = new HeapIntSorter();
      sorter.sort(tmpValues, sortedRows);
    }
    //unify
    lowest = tmpValues[sortedRows[0]];
    highest = tmpValues[sortedRows[chunk.size-1]];
  }

  public int getGEQ(int value, MemChunk chunk, int col, int targetCell, int[][] rows){
    int numIntCols = chunk.dim.xInt;
    int row = (value - lowest) * (highest/(sortedRows.length - 1));
    int index = col + sortedRows[row] * numIntCols;
    if (chunk.intData[index] >= value) while (chunk.intData[index] >= value) {
      index = col + sortedRows[row] * numIntCols;
      --row;
    } else while (chunk.intData[index] < value) {
      index = col + sortedRows[row] * numIntCols;      
      ++row;
    }
    int count = 0;
    int[] dst = rows[targetCell];
    while(row < chunk.size){
      if (dst.length == count){
        int[] newDst = new int[chunk.size - row];
        rows[targetCell] = newDst;
        dst = newDst;
      }
      index = col + sortedRows[row] * numIntCols;
      dst[count++] = chunk.intData[index];
    }
    return count;
  }

}
