package thebeast.nodmem.mem;

import thebeast.util.CycleFinder;
import thebeast.util.Pair;

import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class MemEvaluator {

  public static void evaluate(MemFunction function,
                              MemChunk[] chunks,
                              int[] rows,
                              MemChunk dst,
                              MemVector dstPointer) {

    MemChunk[] returnStack = function.returnStack;
    MemChunk[] argStack = function.argStack;
    MemFunction[] argFunctions = function.argFunctionStack;
    boolean[] processed = function.processedStack;
    MemVector[] argPointersVec = function.argPointersVecStac;
//    MemChunk[] returnStack = new MemChunk[INIT_STACK_SIZE];
//    MemChunk[] argStack = new MemChunk[INIT_STACK_SIZE];
//    MemFunction[] argFunctions = new MemFunction[INIT_STACK_SIZE];
//    boolean[] processed = new boolean[INIT_STACK_SIZE];
//    MemVector[] argPointersVec = new MemVector[INIT_STACK_SIZE];

    returnStack[0] = dst;
    argStack[0] = function.argHolder;
    argFunctions[0] = function;
    argPointersVec[0] = dstPointer;
    processed[0] = false;

    int stackPointer = 0;
    while (stackPointer >= 0) {
      MemFunction f = argFunctions[stackPointer];
      if (f.arguments.length > 0 && !processed[stackPointer]) {
        for (int i = 0; i < f.arguments.length; ++i) {
          int stackPointerOfArg = stackPointer + 1 + i;
          argPointersVec[stackPointerOfArg] = f.argPointersVec[i];
          argFunctions[stackPointerOfArg] = f.arguments[i];
          returnStack[stackPointerOfArg] = f.argHolder;
          processed[stackPointerOfArg] = false;
        }
        processed[stackPointer] = true;
        stackPointer += f.arguments.length;
        continue;
      }

      MemChunk argChunk = f.argHolder;
      MemChunk returnChunk = returnStack[stackPointer];
      MemVector argPointerVec = argPointersVec[stackPointer];

      switch (f.type) {
        case VOID:
          break;
        case COPY:
          copy(argChunk, returnChunk, argPointerVec);
          break;
        case INT_ADD:
          int_add(argChunk, returnChunk, argPointerVec);
          break;
        case INT_MINUS:
          int_minus(argChunk, returnChunk, argPointerVec);
          break;
        case INT_CONSTANT:
          int_constant(f, returnChunk, argPointerVec);
          break;
        case INT_VARIABLE:
          int_variable(f, returnChunk, argPointerVec);
          break;
        case INT_POST_INC:
          int_post_inc(f, returnChunk, argPointerVec);
          break;
        case INT_EQUAL:
          int_equal(argChunk, returnChunk, argPointerVec);
          break;
        case INT_NOTEQUAL:
          int_notequal(argChunk, returnChunk, argPointerVec);
          break;
        case INT_LEQ:
          int_leq(argChunk, returnChunk, argPointerVec);
          break;
        case INT_GEQ:
          int_geq(argChunk, returnChunk, argPointerVec);
          break;
        case INT_LESSTHAN:
          int_lessthan(argChunk, returnChunk, argPointerVec);
          break;
        case INT_GREATERTHAN:
          int_greaterthan(argChunk, returnChunk, argPointerVec);
          break;
        case INT_ATTRIBUTE:
          int_attribute(chunks, f, rows, returnChunk, argPointerVec);
          MemChunk chunk;
          break;
        case INT_EXTRACT:
          int_extract(argChunk, f, returnChunk, argPointerVec);
          break;
        case INT_BINS:
          int_bins(argChunk, f, returnChunk, argPointerVec);
          break;
        case RELATION_MINUS:
          relation_minus(argChunk, returnChunk, argPointerVec);
          break;
        case UNION:
          union(argChunk, returnChunk, argPointerVec, f);
          break;
        case COUNT:
          count(argChunk, returnChunk, argPointerVec);
          break;
        case DOUBLE_EQUAL:
          double_equal(argChunk, returnChunk, argPointerVec);
          break;
        case DOUBLE_NOTEQUAL:
          double_notequal(argChunk, returnChunk, argPointerVec);
          break;
        case DOUBLE_ADD:
          double_add(argChunk, returnChunk, argPointerVec);
          break;
        case DOUBLE_MINUS:
          double_minus(argChunk, returnChunk, argPointerVec);
          break;
        case DOUBLE_TIMES:
          double_times(argChunk, returnChunk, argPointerVec);
          break;
        case DOUBLE_CAST:
          double_cast(argChunk, returnChunk, argPointerVec);
          break;
        case DOUBLE_ATTRIBUTE:
          double_attribute(chunks, f, rows, returnChunk, argPointerVec);
          break;
        case DOUBLE_CONSTANT:
          double_constant(f, returnChunk, argPointerVec);
          break;
        case DOUBLE_VARIABLE:
          double_variable(f, returnChunk, argPointerVec);
          break;
        case DOUBLE_EXTRACT:
          double_extract(argChunk, f, returnChunk, argPointerVec);
          break;
        case DOUBLE_GT:
          double_gt(argChunk, returnChunk, argPointerVec);
          break;
        case DOUBLE_LT:
          double_lt(argChunk, returnChunk, argPointerVec);
          break;
        case DOUBLE_LEQ:
          double_leq(argChunk, returnChunk, argPointerVec);
          break;
        case DOUBLE_GEQ:
          double_geq(argChunk, returnChunk, argPointerVec);
          break;
        case CHUNK_CONSTANT:
          chunk_constant(f, returnChunk, argPointerVec);
          break;
        case CHUNK_EQUAL:
          chunk_equal(argChunk, returnChunk);
          break;
        case CHUNK_NOTEQUAL:
          chunk_notequal(argChunk, returnChunk);
          break;
        case CHUNK_VARIABLE:
          chunk_variable(f, returnChunk, argPointerVec);
          break;
        case CHUNK_ATTRIBUTE:
          chunk_attribute(chunks, f, rows, returnChunk, argPointerVec);
          break;
        case NOT:
          not(argChunk, returnChunk, argPointerVec);
          break;
        case AND:
          and(argChunk, returnChunk, argPointerVec);
          break;
        case OR:
          or(argChunk, returnChunk, argPointerVec);
          break;
        case QUERY:
          query(f, chunks, rows, returnChunk, argPointerVec);
          break;
        case TUPLE_COPY:
          tuple_copy(argChunk, returnChunk, argPointerVec);
          break;
        case RELATION_COPY:
          relation_copy(argChunk, argPointerVec, returnChunk);
          break;
        case TUPLE_SELECTOR:
          tuple_selector(returnChunk, argPointerVec, f, argChunk);
          break;
        case TUPLE_FROM:
          tuple_from(returnChunk, argPointerVec, f, argChunk);
          break;
        case CONTAINS:
          contains(argChunk, returnChunk);
          break;
        case RELATION_SELECTOR:
          relation_selector(f, returnChunk, argPointerVec);
          break;
        case ARRAY_ACCESS_ZERO:
          array_access_zero(f, returnChunk, argPointerVec);
          break;
        case ARRAY_CREATOR:
          array_creator(f, returnChunk, argPointerVec);
          break;
        case GROUP:
          group(returnChunk, argPointerVec, f);
          break;
        case INDEXED_SUM:
          indexed_sum(f, argChunk, returnChunk, argPointerVec);
          break;
        case OPERATOR_INV:
          operator_inv(f, chunks, rows, returnChunk, argPointerVec);
          break;
        case GET:
          get(f, chunks, rows, argChunk, returnChunk, argPointerVec);
          break;
        case CYCLES:
          cycles(f, argChunk, returnChunk);
          break;
        case SUMMARIZE:
          summarize(argChunk, returnChunk, argPointerVec, f);
          break;
        case SPARSE_ADD:
          sparseAdd(argChunk, returnChunk, argPointerVec, f);
          break;
        case INDEX_COLLECTOR:
          MemMath.collect(argChunk.chunkData[0], f.groupAtt, returnChunk.chunkData[argPointerVec.xChunk], f);
          break;
      }
      --stackPointer;
    }
  }

  private static void sparseAdd(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec, MemFunction f) {
    MemChunk memChunk;
    MemChunk lhs = argChunk.chunkData[0];
    double scale = argChunk.doubleData[0];
    MemChunk rhs = argChunk.chunkData[1];
    memChunk = returnChunk.chunkData[argPointerVec.xChunk];
    if (memChunk == null) {
      memChunk = new MemChunk(0, 0, lhs.numIntCols, lhs.numDoubleCols, lhs.numChunkCols);
      returnChunk.chunkData[argPointerVec.xChunk] = memChunk;
    }
    if (memChunk == lhs || memChunk == rhs) {
      MemChunk tmp = new MemChunk(0, 0, lhs.numIntCols, lhs.numDoubleCols, lhs.numChunkCols);
      sparseAdd(f, lhs, scale, rhs, tmp);
      memChunk.copyFrom(tmp);
    } else {
      sparseAdd(f, lhs, scale, rhs, memChunk);
    }
  }

  private static void summarize(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec, MemFunction f) {
    argChunk = argChunk.chunkData[0];
    MemChunk memChunk = returnChunk.chunkData[argPointerVec.xChunk];
    if (memChunk == null) {
      memChunk = new MemChunk(0, 0, f.returnDim);
      returnChunk.chunkData[argPointerVec.xChunk] = memChunk;
    }
    MemSummarizer.summarize(argChunk, f, memChunk);
  }

  private static void operator_inv(MemFunction f, MemChunk[] chunks, int[] rows, MemChunk returnChunk, MemVector argPointerVec) {
    for (int i = 0; i < f.opArgFunctions.length; ++i){
      //f.opArgFunctions[i].clear();
      evaluate(f.opArgFunctions[i], chunks, rows, f.opArgs[i], f.opArgVecs[i]);
    }
    //f.opResultFunction.clear();
    evaluate(f.opResultFunction, null, null, returnChunk, argPointerVec);
  }

  private static void indexed_sum(MemFunction f, MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    if (f.scaleAttribute == -1) returnChunk.doubleData[argPointerVec.xDouble] =
            MemMath.indexSum(argChunk.chunkData[0], argChunk.chunkData[1], f.indexAttribute);
    else returnChunk.doubleData[argPointerVec.xDouble] =
            MemMath.indexSum(argChunk.chunkData[0], argChunk.chunkData[1], f.indexAttribute, f.scaleAttribute);
  }

  private static void group(MemChunk returnChunk, MemVector argPointerVec, MemFunction f) {
    MemChunk result;
    result = returnChunk.chunkData[argPointerVec.xChunk];
    MemGrouper.group(f.argHolder.chunkData[0], f.keyCols, f.dstCols, f.groupCols, f.dstGroupCol, result);
  }

  private static void array_creator(MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    int neededSize;
    MemChunk result;
    neededSize = f.argHolder.size;
    if (neededSize == 0) return;
    result = returnChunk.chunkData[argPointerVec.xChunk];
    if (result == null) {
      result = new MemChunk(neededSize, neededSize,
              f.argHolder.numIntCols, f.argHolder.numDoubleCols, f.argHolder.numChunkCols);
      returnChunk.chunkData[argPointerVec.xChunk] = result;
    } else if (result.capacity < neededSize) {
      result.increaseCapacity(neededSize - result.size);
      result.size = neededSize;
    }
    System.arraycopy(f.argHolder.intData, 0, result.intData, 0, f.argHolder.intData.length);
    System.arraycopy(f.argHolder.doubleData, 0, result.doubleData, 0, f.argHolder.doubleData.length);
    MemChunk.copyChunks(f.argHolder.chunkData, 0, result.chunkData, 0, f.argHolder.chunkData.length);
  }

  private static void array_access_zero(MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    int arrayIndex = f.argHolder.intData[0];
    MemChunk array = f.argHolder.chunkData[0];
    if (array.numIntCols > 0)
      returnChunk.intData[argPointerVec.xInt] = array.intData[arrayIndex * array.numIntCols];
    else if (array.numDoubleCols > 0)
      returnChunk.doubleData[argPointerVec.xDouble] = arrayIndex != -1 ?
              array.doubleData[arrayIndex * array.numDoubleCols] : 0.0;
    else if (array.numChunkCols > 0)
      returnChunk.chunkData[argPointerVec.xChunk] = array.chunkData[arrayIndex * array.numChunkCols];
  }

  private static void relation_selector(MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    MemChunk result;
    MemChunk tuple;
    int neededSize = f.argHolder.chunkData.length;
    if (neededSize == 0) {
      returnChunk.chunkData[argPointerVec.xChunk] = new MemChunk(0, 0, f.returnDim);
      return;
    }
    result = returnChunk.chunkData[argPointerVec.xChunk];
    if (result == null) {
      MemChunk first = f.argHolder.chunkData[0];
      result = new MemChunk(neededSize, neededSize, first.numIntCols, first.numDoubleCols, first.numChunkCols);
      returnChunk.chunkData[argPointerVec.xChunk] = result;
    } else if (result.capacity < neededSize) {

      int increment = neededSize - result.chunkData.length;
      result.increaseCapacity(increment > result.capacity ? increment : result.capacity);
    }
    result.size = neededSize;
    MemChunk[] tuples = f.argHolder.chunkData;
    for (int i = 0; i < tuples.length; ++i) {
      tuple = tuples[i];
      System.arraycopy(tuple.intData, 0, result.intData, i * tuple.numIntCols, tuple.intData.length);
      System.arraycopy(tuple.doubleData, 0, result.doubleData, i * tuple.numDoubleCols, tuple.doubleData.length);
//            for (int col = 0; col < tuple.numChunkCols; ++col)
//              result.chunkData[i * tuple.numChunkCols + col] = tuple.chunkData[col].copy();
      MemChunk.copyChunks(tuple.chunkData, 0, result.chunkData, i * tuple.numChunkCols, tuple.chunkData.length);
    }
    result.unify();
  }

  private static void contains(MemChunk argChunk, MemChunk returnChunk) {
    MemChunk relation = argChunk.chunkData[0];
    MemChunk tuple = argChunk.chunkData[1];
    relation.buildRowIndex();
    MemChunkIndex index = relation.rowIndex;
    returnChunk.intData[0] = index.get(tuple, MemVector.ZERO, tuple.allCols) == -1 ? 0 : 1;
  }

  private static void tuple_from(MemChunk returnChunk, MemVector argPointerVec, MemFunction f, MemChunk argChunk) {
    MemChunk result;
    result = returnChunk.chunkData[argPointerVec.xChunk];
    if (result == null) {
      MemChunk src = f.argHolder.chunkData[0];
      result = new MemChunk(1, 1, src.numIntCols, src.numDoubleCols, src.numChunkCols);
      returnChunk.chunkData[argPointerVec.xChunk] = result;
    }
    System.arraycopy(argChunk.chunkData[0].intData, 0, result.intData, 0, argChunk.chunkData[0].numIntCols);
    System.arraycopy(argChunk.chunkData[0].doubleData, 0, result.doubleData, 0, argChunk.chunkData[0].numDoubleCols);
    MemChunk.copyChunks(argChunk.chunkData[0].chunkData, 0, result.chunkData, 0, argChunk.chunkData[0].numChunkCols);
  }

  private static void tuple_selector(MemChunk returnChunk, MemVector argPointerVec, MemFunction f, MemChunk argChunk) {
    MemChunk result;
    result = returnChunk.chunkData[argPointerVec.xChunk];
    if (result == null) {
      result = new MemChunk(1, 1, f.argHolder.numIntCols, f.argHolder.numDoubleCols, f.argHolder.numChunkCols);
      returnChunk.chunkData[argPointerVec.xChunk] = result;
    }
    System.arraycopy(argChunk.intData, 0, result.intData, 0, argChunk.intData.length);
    System.arraycopy(argChunk.doubleData, 0, result.doubleData, 0, argChunk.doubleData.length);
    MemChunk.copyChunks(argChunk.chunkData, 0, result.chunkData, 0, argChunk.chunkData.length);
  }

  private static void relation_copy(MemChunk argChunk, MemVector argPointerVec, MemChunk returnChunk) {
    argChunk = argChunk.chunkData[0];
    int intSize = argChunk.size * argChunk.numIntCols;
    int doubleSize = argChunk.size * argChunk.numDoubleCols;
    int chunkSize = argChunk.size * argChunk.numChunkCols;
    if (argPointerVec.xInt + intSize > returnChunk.intData.length ||
            argPointerVec.xDouble + doubleSize > returnChunk.doubleData.length ||
            argPointerVec.xChunk + chunkSize > returnChunk.chunkData.length)
      returnChunk.increaseCapacity(argChunk.size);
//          returnChunk.increaseCapacity(argChunk.size > returnChunk.size ? 5 * argChunk.size : returnChunk.size);
    System.arraycopy(argChunk.intData, 0, returnChunk.intData, argPointerVec.xInt, intSize);
    System.arraycopy(argChunk.doubleData, 0, returnChunk.doubleData, argPointerVec.xDouble, doubleSize);
    MemChunk.copyChunks(argChunk.chunkData, 0, returnChunk.chunkData, argPointerVec.xChunk, chunkSize);
    returnChunk.size += argChunk.size;
  }

  private static void tuple_copy(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    argChunk = argChunk.chunkData[0];
    System.arraycopy(argChunk.intData, 0, returnChunk.intData, argPointerVec.xInt, argChunk.intData.length);
    System.arraycopy(argChunk.doubleData, 0, returnChunk.doubleData, argPointerVec.xDouble, argChunk.doubleData.length);
    MemChunk.copyChunks(argChunk.chunkData, 0, returnChunk.chunkData, argPointerVec.xChunk, argChunk.chunkData.length);
    ++returnChunk.size;
  }

  private static void query(MemFunction f, MemChunk[] chunks, int[] rows, MemChunk returnChunk, MemVector argPointerVec) {
    MemEvaluator.evaluate(f.searchChunkFunction, chunks, rows, null, null);
    MemChunk result = returnChunk.chunkData[argPointerVec.xChunk];
    if (result == null) {
      result = new MemChunk(0, 1, f.plan.resultDim);
      returnChunk.chunkData[argPointerVec.xChunk] = result;
    }
    result.clear();
//          result.size = 0;
//          result.rowIndexedSoFar = 0;
//          result.rowIndex.clear();
    MemSearch.search(f.plan, f.searchChunkFunction.argHolder.chunkData,
            null, result, 0);
  }

  private static void or(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    boolean or = false;
    for (int bool : argChunk.intData) {
      if (bool == 1) {
        or = true;
        break;
      }
    }
    returnChunk.intData[argPointerVec.xInt] = or ? 1 : 0;
  }

  private static void and(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    boolean and = true;
    for (int bool : argChunk.intData) {
      if (bool == 0) {
        and = false;
        break;
      }
    }
    returnChunk.intData[argPointerVec.xInt] = and ? 1 : 0;
  }

  private static void not(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.intData[0] == 0 ? 1 : 0;
  }

  private static void chunk_attribute(MemChunk[] chunks, MemFunction f, int[] rows, MemChunk returnChunk, MemVector argPointerVec) {
    MemChunk chunk;
    chunk = chunks[f.chunkIndex];
    returnChunk.chunkData[argPointerVec.xChunk] =
            chunk.chunkData[rows[f.chunkIndex] * chunk.numChunkCols + f.attributeIndex];
  }

  private static void chunk_variable(MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.chunkData[argPointerVec.xChunk] = f.varChunk.chunkData[f.varPointer.xChunk];
  }

  private static void chunk_notequal(MemChunk argChunk, MemChunk returnChunk) {
    returnChunk.intData[0] = !argChunk.chunkData[0].equals(argChunk.chunkData[1]) ? 1 : 0;
  }

  private static void chunk_equal(MemChunk argChunk, MemChunk returnChunk) {
    returnChunk.intData[0] = argChunk.chunkData[0].equals(argChunk.chunkData[1]) ? 1 : 0;
  }

  private static void chunk_constant(MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.chunkData[argPointerVec.xChunk] = f.constantChunk;
  }

  private static void double_geq(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.doubleData[0] >= argChunk.doubleData[1] ? 1 : 0;
  }

  private static void double_leq(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.doubleData[0] <= argChunk.doubleData[1] ? 1 : 0;
  }

  private static void double_lt(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.doubleData[0] < argChunk.doubleData[1] ? 1 : 0;
  }

  private static void double_gt(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.doubleData[0] > argChunk.doubleData[1] ? 1 : 0;
  }

  private static void double_extract(MemChunk argChunk, MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.doubleData[argPointerVec.xDouble] = argChunk.chunkData[0].doubleData[f.attributeIndex];
  }

  private static void double_variable(MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.doubleData[argPointerVec.xDouble] = f.varChunk.doubleData[f.varPointer.xDouble];
  }

  private static void double_constant(MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.doubleData[argPointerVec.xDouble] = f.constantDouble;
  }

  private static void double_attribute(MemChunk[] chunks, MemFunction f, int[] rows, MemChunk returnChunk, MemVector argPointerVec) {
    MemChunk chunk;
    chunk = chunks[f.chunkIndex];
    returnChunk.doubleData[argPointerVec.xDouble] =
            chunk.doubleData[rows[f.chunkIndex] * chunk.numDoubleCols + f.attributeIndex];
  }

  private static void double_cast(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.doubleData[argPointerVec.xDouble] = argChunk.intData[0];
  }

  private static void double_times(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.doubleData[argPointerVec.xDouble] = argChunk.doubleData[0] * argChunk.doubleData[1];
  }

  private static void double_minus(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.doubleData[argPointerVec.xDouble] = argChunk.doubleData[0] - argChunk.doubleData[1];
  }

  private static void double_add(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.doubleData[argPointerVec.xDouble] = argChunk.doubleData[0] + argChunk.doubleData[1];
  }

  private static void double_notequal(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.doubleData[0] != argChunk.doubleData[1] ? 1 : 0;
  }

  private static void double_equal(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.doubleData[0] == argChunk.doubleData[1] ? 1 : 0;
  }

  private static void count(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.chunkData[0].size;
  }

  private static void union(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec, MemFunction f) {
    MemChunk dst;
    int maxSize = 0;
    for (int i = 0; i < argChunk.numChunkCols; ++i) maxSize += argChunk.chunkData[i].size;
    dst = returnChunk.chunkData[argPointerVec.xChunk];
    if (dst == null) {
      dst = new MemChunk(0, maxSize, f.returnDim);
      returnChunk.chunkData[argPointerVec.xChunk] = dst;
    } else {
      dst.size = 0;
      if (dst.capacity < maxSize) dst.increaseCapacity(maxSize - dst.capacity);
    }
    for (int i = 0; i < argChunk.numChunkCols; ++i)
      MemInserter.append(argChunk.chunkData[i], dst);
    dst.unify();
  }

  private static void relation_minus(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.chunkData[argPointerVec.xChunk] = argChunk.chunkData[0].minus(argChunk.chunkData[1]);
  }

  private static void int_bins(MemChunk argChunk, MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    bins:
          {
            int value = argChunk.intData[0];
      boolean positive = value >= 0;
      if (!positive) value = -value;
      for (int i = 0; i < f.bins.length; ++i)
        if (value <= f.bins[i]) {
          returnChunk.intData[argPointerVec.xInt] = positive ? i : -i;
          break bins;
        }
      returnChunk.intData[argPointerVec.xInt] = positive ? f.bins.length : -f.bins.length;
    }
  }

  private static void int_extract(MemChunk argChunk, MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.chunkData[0].intData[f.attributeIndex];
  }

  private static void int_attribute(MemChunk[] chunks, MemFunction f, int[] rows, MemChunk returnChunk, MemVector argPointerVec) {
    MemChunk chunk = chunks[f.chunkIndex];
    returnChunk.intData[argPointerVec.xInt] =
            chunk.intData[rows[f.chunkIndex] * chunk.numIntCols + f.attributeIndex];
  }

  private static void int_greaterthan(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.intData[0] > argChunk.intData[1] ? 1 : 0;
  }

  private static void int_lessthan(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.intData[0] < argChunk.intData[1] ? 1 : 0;
  }

  private static void int_geq(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.intData[0] >= argChunk.intData[1] ? 1 : 0;
  }

  private static void int_leq(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.intData[0] <= argChunk.intData[1] ? 1 : 0;
  }

  private static void int_notequal(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.intData[0] != argChunk.intData[1] ? 1 : 0;
  }

  private static void int_equal(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.intData[0] == argChunk.intData[1] ? 1 : 0;
  }

  private static void int_post_inc(MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = f.varChunk.intData[f.varPointer.xInt]++;
  }

  private static void int_variable(MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = f.varChunk.intData[f.varPointer.xInt];
  }

  private static void int_constant(MemFunction f, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = f.constantInt;
  }

  private static void int_minus(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.intData[0] - argChunk.intData[1];
  }

  private static void int_add(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    returnChunk.intData[argPointerVec.xInt] = argChunk.intData[0] + argChunk.intData[1];
  }

  private static void copy(MemChunk argChunk, MemChunk returnChunk, MemVector argPointerVec) {
    System.arraycopy(argChunk.intData, 0, returnChunk.intData, argPointerVec.xInt, argChunk.intData.length);
    System.arraycopy(argChunk.doubleData, 0, returnChunk.doubleData, argPointerVec.xDouble, argChunk.doubleData.length);
    MemChunk.copyChunks(argChunk.chunkData, 0, returnChunk.chunkData, argPointerVec.xChunk, argChunk.chunkData.length);
    ++returnChunk.size;
  }

  private static void cycles(MemFunction f, MemChunk argChunk, MemChunk returnChunk) {
    HashMap<Integer, Integer> val2vertex = new HashMap<Integer, Integer>();
    HashMap<Integer, Integer> vertex2val = new HashMap<Integer, Integer>();
    HashMap<Pair<Integer, Integer>, Integer> edge2row = new HashMap<Pair<Integer, Integer>, Integer>();
    int vertices = 0;
    MemChunk graph = argChunk.chunkData[0];
    int xInt = 0;
    for (int row = 0; row < graph.size; ++row) {
      int fromValue = graph.intData[xInt + f.cycleFrom];
      int toValue = graph.intData[xInt + f.cycleTo];
      Integer fromVertex = val2vertex.get(fromValue);
      if (fromVertex == null) {
        fromVertex = vertices++;
        val2vertex.put(fromValue, fromVertex);
        vertex2val.put(fromVertex, fromValue);
      }
      Integer toVertex = val2vertex.get(toValue);
      if (toVertex == null) {
        toVertex = vertices++;
        val2vertex.put(toValue, toVertex);
        vertex2val.put(toVertex, toValue);
      }
      edge2row.put(new Pair<Integer, Integer>(fromVertex, toVertex), row);
      xInt += graph.numIntCols;
    }
    int[][] intGraph = new int[edge2row.size()][];
    int edgeIndex = 0;
    for (Pair<Integer, Integer> edge : edge2row.keySet())
      intGraph[edgeIndex++] = new int[]{edge.arg1, edge.arg2};
//    for (int[] edge : intGraph)
//      System.out.println(Arrays.toString(edge));
    int[][] cycles = CycleFinder.findCycleVertices(intGraph, vertices);
//    for (int[] cycle : cycles) {
//      System.out.println("Arrays.toString(cycle) = " + Arrays.toString(cycle));
//    }
    MemChunk cycleChunk = returnChunk.chunkData[0];
    if (cycleChunk.capacity < cycles.length)
      cycleChunk.increaseCapacity(cycles.length - cycleChunk.capacity);
    cycleChunk.size = cycles.length;
    for (int cycleIndex = 0; cycleIndex < cycles.length; ++cycleIndex) {
      int[] cycle = cycles[cycleIndex];
      MemChunk edges = cycleChunk.chunkData[cycleIndex];
      if (edges == null) {
        edges = new MemChunk(cycle.length, cycle.length,
                graph.numIntCols, graph.numDoubleCols, graph.numChunkCols);
        cycleChunk.chunkData[cycleIndex] = edges;
      }
      if (edges.capacity < cycle.length)
        edges.increaseCapacity(cycle.length - edges.capacity);
      for (int vertexIndex = 0; vertexIndex < cycle.length; ++vertexIndex) {
        int row = edge2row.get(new Pair<Integer, Integer>(cycle[vertexIndex],
                cycle[vertexIndex == cycle.length - 1 ? 0 : vertexIndex + 1]));
        //System.out.print(graph.intData[row * 2] + "-" + graph.intData[row * 2 + 1] + " ");
        System.arraycopy(graph.intData, row * graph.numIntCols,
                edges.intData, vertexIndex * graph.numIntCols, graph.numIntCols);
        System.arraycopy(graph.doubleData, row * graph.numDoubleCols,
                edges.doubleData, vertexIndex * graph.numDoubleCols, graph.numDoubleCols);
        for (int chunkIndex = 0; chunkIndex < graph.numChunkCols; ++chunkIndex)
          edges.chunkData[vertexIndex * graph.numChunkCols + chunkIndex] =
                  graph.chunkData[row * graph.numChunkCols + chunkIndex].copy();
      }
      edges.size = cycle.length;
      //System.out.println("");
    }
  }

  public static void get(MemFunction f,
                         MemChunk[] chunks,
                         int[] rows,
                         MemChunk argChunk, MemChunk dst, MemVector dstVct) {
    int foundRow = -1;
    final MemChunk chunk = f.getRel;
    MemVector ptr = new MemVector();
    MemDim dim = chunk.getDim();
    argChunk = argChunk.chunkData[0];
    if (f.indexNr == -1) {
      int row;
      main:
      for (row = 0; row < f.getRel.size; ++row) {
        ptr.xInt = row * chunk.numIntCols;
        ptr.xDouble = row * chunk.numDoubleCols;
        ptr.xChunk = row * chunk.numChunkCols;
        for (int col = 0; col < argChunk.numIntCols; ++col)
          if (chunk.intData[ptr.xInt + f.argCols.intCols[col]] != argChunk.intData[col]) continue main;
        for (int col = 0; col < argChunk.numDoubleCols; ++col)
          if (chunk.doubleData[ptr.xDouble + f.argCols.doubleCols[col]] != argChunk.doubleData[col]) continue main;
        for (int col = 0; col < argChunk.numChunkCols; ++col)
          if (chunk.chunkData[ptr.xChunk + f.argCols.chunkCols[col]] != argChunk.chunkData[col]) continue main;
        foundRow = row;
        break;
      }
    } else {
      int[][] searchSpace = new int[1][];
      int size = chunk.indices[f.indexNr].get(argChunk, MemVector.ZERO, f.indexCols, 0, searchSpace);
      int i;
      main:
      for (i = 0; i < size; ++i) {
        int row = searchSpace[0][i];
        ptr = new MemVector(row, dim);
        for (int col = 0; col < argChunk.numIntCols; ++col)
          if (chunk.intData[ptr.xInt + f.argCols.intCols[col]] != argChunk.intData[col]) continue main;
        for (int col = 0; col < argChunk.numDoubleCols; ++col)
          if (chunk.doubleData[ptr.xDouble + f.argCols.doubleCols[col]] != argChunk.doubleData[col]) continue main;
        for (int col = 0; col < argChunk.numChunkCols; ++col)
          if (chunk.chunkData[ptr.xChunk + f.argCols.chunkCols[col]] != argChunk.chunkData[col]) continue main;
        foundRow = row;
        break;
      }
    }
    if (foundRow == -1) {
      ptr = new MemVector(chunk.size, dim);
      if (chunk.size == chunk.capacity)
        chunk.increaseCapacity(100);
      //evaluate the back off into the solution
      MemEvaluator.evaluate(f.backoffFunction, chunks, rows, dst, dstVct);
      //copy the backoff
      if (f.put) {
        for (int col = 0; col < f.resultCols.intCols.length; ++col)
          chunk.intData[ptr.xInt + f.resultCols.intCols[col]] = dst.chunkData[dstVct.xChunk].intData[col];
        for (int col = 0; col < f.resultCols.doubleCols.length; ++col)
          chunk.doubleData[ptr.xDouble + f.resultCols.doubleCols[col]] = dst.chunkData[dstVct.xChunk].doubleData[col];
        for (int col = 0; col < f.resultCols.chunkCols.length; ++col)
          chunk.chunkData[ptr.xChunk + f.resultCols.chunkCols[col]] = dst.chunkData[dstVct.xChunk].chunkData[col];
        //use the arguments
        for (int col = 0; col < f.argCols.intCols.length; ++col)
          chunk.intData[ptr.xInt + f.argCols.intCols[col]] = argChunk.intData[col];
        for (int col = 0; col < f.argCols.doubleCols.length; ++col)
          chunk.doubleData[ptr.xDouble + f.argCols.doubleCols[col]] = argChunk.doubleData[col];
        for (int col = 0; col < f.argCols.chunkCols.length; ++col)
          chunk.chunkData[ptr.xChunk + f.argCols.chunkCols[col]] = argChunk.chunkData[col];
        //add to index
        //todo: increase capacity?
        if (f.indexNr != -1) {
          if (chunk.indices[f.indexNr].getCapacity() == 0) {
            chunk.indices[f.indexNr].increaseCapacity(50);
          }
          chunk.indices[f.indexNr].add(argChunk, MemVector.ZERO, f.indexCols, chunk.size);
          ++chunk.indices[f.indexNr].indexedSoFar;
        }
        ++chunk.size;
      }
    } else {
      if (dst.chunkData[dstVct.xChunk] == null) {
        dst.chunkData[dstVct.xChunk] = new MemChunk(1, 1, f.resultCols.intCols.length,
                f.resultCols.doubleCols.length, f.resultCols.chunkCols.length);
      }
      dst = dst.chunkData[dstVct.xChunk];
      for (int col = 0; col < f.resultCols.intCols.length; ++col)
        dst.intData[col] = chunk.intData[ptr.xInt + f.resultCols.intCols[col]];
      for (int col = 0; col < f.resultCols.doubleCols.length; ++col)
        dst.doubleData[col] = chunk.doubleData[ptr.xDouble + f.resultCols.doubleCols[col]];
      for (int col = 0; col < f.resultCols.chunkCols.length; ++col)
        dst.chunkData[col] = chunk.chunkData[ptr.xChunk + f.resultCols.chunkCols[col]];
    }
  }

  public static void sparseAdd(MemFunction add, MemChunk lhs, double scale, MemChunk rhs, MemChunk dst) {
    //add lhs (and check whether it is in rhs)
    MemColumnSelector cols = new MemColumnSelector(new int[]{add.indexAtt}, new int[0], new int[0]);
    MemVector dstPtr = new MemVector();
    if (dst.capacity < lhs.size + rhs.size)
      dst.increaseCapacity(lhs.size + rhs.size - dst.capacity);
    MemDim dim = dst.getDim();

    if (add.rhsIndex != -1) {
      MemChunkMultiIndex index = rhs.indices[add.rhsIndex];
      int[][] rows = new int[1][];
      MemVector lhsPtr = new MemVector();
      for (int row = 0; row < lhs.size; ++row) {
        System.arraycopy(lhs.intData, lhsPtr.xInt, dst.intData, dstPtr.xInt, lhs.numIntCols);
        System.arraycopy(lhs.doubleData, lhsPtr.xDouble, dst.doubleData, dstPtr.xDouble, lhs.numDoubleCols);
        if (lhs.chunkData != null)
          MemChunk.copyChunks(lhs.chunkData, lhsPtr.xChunk, dst.chunkData, dstPtr.xChunk, lhs.numChunkCols);
        int count = index.get(lhs, lhsPtr, cols, 0, rows);
        if (count == 1) {
          dst.doubleData[dstPtr.xDouble + add.valueAtt] +=
                  scale * rhs.doubleData[rows[0][0] * rhs.numDoubleCols + add.valueAtt];
        }
        dstPtr.add(dim);
        lhsPtr.add(dim);
        ++dst.size;
      }
    } else {

    }
    if (add.lhsIndex != -1) {
      MemChunkMultiIndex index = lhs.indices[add.lhsIndex];
      int[][] rows = new int[1][];
      MemVector rhsPtr = new MemVector();
      for (int row = 0; row < rhs.size; ++row) {
        int count = index.get(rhs, rhsPtr, cols, 0, rows);
        if (count == 0) {
          System.arraycopy(rhs.intData, rhsPtr.xInt, dst.intData, dstPtr.xInt, lhs.numIntCols);
          System.arraycopy(rhs.doubleData, rhsPtr.xDouble, dst.doubleData, dstPtr.xDouble, lhs.numDoubleCols);
          if (rhs.chunkData != null)
            MemChunk.copyChunks(rhs.chunkData, rhsPtr.xChunk, dst.chunkData, dstPtr.xChunk, lhs.numChunkCols);
          dst.doubleData[dstPtr.xDouble + add.valueAtt] *= scale;
          dstPtr.add(dim);
          ++dst.size;
        }
        rhsPtr.add(dim);
      }
    } else {

    }


  }


}
