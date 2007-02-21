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

}
