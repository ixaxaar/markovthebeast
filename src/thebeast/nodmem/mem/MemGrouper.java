package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public class MemGrouper {

  public static void group(MemChunk src,
                           MemColumnSelector keyCols,
                           MemColumnSelector dstCols,
                           MemColumnSelector groupedCols,
                           int dstGroupCol,
                           MemChunk dst){
    MemChunkIndex index = new MemChunkIndex(src.getSize(), keyCols.getDim());
    MemVector srcPointer = new MemVector();
    MemVector dstPointer;
    MemDim srcDim = src.getDim();
    MemDim dstDim = dst.getDim();
    MemChunk groupCol;

    int dstRow = 0;
    dst.size = 0;
    for (int row = 0; row < src.size; ++row){
      int old = index.put(src,srcPointer,keyCols, dstRow, false);
      if (old == -1){
        dstPointer = new MemVector(dstRow,dstDim);
        if (dstRow >= dst.capacity)
          dst.increaseCapacity(src.size - dst.capacity);
        for (int i = 0; i < keyCols.intCols.length;++i)
          dst.intData[dstPointer.xInt + dstCols.intCols[i]] =
                  src.intData[srcPointer.xInt + keyCols.intCols[i]];
        for (int i = 0; i < keyCols.doubleCols.length;++i)
          dst.doubleData[dstPointer.xDouble + dstCols.doubleCols[i]] =
                  src.doubleData[srcPointer.xDouble + keyCols.doubleCols[i]];
        for (int i = 0; i < keyCols.chunkCols.length;++i)
          dst.chunkData[dstPointer.xChunk + dstCols.chunkCols[i]] =
                  src.chunkData[srcPointer.xChunk + keyCols.chunkCols[i]];
        groupCol = dst.chunkData[dstPointer.xChunk + dstGroupCol];
        if (groupCol == null){
          groupCol = new MemChunk(0,1,groupedCols.getDim());
          dst.chunkData[dstPointer.xChunk + dstGroupCol] = groupCol;
        }
        groupCol.size = 0;
        dstRow++;
        ++dst.size;
      } else {
        groupCol = dst.chunkData[old * dstDim.xChunk + dstGroupCol];
      }
      if (groupCol.capacity <= groupCol.size){
        groupCol.increaseCapacity(10);
      }
      int dstInt = groupCol.size * groupCol.dim.xInt;
      int dstDouble = groupCol.size * groupCol.dim.xDouble;
      int dstChunk = groupCol.size * groupCol.dim.xChunk;

      for (int xInt : groupedCols.intCols)
        groupCol.intData[dstInt++] = src.intData[srcPointer.xInt + xInt];
      for (int xDouble : groupedCols.doubleCols)
        groupCol.doubleData[dstDouble++] = src.doubleData[srcPointer.xDouble + xDouble];
      for (int xChunk : groupedCols.chunkCols)
        groupCol.chunkData[dstChunk++] = src.chunkData[srcPointer.xChunk + xChunk];
      groupCol.size++;

      srcPointer.add(srcDim);
    }
  }

}
