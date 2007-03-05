package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public final class MemFunction {

  MemColumnSelector argCols, indexCols, resultCols;
  int indexNr;
  boolean put;
  int[] bins;
  public MemChunkIndex index;
  public int groupAtt;


  public enum Type {
    COPY,
    VOID,
    INT_CONSTANT,
    INT_ADD, INT_EQUAL, INT_MINUS, INT_LEQ, INT_GEQ, INT_LESSTHAN, INT_GREATERTHAN,
    INT_ATTRIBUTE,
    INT_VARIABLE, INT_EXTRACT,
    CHUNK_CONSTANT, CHUNK_EQUAL,
    DOUBLE_CONSTANT,
    DOUBLE_ATTRIBUTE, DOUBLE_VARIABLE,
    QUERY,
    TUPLE_SELECTOR, TUPLE_COPY, TUPLE_FROM,
    CHUNK_VARIABLE, CHUNK_ATTRIBUTE,
    RELATION_SELECTOR, CONTAINS,
    INT_POST_INC, ARRAY_ACCESS, ARRAY_CREATOR,
    GROUP, INDEXED_SUM,
    AND, NOT, DOUBLE_EXTRACT, DOUBLE_GT, OR,
    OPERATOR_INV, GET,
    COUNT, RELATION_COPY, CYCLES,
    DOUBLE_LEQ, SPARSE_ADD, SUMMARIZE, RELATION_MINUS, INT_NOTEQUAL, CHUNK_NOTEQUAL, DOUBLE_ADD, UNION,
    DOUBLE_CAST, DOUBLE_MINUS, INT_BINS, DOUBLE_EQUAL, DOUBLE_TIMES, DOUBLE_NOTEQUAL,
    INDEX_COLLECTOR

  }

  /* for summarize */
  MemColumnSelector key2original;
  MemColumnSelector key2result;
  MemColumnSelector tmp2original;
  MemColumnSelector tmp2result;
  MemSummarizer.Spec[] intSpecs, doubleSpecs, chunkSpecs;
  MemFunction tmpFunction;


  public MemFunction(MemColumnSelector key2original, MemColumnSelector key2result,
                     MemColumnSelector tmp2result, MemSummarizer.Spec[] intSpecs, MemSummarizer.Spec[] doubleSpecs,
                     MemSummarizer.Spec[] chunkSpecs, MemFunction tmpFunction, MemFunction relFunction, MemDim returnDim) {
    this.key2original = key2original;
    this.key2result = key2result;
    this.tmp2result = tmp2result;
    this.intSpecs = intSpecs;
    this.doubleSpecs = doubleSpecs;
    this.chunkSpecs = chunkSpecs;
    this.tmpFunction = tmpFunction;
    this.type = Type.SUMMARIZE;
    this.argPointersVec = new MemVector[]{new MemVector(0, 0, 0)};
    this.arguments = new MemFunction[]{relFunction};
    this.argHolder = new MemChunk(1, 1, 0, 0, 1);
    this.returnDim = returnDim;
    buildStacks();
  }

  MemFunction[] arguments = new MemFunction[0];
  MemChunk argHolder;
  MemChunk constantChunk;
  int constantInt;
  double constantDouble;

  int chunkIndex;
  int attributeIndex;

  MemSearchPlan plan;
  MemFunction searchVarFunction, searchChunkFunction;

  int[] indicesToUse;
  MemVector[] argPointersVec;
  Type type;
  int maxStackDepth = 1;
  MemDim returnDim;

  MemDim tupleDim;

  MemChunk varChunk;
  MemVector varPointer;

  MemColumnSelector keyCols, groupCols, dstCols;
  int dstGroupCol;

  int indexAttribute, scaleAttribute;

  MemChunk[] returnStack;
  MemChunk[] argStack;
  MemFunction[] argFunctionStack;
  boolean[] processedStack;
  MemVector[] argPointersVecStac;

  MemFunction[] opArgFunctions;
  MemVector[] opArgVecs;
  MemChunk[] opArgs;
  MemFunction opResultFunction;

  MemFunction getRestrict;
  MemPointer[] getResultSrc, getResultDst, getArgSrc, getArgDst;
  MemFunction backoffFunction;
  MemChunk getRel, getBackoffChunk;

  int cycleFrom, cycleTo;

  int indexAtt, valueAtt, lhsIndex, rhsIndex;

  public void buildStacks() {
    int maxStackSize = getMaxStackSize();
    returnStack = new MemChunk[maxStackSize];
    argStack = new MemChunk[maxStackSize];
    argFunctionStack = new MemFunction[maxStackSize];
    processedStack = new boolean[maxStackSize];
    argPointersVecStac = new MemVector[maxStackSize];
  }

  public int getMaxStackSize() {
    int max = 1;
    for (int i = 0; i < arguments.length; ++i) {
      int size = i + 1 + arguments[i].getMaxStackSize();
      if (size > max) max = size;
    }
    return max;
  }


  public MemFunction(MemFunction graphFunction, int cycleFrom, int cycleTo) {
    this.cycleFrom = cycleFrom;
    this.cycleTo = cycleTo;
    this.arguments = new MemFunction[]{graphFunction};
    this.argHolder = new MemChunk(1, 1, 0, 0, 1);
    this.type = Type.CYCLES;
    this.argPointersVec = new MemVector[]{new MemVector(0, 0, 0)};
    buildStacks();
  }

  public MemFunction(MemChunk rel, MemFunction argFunction, MemColumnSelector argCols, MemColumnSelector resultCols,
                     MemFunction backoffFunction, boolean put, MemColumnSelector indexCols, int indexNr) {
    this.argCols = argCols;
    this.resultCols = resultCols;
    this.backoffFunction = backoffFunction;
    this.indexCols = indexCols;
    this.indexNr = indexNr;
    this.type = Type.GET;
    this.arguments = new MemFunction[]{argFunction};
    this.argPointersVec = new MemVector[]{new MemVector(0, 0, 0)};
    this.argHolder = new MemChunk(1, 1, 0, 0, 1);
    this.getRel = rel;
    this.put = put;
    buildStacks();

  }

  /**
   * Builds an OPERATOR_INV function
   *
   * @param opArgFunctions
   * @param opVecs
   * @param opArgs
   * @param result
   */
  public MemFunction(MemFunction[] opArgFunctions, MemVector[] opVecs, MemChunk[] opArgs, MemFunction result) {
    type = Type.OPERATOR_INV;
    this.opArgFunctions = opArgFunctions;
    this.opArgVecs = opVecs;
    this.opArgs = opArgs;
    this.opResultFunction = result;
    arguments = new MemFunction[0];
    buildStacks();
  }


  /**
   * Creates a INDEXED_SUM function
   *
   * @param indexAttribute the column of the index attribute
   * @param scaleAttribute the column of the scale attribute (-1 for no scaling)
   * @param array          the function that returns the array
   * @param indexRelation  the function that returns the index relation
   */
  public MemFunction(int indexAttribute, int scaleAttribute, MemFunction array, MemFunction indexRelation) {
    type = Type.INDEXED_SUM;
    argHolder = new MemChunk(1, 1, 0, 0, 2);
    argPointersVec = new MemVector[]{new MemVector(0, 0, 0), new MemVector(0, 0, 1)};
    arguments = new MemFunction[]{array, indexRelation};
    this.indexAttribute = indexAttribute;
    this.scaleAttribute = scaleAttribute;
    returnDim = new MemDim(0, 1, 0);
    buildStacks();
  }


  /**
   * Creates a GROUP expression
   *
   * @param keyCols       the columns to use as key columns
   * @param groupCols     the columns that should be grouped
   * @param chunkFunction the function that returns the table group
   * @param dstCols       the columns to put the key columns into
   * @param dstGroupCol   the column to put the group column into
   */
  public MemFunction(MemColumnSelector keyCols, MemColumnSelector dstCols,
                     MemColumnSelector groupCols, int dstGroupCol, MemFunction chunkFunction) {
    this.keyCols = keyCols;
    this.groupCols = groupCols;
    returnDim = new MemDim(0, 0, 1);
    argHolder = new MemChunk(1, 1, 0, 0, 1);
    type = Type.GROUP;
    arguments = new MemFunction[]{chunkFunction};
    argPointersVec = new MemVector[]{new MemVector()};
    this.dstCols = dstCols;
    this.groupCols = groupCols;
    this.dstGroupCol = dstGroupCol;
    buildStacks();
  }

  /**
   * Creates a int constant function.
   *
   * @param constantInt the integer to return.
   */
  public MemFunction(int constantInt) {
    this.constantInt = constantInt;
    type = Type.INT_CONSTANT;
    maxStackDepth = 1;
    arguments = new MemFunction[0];
    indicesToUse = null;
    argPointersVec = null;
    returnDim = new MemDim(1, 0, 0);
    buildStacks();
  }

  public MemFunction(double constantDouble) {
    this.constantDouble = constantDouble;
    type = Type.DOUBLE_CONSTANT;
    maxStackDepth = 1;
    arguments = new MemFunction[0];
    indicesToUse = null;
    argPointersVec = null;
    returnDim = new MemDim(1, 0, 0);
    buildStacks();
  }

  /**
   * Creates a COPY function (copies arguments in the argholder to the result chunk)
   *
   * @param pointers  for each argument function this pointer defines where to put it's return value
   * @param argHolder holder for results of argument functions
   * @param args      argument functions
   */
  public MemFunction(MemChunk argHolder, MemVector[] pointers, MemFunction... args) {
    this.arguments = args;
    this.type = Type.COPY;
    this.argHolder = argHolder;
    argPointersVec = pointers;
    returnDim = argHolder.getDim();
    buildStacks();
  }

  /**
   * Creates a VOID function (that does nothing with it's arguments in the argholder)
   *
   * @param pointers  for each argument function this pointer defines where to put it's return value
   * @param argHolder holder for results of argument functions
   * @param args      argument functions
   */
  public MemFunction(MemVector[] pointers, MemChunk argHolder, MemFunction... args) {
    this.arguments = args;
    this.type = Type.VOID;
    this.argHolder = argHolder;
    argPointersVec = pointers;
    returnDim = new MemDim(0, 0, 0);
    buildStacks();
  }


  /**
   * Create a variable function.
   *
   * @param type    must be either INT_VARIABLE, DOUBLE_VARIABLE or CHUNK_VARIABLE
   * @param chunk   the container chunk
   * @param pointer the pointer to the variable content within the container
   */
  public MemFunction(Type type, MemChunk chunk, MemVector pointer) {
    this.type = type;
    this.varChunk = chunk;
    this.varPointer = pointer;
    switch (type) {
      case INT_VARIABLE:
        returnDim = new MemDim(1, 0, 0);
        break;
      case DOUBLE_VARIABLE:
        returnDim = new MemDim(0, 1, 0);
        break;
      case CHUNK_VARIABLE:
        returnDim = new MemDim(0, 0, 1);
        break;
    }
    buildStacks();
  }


  public MemFunction(MemFunction arg, int... bins) {
    this.type = Type.INT_BINS;
    this.returnDim = new MemDim(1, 0, 0);
    this.argHolder = new MemChunk(1, 1, 1, 0, 0);
    this.arguments = new MemFunction[]{arg};
    this.argPointersVec = new MemVector[]{MemVector.ZERO};
    this.bins = bins;
    buildStacks();
  }

  /**
   * The default way of creating a MemFunction.
   *
   * @param type      type of function
   * @param argHolder the chunk the function needs to store its arguments
   * @param pointers  pointers that map the results of the argument functions to cells of the argHolder
   * @param args      the argument functions
   */
  public MemFunction(Type type, MemChunk argHolder, MemVector[] pointers, MemFunction... args) {
    this.type = type;
    argPointersVec = pointers;
    this.argHolder = argHolder;
    this.arguments = args;
    buildStacks();
  }

  public MemFunction(int indexAttribute, int valueAttribute, int lhsIndex, int rhsIndex,
                     MemFunction lhs, MemFunction scale, MemFunction rhs) {
    this.type = Type.SPARSE_ADD;
    this.argHolder = new MemChunk(1, 1, 0, 1, 2);
    this.argPointersVec = new MemVector[]{new MemVector(0, 0, 0), new MemVector(0, 0, 0), new MemVector(0, 0, 1)};
    this.arguments = new MemFunction[]{lhs, scale, rhs};
    this.indexAtt = indexAttribute;
    this.valueAtt = valueAttribute;
    this.lhsIndex = lhsIndex;
    this.rhsIndex = rhsIndex;
    buildStacks();
  }

  /**
   * The default way of creating a MemFunction that produces chunks
   *
   * @param type      type of function
   * @param chunkDim  dimension of the result chunks
   * @param argHolder the chunk the function needs to store its arguments
   * @param pointers  pointers that map the results of the argument functions to cells of the argHolder
   * @param args      the argument functions
   */
  public MemFunction(Type type, MemDim chunkDim, MemChunk argHolder, MemVector[] pointers, MemFunction... args) {
    this.type = type;
    argPointersVec = pointers;
    this.argHolder = argHolder;
    this.arguments = args;
    this.returnDim = chunkDim;
    buildStacks();
  }

  /**
   * Creates a extract component function
   *
   * @param type           any extract type: INT_EXTRACT, DOUBLE_EXTRACT etc.
   * @param attributeIndex the index of the int/double/... attribute to extract
   * @param argholder      the chunk where argument tuple is to be stored.
   * @param tupleFunction  the tuple function that creates a tuple to extract from.
   */
  public MemFunction(Type type, int attributeIndex, MemChunk argholder, MemFunction tupleFunction) {
    this.type = type;
    this.attributeIndex = attributeIndex;
    this.argHolder = argholder;
    this.arguments = new MemFunction[]{tupleFunction};
    this.argPointersVec = new MemVector[]{new MemVector(0, 0, 0)};
    buildStacks();
  }


  public MemFunction(MemSearchPlan plan, MemFunction chunkFunction, MemFunction varFunction) {
    type = Type.QUERY;
    this.plan = plan;
    searchChunkFunction = chunkFunction;
    searchVarFunction = varFunction;
    arguments = new MemFunction[0];
    buildStacks();
  }

  public MemFunction(MemChunk constantChunk) {
    this.constantChunk = constantChunk;
    type = Type.CHUNK_CONSTANT;
    maxStackDepth = 1;
    arguments = new MemFunction[0];
    indicesToUse = null;
    argPointersVec = null;
    buildStacks();
  }

  public MemFunction(Type type, MemFunction... args) {
    this.arguments = args;
    this.type = type;
    switch (type) {
      case INT_ADD:
      case INT_MINUS:
      case INT_GEQ:
      case INT_LEQ:
      case INT_LESSTHAN:
      case INT_GREATERTHAN:
      case INT_EQUAL:
      case INT_NOTEQUAL:
        argPointersVec = new MemVector[]{new MemVector(0, 0, 0), new MemVector(1, 0, 0)};
        argHolder = new MemChunk(1, 1, 2, 0, 0);
        returnDim = new MemDim(1, 0, 0);
        indicesToUse = null;
        maxStackDepth = 1 + args[1].maxStackDepth > args[0].maxStackDepth ?
                1 + args[1].maxStackDepth : args[0].maxStackDepth;
        break;
      case DOUBLE_ADD:
      case DOUBLE_MINUS:
      case DOUBLE_TIMES:
      case DOUBLE_LEQ:
      case DOUBLE_GT:
      case DOUBLE_EQUAL:
      case DOUBLE_NOTEQUAL:
        argPointersVec = new MemVector[]{new MemVector(0, 0, 0), new MemVector(0, 1, 0)};
        argHolder = new MemChunk(1, 1, 0, 2, 0);
        returnDim = new MemDim(0, 1, 0);
        indicesToUse = null;
        maxStackDepth = 1 + args[1].maxStackDepth > args[0].maxStackDepth ?
                1 + args[1].maxStackDepth : args[0].maxStackDepth;
        break;
      case CHUNK_EQUAL:
      case CHUNK_NOTEQUAL:
      case RELATION_MINUS:
        argPointersVec = new MemVector[]{new MemVector(0, 0, 0), new MemVector(0, 0, 1)};
        argHolder = new MemChunk(1, 1, 0, 0, 2);
        returnDim = new MemDim(0, 0, 1);
        indicesToUse = null;
        maxStackDepth = 1 + args[1].maxStackDepth > args[0].maxStackDepth ?
                1 + args[1].maxStackDepth : args[0].maxStackDepth;
        break;
    }
    buildStacks();
  }

  /**
   * Creates a INT, DOUBLE or CHUNK attribute depending on the type of the attributePointer.
   *
   * @param chunkIndex       the index of the chunk within the row array of the search alg.
   * @param attributePointer the pointer to the attribute in the tuple
   */
  public MemFunction(int chunkIndex, MemPointer attributePointer) {
    this.chunkIndex = chunkIndex;
    this.attributeIndex = attributePointer.pointer;
    maxStackDepth = 1;
    arguments = new MemFunction[0];
    indicesToUse = null;
    argPointersVec = null;
    switch (attributePointer.type) {
      case INT:
        type = Type.INT_ATTRIBUTE;
        returnDim = new MemDim(1, 0, 0);
        break;
      case DOUBLE:
        type = Type.DOUBLE_ATTRIBUTE;
        returnDim = new MemDim(0, 1, 0);
        break;
      case CHUNK:
        type = Type.CHUNK_ATTRIBUTE;
        returnDim = new MemDim(0, 0, 1);
        break;
    }
    buildStacks();
  }

  public String toString() {
    return type.name();
  }

  public MemDim getReturnDim() {
    return returnDim;
  }
}
