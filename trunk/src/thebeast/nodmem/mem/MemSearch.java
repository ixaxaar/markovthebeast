package thebeast.nodmem.mem;

import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public final class MemSearch {

  private static final int CAPACITY_INCREMENTS = 200;

  public static void search(MemSearchPlan plan, MemChunk[] chunks, MemChunk variables, MemChunk dst, int dstRow) {
    final int chunkCount = chunks.length;
    final MemChunkMultiIndex[] indices = new MemChunkMultiIndex[chunkCount];
//    for (int i = 0; i < chunks.length; ++i)
//      indices[i] = chunks[i].indices[plan.indicesToUse[i]];

    int[] currentPointers = plan.currentPointers;
    int[] currentSizes = plan.currentSizes;
    int[] currentRows = plan.currentRows;

    int[][] currentSpaces = plan.currentSpaces;

    boolean[] filled = plan.filled;
    for (int i = 0; i < filled.length; ++i) filled[i] = false;
    boolean[] incremental = plan.incremental;
    for (int i = 0; i < incremental.length; ++i) incremental[i] = false;
//    int[] currentPointers = new int[chunkCount];
//    int[] currentSizes = new int[chunkCount];
//    int[] currentRows = new int[chunkCount];
//
//    int[][] currentSpaces = new int[chunkCount][];
//
//    boolean[] filled = new boolean[chunkCount];
//    boolean[] incremental = new boolean[chunkCount];
    MemVector zero = new MemVector();

    MemVector dstPointer = new MemVector(dstRow, new MemDim(dst.numIntCols, dst.numDoubleCols, dst.numChunkCols));
    dst.size = dstRow;

    int currentChunk = 0;
    MemChunk valid = plan.valid;
//
//    MemChunk valid = new MemChunk(1, 1, new MemDim(1, 0, 0));

    main:
    do {
      if (currentChunk == chunkCount || !filled[currentChunk]) {
        MemSearchAction action = plan.actions[currentChunk];
        MemChunk actionArgs = action.args;

        switch (action.type) {
          case ALL:
            incremental[currentChunk] = true;
            currentRows[currentChunk] = 0;
            currentSizes[currentChunk] = chunks[currentChunk].size;
            currentPointers[currentChunk] = 0;
            break;
          case MULTI_INDEX:
            MemEvaluator.evaluate(action.functions[0], chunks, currentRows, action.args, zero);
            currentSizes[currentChunk] =
                    chunks[currentChunk].indices[action.indexNr].get(actionArgs, zero, action.cols, currentChunk, currentSpaces);
            currentPointers[currentChunk] = 0;
            if (currentSizes[currentChunk] > 0)
              currentRows[currentChunk] = currentSpaces[currentChunk][0];
            break;
          case GEQ_SEQ:
            MemEvaluator.evaluate(action.functions[0], chunks, currentRows, action.args, zero);
            int value = actionArgs.intData[0];
            MemChunkSequentialIndex index = action.sequentialIndex;
            currentSizes[currentChunk] = index.getGEQ(value, chunks[currentChunk], action.col, currentChunk, currentSpaces);
            currentPointers[currentChunk] = 0;
            if (currentSizes[currentChunk] > 0)
              currentRows[currentChunk] = currentSpaces[currentChunk][0];
            break;
          case VALIDATE_WRITE:
            //printRows(currentPointers, currentSpaces, currentRows, System.out);
            MemEvaluator.evaluate(action.functions[0], chunks, currentRows, valid, zero);
            if (valid.intData[0] == 1) {
              //System.out.println("Written");
              if (dst.capacity == dst.size)
                dst.increaseCapacity(CAPACITY_INCREMENTS);
              MemEvaluator.evaluate(action.functions[1], chunks, currentRows, dst, dstPointer);
              //++dst.size;
              dstPointer.xInt += dst.numIntCols;
              dstPointer.xDouble += dst.numDoubleCols;
              dstPointer.xChunk += dst.numChunkCols;
            }
            --currentChunk;
            continue main;
          case WRITE:
            //printRows(currentPointers, currentSpaces, currentRows, System.out);
            if (dst.capacity == dst.size)
              dst.increaseCapacity(CAPACITY_INCREMENTS);
            int oldSize = dst.size;
            MemEvaluator.evaluate(action.functions[0], chunks, currentRows, dst, dstPointer);
            int diffSize = dst.size - oldSize;
            //++dst.size;
            dstPointer.xInt += diffSize * dst.numIntCols;
            dstPointer.xDouble += diffSize * dst.numDoubleCols;
            dstPointer.xChunk += diffSize * dst.numChunkCols;
            --currentChunk;
            continue main;
        }

        if (currentPointers[currentChunk] >= currentSizes[currentChunk]) {
          --currentChunk;
        } else {
          filled[currentChunk] = true;
          ++currentChunk;
        }
      } else if (currentPointers[currentChunk] == currentSizes[currentChunk] - 1) {
        filled[currentChunk] = false;
        --currentChunk;
      } else {
        if (incremental[currentChunk]) {
          currentPointers[currentChunk] = ++currentRows[currentChunk];
        } else {
          currentRows[currentChunk] = currentSpaces[currentChunk][++currentPointers[currentChunk]];
        }
        ++currentChunk;
      }


    } while (currentChunk >= 0);

    dst.unify();

  }

  private static void printRows(int[] currentPointers, int[][] currentSpaces, int[] currentRows, PrintStream out) {
    for (int i = 0; i < currentPointers.length; ++i) {
      int[] rows = currentSpaces[i];
      out.printf("%-4d", (rows != null ? rows[currentPointers[i]] : currentRows[i]));
    }
    out.println();
  }

  static String currentRows(int[] currentPointers, int[][] currentSpaces, int[] currentRows, MemChunk[] chunks) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < currentPointers.length; ++i) {
      if (i > 0) result.append("| ");
      int[] rows = currentSpaces[i];
      int row = rows != null ? rows[currentPointers[i]] : currentRows[i];
      for (int k = 0; k < chunks[i].numIntCols; ++k)
        result.append(chunks[i].intData[row * chunks[i].numIntCols + k]).append(" ");
    }
    return result.toString();
  }


}
