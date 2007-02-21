package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public class MemUpdater {

  public static void update(MemChunk chunk, MemFunction where, MemVector[] argVectors,
                            MemPointer[] argPointers, MemFunction... args) {
    MemVector pointer = new MemVector();
    MemDim dim = chunk.getDim();
    MemChunk tmp = new MemChunk(dim);
    MemChunk[] chunks = new MemChunk[]{chunk};
    int[] rows = new int[]{0};
    if (where == null)
      for (int row = 0; row < chunk.size; ++row) {
        for (int arg = 0; arg < argVectors.length; ++arg)
          MemEvaluator.evaluate(args[arg], chunks, rows, tmp, argVectors[arg]);
        for (MemPointer argPointer : argPointers) {
          switch (argPointer.type) {
            case INT:
              chunk.intData[pointer.xInt + argPointer.pointer] = tmp.intData[argPointer.pointer];
              break;
            case DOUBLE:
              chunk.doubleData[pointer.xDouble + argPointer.pointer] = tmp.doubleData[argPointer.pointer];
              break;
            case CHUNK:
              chunk.chunkData[pointer.xChunk + argPointer.pointer] = tmp.chunkData[argPointer.pointer];
              break;
          }
        }
        pointer.xInt += dim.xInt;
        pointer.xDouble += dim.xDouble;
        pointer.xChunk += dim.xChunk;
        ++rows[0];
      }
    else {
      MemChunk condition = new MemChunk(1, 1, 1, 0, 0);
      MemVector zero = new MemVector(0, 0, 0);
      for (int row = 0; row < chunk.size; ++row) {
        MemEvaluator.evaluate(where, chunks, rows, condition, zero);
        if (condition.intData[0] == 1) {
          for (int arg = 0; arg < argVectors.length; ++arg)
            MemEvaluator.evaluate(args[arg], chunks, rows, tmp, argVectors[arg]);
          System.arraycopy(tmp.intData, 0, chunk.intData, pointer.xInt, dim.xInt);
          System.arraycopy(tmp.doubleData, 0, chunk.doubleData, pointer.xDouble, dim.xDouble);
          System.arraycopy(tmp.chunkData, 0, chunk.chunkData, pointer.xChunk, dim.xChunk);
        }
        pointer.xInt += dim.xInt;
        pointer.xDouble += dim.xDouble;
        pointer.xChunk += dim.xChunk;
        ++rows[0];
      }
    }
  }

}
