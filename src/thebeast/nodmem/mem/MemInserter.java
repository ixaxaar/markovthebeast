package thebeast.nodmem.mem;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 18-Jan-2007 Time: 15:03:24
 */
public class MemInserter {

  public static void insert(MemChunk src, MemChunk dst) {
    //assuming src is unique
    MemVector pointer = new MemVector();
    MemChunkIndex index = dst.rowIndex;
    dst.buildRowIndex();
    MemVector dstPointer = new MemVector(dst.size, dst.getDim());
    for (int row = 0; row < src.size; ++row) {
      int old = index.get(src, pointer, src.allCols);
      if (old == -1) {
        if (dst.size == dst.capacity)
          dst.increaseCapacity(src.size);
        System.arraycopy(src.intData, pointer.xInt, dst.intData, dstPointer.xInt, dst.numIntCols);
        System.arraycopy(src.doubleData, pointer.xDouble, dst.doubleData, dstPointer.xDouble, dst.numDoubleCols);
        System.arraycopy(src.chunkData, pointer.xChunk, dst.chunkData, dstPointer.xChunk, dst.numChunkCols);
        ++dst.size;
        dstPointer.xInt += dst.numIntCols;
        dstPointer.xDouble += dst.numDoubleCols;
        dstPointer.xChunk += dst.numChunkCols;
      }
      pointer.xInt += src.numIntCols;
      pointer.xDouble += src.numDoubleCols;
      pointer.xChunk += src.numChunkCols;
    }
  }

  public static void append(MemChunk src, MemChunk dst) {
    if (src.size + dst.size > dst.capacity) {
      dst.increaseCapacity(src.size + dst.size - dst.capacity);
    }
    System.arraycopy(src.intData, 0, dst.intData, dst.size * dst.numIntCols, src.size * src.numIntCols);
    System.arraycopy(src.doubleData, 0, dst.doubleData, dst.size * dst.numDoubleCols, src.size * src.numDoubleCols);
    System.arraycopy(src.chunkData, 0, dst.chunkData, dst.size * dst.numChunkCols, src.size * src.numChunkCols);
    dst.size += src.size;
  }


}
