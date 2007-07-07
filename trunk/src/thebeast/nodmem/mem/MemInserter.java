package thebeast.nodmem.mem;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 18-Jan-2007 Time: 15:03:24
 */
public class MemInserter {

  public static void insert(MemChunk src, MemChunk dst) {
    //assuming src is unique
    MemVector pointer = new MemVector();
    dst.buildRowIndex();
    MemChunkIndex index = dst.rowIndex;
    //System.out.println("index.getLoadFactor() = " + index.getLoadFactor());;
    MemVector dstPointer = new MemVector(dst.size, dst.getDim());
    for (int row = 0; row < src.size; ++row) {
      int old = index.get(src, pointer, src.dim.allCols);
      if (old == -1) {
        if (dst.size == dst.capacity)
          //dst.increaseCapacity(dst.size + src.size);
          dst.increaseCapacity(dst.size + src.size);
        if (src.intData != null  && src.dim.xInt > 0)
          System.arraycopy(src.intData, pointer.xInt, dst.intData, dstPointer.xInt, dst.dim.xInt);
        if (src.doubleData != null && src.dim.xDouble > 0)
          System.arraycopy(src.doubleData, pointer.xDouble, dst.doubleData, dstPointer.xDouble, dst.dim.xDouble);
        if (src.chunkData != null  && src.dim.xChunk > 0)
          MemChunk.copyChunks(src.chunkData, pointer.xChunk, dst.chunkData, dstPointer.xChunk, dst.dim.xChunk);
        ++dst.size;
        dstPointer.xInt += dst.dim.xInt;
        dstPointer.xDouble += dst.dim.xDouble;
        dstPointer.xChunk += dst.dim.xChunk;
      }
      pointer.xInt += src.dim.xInt;
      pointer.xDouble += src.dim.xDouble;
      pointer.xChunk += src.dim.xChunk;
    }
  }

  public static void append(MemChunk src, MemChunk dst) {
    if (src.size + dst.size > dst.capacity) {
      dst.increaseCapacity(src.size + dst.size - dst.capacity);
    }
    if (src.intData != null && src.dim.xInt > 0)
      System.arraycopy(src.intData, 0, dst.intData, dst.size * dst.dim.xInt, src.size * src.dim.xInt);
    if (src.doubleData != null && src.dim.xDouble > 0)
      System.arraycopy(src.doubleData, 0, dst.doubleData, dst.size * dst.dim.xDouble, src.size * src.dim.xDouble);
    if (src.chunkData != null && src.dim.xChunk > 0)
      MemChunk.copyChunks(src.chunkData, 0, dst.chunkData, dst.size * dst.dim.xChunk, src.size * src.dim.xChunk);
    dst.size += src.size;
  }


}
