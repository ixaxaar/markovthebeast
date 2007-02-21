package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public class MemSummarizer {

  public enum Spec {
    INT_COUNT, INT_SUM, DOUBLE_COUNT, DOUBLE_SUM
  }

  public static void summarize(MemChunk src,
                               MemFunction f,
                               MemChunk dst) {
    MemColumnSelector key2original = f.key2original;
    MemColumnSelector key2result = f.key2result;
    //MemColumnSelector tmp2original = f.tmp2original;
    MemColumnSelector tmp2result = f.tmp2result;
    Spec[] intSpecs = f.intSpecs;
    Spec[] doubleSpecs = f.doubleSpecs;
    MemChunk tmp = new MemChunk(1,1,tmp2result.getDim());
    MemChunk wrappedTmp = new MemChunk(1, new int[0], new double[0], new MemChunk[]{tmp});
    MemChunkIndex index = new MemChunkIndex(src.getSize(), key2original.getDim());
    MemVector srcPointer = new MemVector();
    MemVector dstPointer;
    MemDim srcDim = src.getDim();
    MemDim dstDim = dst.getDim();
    MemChunk[] chunks = new MemChunk[]{src};
    int[] rows = new int[1];

    int dstRow = 0;
    dst.size = 0;
    int currentRow = 0;
    for (int row = 0; row < src.size; ++row) {
      rows[0] = row;
      int old = index.put(src, srcPointer, key2original, dstRow, false);
      if (old == -1) {
        dstPointer = new MemVector(dstRow, dstDim);
        if (dstRow >= dst.capacity)
          dst.increaseCapacity(src.size - dst.capacity);
        //copy keys
        for (int i = 0; i < key2original.intCols.length; ++i)
          dst.intData[dstPointer.xInt + key2result.intCols[i]] =
                  src.intData[srcPointer.xInt + key2original.intCols[i]];
        for (int i = 0; i < key2original.doubleCols.length; ++i)
          dst.doubleData[dstPointer.xDouble + key2result.doubleCols[i]] =
                  src.doubleData[srcPointer.xDouble + key2original.doubleCols[i]];
        for (int i = 0; i < key2original.chunkCols.length; ++i)
          dst.chunkData[dstPointer.xChunk + key2result.chunkCols[i]] =
                  src.chunkData[srcPointer.xChunk + key2original.chunkCols[i]];

        currentRow = dstRow++;
        ++dst.size;
      } else {
        currentRow = old;
      }
      int dstInt = currentRow * dst.numIntCols;
      int dstDouble = currentRow * dst.numDoubleCols;

      //evaluate tmp function
      MemEvaluator.evaluate(f.tmpFunction, chunks, rows, wrappedTmp, MemVector.ZERO);

      //evaluate specs
      for (int i = 0; i < intSpecs.length; ++i){
        switch (intSpecs[i]){
          case INT_SUM:
            dst.intData[dstInt + tmp2result.intCols[i]] += tmp.intData[i];
            break;
          case INT_COUNT:
            ++dst.intData[dstInt + tmp2result.intCols[i]];
            break;
        }
      }
      for (int i = 0; i < doubleSpecs.length; ++i){
        switch (doubleSpecs[i]){
          case DOUBLE_SUM:
            dst.doubleData[dstDouble + tmp2result.doubleCols[i]] += tmp.doubleData[i];
            break;
          case DOUBLE_COUNT:
            ++dst.doubleData[dstDouble + tmp2result.doubleCols[i]];
            break;
        }
      }
      srcPointer.add(srcDim);
    }
  }

}
