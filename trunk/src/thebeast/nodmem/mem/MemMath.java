package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public class MemMath {

  public static double sparseAdd(MemChunk array, MemChunk sparse,
                                 double scale, int indexColumn, int valueColumn){
    double result = 0;
    int indexPtr = indexColumn;
    int valuePtr = valueColumn;
    for (int row = 0; row < sparse.size;++row){
      array.doubleData[sparse.intData[indexPtr]] += scale * sparse.doubleData[valuePtr];
      indexPtr += sparse.numIntCols;
      valuePtr += sparse.numDoubleCols;
    }
    return result;
  }

  public static void add(MemChunk dst, MemChunk arg, double scale){
    for (int row = 0; row < dst.size;++row){
      dst.doubleData[row] += scale * arg.doubleData[row];
    }
  }


  public static void scale(MemChunk dst, double scale){
    for (int row = 0; row < dst.size;++row){
      dst.doubleData[row] *= scale;
    }
  }


  public static double indexSum(MemChunk array, MemChunk indexRelation, int indexColumn){
    double result = 0;
    int relPointer = indexColumn;
    for (int row = 0; row < indexRelation.size;++row){
      result += array.doubleData[indexRelation.intData[relPointer]];
      relPointer += indexRelation.numIntCols;
    }
    return result;
  }

  public static double indexSum(MemChunk array, MemChunk indexRelation, int indexColumn, int scaleColumn){
    double result = 0;
    int indexPtr = indexColumn;
    int scalePtr = scaleColumn;
    for (int row = 0; row < indexRelation.size;++row){
      result += array.doubleData[indexRelation.intData[indexPtr]] * 
              indexRelation.doubleData[scalePtr];
      indexPtr += indexRelation.numIntCols;
      scalePtr += indexRelation.numDoubleCols;
    }
    return result;
  }

  public static void collect(MemChunk grouped, int groupedAttribute, MemChunk dstSparseVector, MemFunction f){
    MemChunkIndex chunkIndex = f.index;
    if (chunkIndex.getCapacity() == 0){
      chunkIndex.increaseCapacity(grouped.size);
    }
    chunkIndex.clear();
    int chunkPtr = groupedAttribute;
    MemVector indexPtr = new MemVector();
    MemColumnSelector cols = new MemColumnSelector(1,0,0);
    int dstSize = 0;
    if (dstSparseVector.capacity == 0)
      dstSparseVector.increaseCapacity(grouped.size);
    for (int row = 0; row < grouped.size; ++row){
      MemChunk indices = grouped.chunkData[chunkPtr];
      indexPtr.set(0,0,0);
      for (int i = 0; i < indices.size; ++i){
        int index = indices.intData[i];
        int old = chunkIndex.put(indices,indexPtr,cols,dstSize, false);
        if (old == -1){
          if (dstSize >= dstSparseVector.capacity){
            dstSparseVector.increaseCapacity(dstSize);
          }
          dstSparseVector.intData[dstSize] = index;
          dstSparseVector.doubleData[dstSize] = 1.0;
          ++dstSparseVector.size;
          if (chunkIndex.getLoadFactor() > 3){
            chunkIndex.increaseCapacity(chunkIndex.getCapacity());
          }
          ++dstSize;
        } else {
          dstSparseVector.doubleData[old] += 1.0;
        }
         
        ++indexPtr.xInt;
      }
      chunkPtr+=grouped.numChunkCols;
    }
    dstSparseVector.size = dstSize; 
  }

}
