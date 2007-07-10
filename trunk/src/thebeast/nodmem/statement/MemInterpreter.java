package thebeast.nodmem.statement;

import thebeast.nod.expression.*;
import thebeast.nod.statement.*;
import thebeast.nod.type.*;
import thebeast.nod.value.BoolValue;
import thebeast.nod.value.DoubleValue;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.RelationValue;
import thebeast.nod.variable.*;
import thebeast.nodmem.MemNoDServer;
import thebeast.nodmem.value.MemRelation;
import thebeast.nodmem.expression.AbstractMemExpression;
import thebeast.nodmem.expression.MemDoubleConstant;
import thebeast.nodmem.mem.*;
import thebeast.nodmem.type.*;
import thebeast.nodmem.variable.*;
import thebeast.util.Util;
import thebeast.pml.TheBeast;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @author Sebastian Riedel
 */
public class MemInterpreter implements Interpreter, StatementVisitor {

  private MemStatementFactory factory = new MemStatementFactory();
  private MemTypeFactory typeFactory;
  private MemNoDServer server;

  private ReferenceQueue<RelationVariable> relVarReferenceQueue = new ReferenceQueue<RelationVariable>();
  private ReferenceQueue<ArrayVariable> arrayVarReferenceQueue = new ReferenceQueue<ArrayVariable>();
  private LinkedList<WeakReference<RelationVariable>>
          relVarReferences = new LinkedList<WeakReference<RelationVariable>>();
  private LinkedList<WeakReference<ArrayVariable>>
          arrayVarReferences = new LinkedList<WeakReference<ArrayVariable>>();
  private int relVarCount = 0;

  public MemInterpreter(MemNoDServer server) {
    typeFactory = (MemTypeFactory) server.typeFactory();
    this.server = server;
  }

  public void interpret(Statement statement) {
    statement.acceptStatementVisitor(this);
  }

  public BoolValue evaluateBool(BoolExpression expr) {
    return createBoolVariable(expr).value();
  }

  public IntValue evaluateInt(IntExpression expr) {
    return createIntVariable(expr).value();
  }

  public DoubleValue evaluateDouble(DoubleExpression expr) {
    return createDoubleVariable(expr).value();
  }

  public RelationValue evaluateRelation(RelationExpression expr) {
    return createRelationVariable(expr).value();
  }


  public void assign(BoolVariable variable, BoolExpression expression) {
    if (!variable.type().equals(expression.type()))
      throw new NoDTypeMismatchException(variable.type(), expression.type(), variable);
    interpret(factory.createAssign(variable, expression));
  }

  public void assign(IntVariable variable, IntExpression expression) {
    if (!variable.type().equals(expression.type()))
      throw new NoDTypeMismatchException(variable.type(), expression.type(), variable);
    interpret(factory.createAssign(variable, expression));
  }

  public void assign(DoubleVariable variable, DoubleExpression expression) {
    if (!variable.type().equals(expression.type()))
      throw new NoDTypeMismatchException(variable.type(), expression.type(), variable);
    interpret(factory.createAssign(variable, expression));
  }

  public void assign(TupleVariable variable, TupleExpression expression) {
    if (!variable.type().equals(expression.type()))
      throw new NoDTypeMismatchException(variable.type(), expression.type(), variable);
    interpret(factory.createAssign(variable, expression));
  }

  public void assign(ArrayVariable variable, ArrayExpression expression) {
    if (!variable.type().equals(expression.type()))
      throw new NoDTypeMismatchException(variable.type(), expression.type(), variable);
    interpret(factory.createAssign(variable, expression));
  }

  public void assign(RelationVariable variable, RelationExpression expression) {
    if (!variable.type().equals(expression.type()))
      throw new NoDTypeMismatchException(variable.type(), expression.type(), variable);
    interpret(factory.createAssign(variable, expression));
  }

  public void load(RelationVariable variable, InputStream inputStream) throws IOException {
    MemRelationVariable var = (MemRelationVariable) variable;
    MemRelationType type = (MemRelationType) var.type();
    var.own();
    type.loadFromRows(inputStream, var.getContainerChunk(), var.getPointer());
  }


  public DoubleVariable createDoubleVariable(DoubleExpression expr) {
    MemDoubleVariable var = new MemDoubleVariable(server, MemDoubleType.DOUBLE, new MemChunk(1, 1, MemDim.DOUBLE_DIM));
    interpret(factory.createAssign(var, expr));
    return var;
  }

  public IntVariable createIntVariable(IntExpression expr) {
    MemIntVariable var = new MemIntVariable(server, expr.type(), new MemChunk(1, 1, MemDim.INT_DIM));
    interpret(factory.createAssign(var, expr));
    return var;
  }

  public IntVariable createIntVariable() {
    return new MemIntVariable(server, MemIntType.INT, new MemChunk(1, 1, MemDim.INT_DIM));
  }

  public TupleVariable createTupleVariable(TupleExpression expr) {
    MemTupleVariable var = new MemTupleVariable(server, expr.type());
    interpret(factory.createAssign(var, expr));
    return var;
  }

  public ArrayVariable createArrayVariable(Type instanceType) {
    MemArrayVariable var = new MemArrayVariable(server, typeFactory.createArrayType(instanceType));
    arrayVarReferences.add(new WeakReference<ArrayVariable>(var,arrayVarReferenceQueue));
    return var;
  }

  public ArrayVariable createArrayVariable(Type instanceType, int size) {
    MemArrayVariable var = new MemArrayVariable(server, typeFactory.createArrayType(instanceType), size);
    arrayVarReferences.add(new WeakReference<ArrayVariable>(var,arrayVarReferenceQueue));
    return var;
  }

  public ArrayVariable createDoubleArrayVariable(int size) {
    return createArrayVariable(typeFactory.doubleType(), size);
  }

  public ArrayVariable createArrayVariable(ArrayExpression expr) {
    MemArrayVariable var = new MemArrayVariable(server, expr.type());
    interpret(factory.createAssign(var, expr));
    arrayVarReferences.add(new WeakReference<ArrayVariable>(var,arrayVarReferenceQueue));
    return var;
  }

  public RelationVariable createRelationVariable(RelationExpression expr) {
    MemRelationVariable var = new MemRelationVariable(server, expr.type());
    relVarReferences.add(new WeakReference<RelationVariable>(var, relVarReferenceQueue));
    interpret(factory.createAssign(var, expr));
    return var;
  }

  public RelationVariable createRelationVariable(Heading heading) {
    MemRelationVariable memRelationVariable = new MemRelationVariable(server, new MemRelationType((MemHeading) heading));
    relVarReferences.add(new WeakReference<RelationVariable>(memRelationVariable, relVarReferenceQueue));
    return memRelationVariable;
  }

  public CategoricalVariable createCategoricalVariable(CategoricalExpression categorical) {
    MemCategoricalVariable var = new MemCategoricalVariable(server, categorical.type(), new MemChunk(1, 1, MemDim.INT_DIM));
    ++relVarCount;
    interpret(factory.createAssign(var, categorical));
    return var;
  }

  public CategoricalVariable createCategoricalVariable(CategoricalType type) {
    return new MemCategoricalVariable(server, type, new MemChunk(1, 1, MemDim.INT_DIM));
  }


  public BoolVariable createBoolVariable() {
    return new MemBoolVariable(server, typeFactory.boolType(), new MemChunk(1, 1, MemDim.INT_DIM));
  }

  public BoolVariable createBoolVariable(BoolExpression expr) {
    MemBoolVariable var = new MemBoolVariable(server, typeFactory.boolType(), new MemChunk(1, 1, MemDim.INT_DIM));
    assign(var, expr);
    return var;
  }

  public Variable createVariable(Type type) {
    if (type instanceof IntType) {
      return createIntVariable();
    } else if (type instanceof DoubleType) {
      return createDoubleVariable();
    } else if (type instanceof CategoricalType)
      return createCategoricalVariable((CategoricalType) type);
    throw new RuntimeException("Can do generically create variables for " + type + " yet!");
  }

  public DoubleVariable createDoubleVariable() {
    return new MemDoubleVariable(server, MemDoubleType.DOUBLE, new MemChunk(1, 1, MemDim.DOUBLE_DIM));
  }

  public void append(ArrayVariable arrayVariable, ArrayExpression expression) {
    interpret(new MemArrayAppend(arrayVariable, expression));
  }




  public void append(RelationVariable relVar, RelationExpression expression){
    interpret(new MemRelationAppend(relVar, expression));
  }

  public void add(ArrayVariable arrayVariable, ArrayExpression argument, DoubleExpression scale) {
    interpret(new MemArrayAdd(arrayVariable, argument, scale));
  }

  public void scale(ArrayVariable arrayVariable, DoubleExpression scale) {
    MemChunk dst = new MemChunk(1, 1, MemDim.DOUBLE_DIM);
    MemEvaluator.evaluate(((AbstractMemExpression) scale).compile(), null, null, dst, MemVector.ZERO);
    AbstractMemVariable var = (AbstractMemVariable) arrayVariable;
    MemMath.scale(var.getContainerChunk().chunkData[var.getPointer().xChunk], dst.doubleData[0]);
    var.invalidate();
  }

  public void scale(ArrayVariable arrayVariable, double scale) {
    AbstractMemVariable var = (AbstractMemVariable) arrayVariable;
    MemMath.scale(var.getContainerChunk().chunkData[var.getPointer().xChunk], scale);
    var.invalidate();
  }

  public void add(ArrayVariable arrayVariable, ArrayExpression argument, double scale) {
    add(arrayVariable, argument, new MemDoubleConstant(MemDoubleType.DOUBLE, scale));
  }

  public void sparseAdd(ArrayVariable var, RelationExpression sparse, DoubleExpression scale,
                        String indexAttribute, String valueAttribute) {
    interpret(new MemArraySparseAdd(var, sparse, scale, indexAttribute, valueAttribute));
  }

  public void sparseAdd(ArrayVariable var, RelationExpression sparse, DoubleExpression scale, String indexAttribute,
                        String valueAttribute, boolean positive) {
    interpret(new MemArraySparseAdd(var, sparse, scale, indexAttribute, valueAttribute,
            positive ? ArraySparseAdd.Sign.NONNEGATIVE : ArraySparseAdd.Sign.NONPOSITIVE));
  }

  public void update(RelationVariable relationVariable, BoolExpression where, List<AttributeAssign> assigns) {
    interpret(new MemRelationUpdate(relationVariable, where, assigns));
  }

  public void update(RelationVariable relationVariable, List<AttributeAssign> assigns) {
    interpret(new MemRelationUpdate(relationVariable, null, assigns));
  }

  public void update(RelationVariable relationVariable, AttributeAssign assign) {
    LinkedList<AttributeAssign> assigns = new LinkedList<AttributeAssign>();
    assigns.add(assign);
    interpret(new MemRelationUpdate(relationVariable, null, assigns));
  }


  public void insert(RelationVariable var, RelationExpression relation) {
    interpret(new MemInsert(var, relation));
  }

  public void addIndex(RelationVariable var, String name, Index.Type type, Collection<String> attributes) {
    interpret(new MemCreateIndex(name, attributes, type, var));
  }

  public void addIndex(RelationVariable var, String name, Index.Type type, String... attributes) {
    interpret(new MemCreateIndex(name, Arrays.asList(attributes), type, var));
  }

  public void clear(RelationVariable var) {
    interpret(new MemClearRelationVariable((MemRelationVariable) var));
  }

  public void clear(ArrayVariable variable) {
    AbstractMemVariable var = (AbstractMemVariable) variable;
    var.getContainerChunk().chunkData[var.getPointer().xChunk].size = 0;
  }

  public void typeCheck(Type t1, Type t2, Object context) {
    if (!t1.equals(t2))
      throw new NoDTypeMismatchException(t1, t2, context);
  }

  public void visitInsert(Insert insert) {
    //System.out.println(insert.relationExp());
    MemInsert memInsert = (MemInsert) insert;
    typeCheck(insert.relationTarget().type(), insert.relationExp().type(), insert);
    MemRelationVariable var = (MemRelationVariable) insert.relationTarget();
    var.own();
    MemChunk result = var.getContainerChunk().chunkData[var.getPointer().xChunk];
    MemChunk src;
    if (insert.relationExp() instanceof AbstractMemVariable) {
      MemRelationVariable arg = (MemRelationVariable) insert.relationExp();
      src = arg.getContainerChunk().chunkData[arg.getPointer().xChunk];
    } else {
      MemChunk buffer = memInsert.getBuffer();
//      buffer.chunkData[0].size = 0;
//      buffer.chunkData[0].rowIndexedSoFar = 0;
//      if (buffer.chunkData[0].rowIndex != null) buffer.chunkData[0].rowIndex.clear();
      buffer.clear();
      AbstractMemExpression expr = (AbstractMemExpression) insert.relationExp();
      MemEvaluator.evaluate(expr.compile(), null, null, buffer, new MemVector(0, 0, 0));
      src = buffer.chunkData[0];
    }
    //MemInserter.
//    System.out.println("src.byteSize() = " + src.byteSize());
//    System.out.println("src.size = " + src.size);
//    System.out.println("result.size = " + result.size);
//    System.out.println("result.capacity = " + result.capacity);
    //MemInserter.append(src,result);
    MemInserter.insert(src, result);
    //var.invalidate();
  }

   public void visitRelationAppend(RelationAppend relationAppend) {
    MemRelationAppend append = (MemRelationAppend) relationAppend;
    MemRelationVariable var = (MemRelationVariable) relationAppend.relationTarget();
    var.own();
    MemChunk result = var.getContainerChunk().chunkData[var.getPointer().xChunk];
    MemChunk src;
    if (relationAppend.relationExp() instanceof AbstractMemVariable) {
      MemRelationVariable arg = (MemRelationVariable) relationAppend.relationExp();
      src = arg.getContainerChunk().chunkData[arg.getPointer().xChunk];
    } else {
      MemChunk buffer = append.getBuffer();
      buffer.chunkData[0].size = 0;
      buffer.chunkData[0].rowIndexedSoFar = 0;
      if (buffer.chunkData[0].rowIndex != null) buffer.chunkData[0].rowIndex.clear();
      AbstractMemExpression expr = (AbstractMemExpression) relationAppend.relationExp();
      MemEvaluator.evaluate(expr.compile(), null, null, buffer, new MemVector(0, 0, 0));
      src = buffer.chunkData[0];
    }
    MemInserter.append(src, result);

  }

  public void visitAssign(Assign assign) {
    typeCheck(assign.target().type(), assign.expression().type(), assign);
    //System.out.println(assign.expression());
    if (assign.expression() instanceof AbstractMemVariable) {
      AbstractMemVariable other = (AbstractMemVariable) assign.expression();
      AbstractMemVariable var = (AbstractMemVariable) assign.target();
      if (var.copy(other))
        var.invalidate();
    } else {
      AbstractMemExpression expr = (AbstractMemExpression) assign.expression();
      AbstractMemVariable var = (AbstractMemVariable) assign.target();
      var.own();
      MemEvaluator.evaluate(expr.compile(), null, null, var.getContainerChunk(), var.getPointer());
      var.invalidate();
    }
  }

  public void visitCreateIndex(CreateIndex createIndex) {
    MemRelationVariable memRelationVariable = (MemRelationVariable) createIndex.variable();
    MemChunk chunk = memRelationVariable.getContainerChunk().chunkData[memRelationVariable.getPointer().xChunk];
    MemHashIndex index = new MemHashIndex(memRelationVariable,
            createIndex.indexType(), createIndex.attributes());
    int nr = memRelationVariable.indexInformation().addIndex(createIndex.name(), index);
    chunk.addMemShallowMultiIndex(index.memIndex());
    for (AbstractMemExpression expr : memRelationVariable.dependendExpressions()) {
      expr.needsUpdating();
    }
  }

  public void visitRelationUpdate(RelationUpdate relationUpdate) {
    MemFunction[] args = new MemFunction[relationUpdate.attributeAssigns().size()];
    MemVector[] argVectors = new MemVector[args.length];
    MemPointer[] argPointers = new MemPointer[args.length];
    MemHeading heading = (MemHeading) relationUpdate.target().type().heading();
    int index = 0;
    for (AttributeAssign assign : relationUpdate.attributeAssigns()) {
      args[index] = ((AbstractMemExpression) assign.expression()).compile(heading);
      argPointers[index] = heading.pointerForAttribute(assign.attributeName());
      argVectors[index] = new MemVector(argPointers[index]);
      ++index;
    }
    MemFunction where = relationUpdate.where() == null ? null :
            ((AbstractMemExpression) relationUpdate.where()).compile(heading);
    MemRelationVariable memRelationVariable = (MemRelationVariable) relationUpdate.target();
    memRelationVariable.own();
    MemChunk chunk = memRelationVariable.getContainerChunk().chunkData[memRelationVariable.getPointer().xChunk];
    MemUpdater.update(chunk, where, argVectors, argPointers, args);
    memRelationVariable.invalidate();
  }

  public void visitArrayAppend(ArrayAppend arrayAppend) {
    AbstractMemExpression expr = (AbstractMemExpression) arrayAppend.expression();
    MemArrayVariable var = (MemArrayVariable) arrayAppend.variable();
    //write solution in a buffer chunk
    MemChunk result = var.getContainerChunk().chunkData[var.getPointer().xChunk];
    if (result == null) {
      result = new MemChunk(0, 0, ((AbstractMemType) var.type().instanceType()).getDim());
      var.getContainerChunk().chunkData[var.getPointer().xChunk] = result;
    }
    //int size = arrayAppend.expression().
    MemChunk buffer = new MemChunk(1, new int[0], new double[0], new MemChunk[]{
            new MemChunk(0, 0, ((AbstractMemType)var.type().instanceType()).getDim())});
    MemEvaluator.evaluate(expr.compile(), null, null, buffer, new MemVector(0, 0, 0));
    MemInserter.append(buffer.chunkData[0], result);
    var.invalidate();
  }

  public void visitClearRelationVariable(ClearRelationVariable clearRelationVariable) {
    MemRelationVariable var = (MemRelationVariable) clearRelationVariable.variable();
    var.own();
    var.getContainerChunk().chunkData[var.getPointer().xChunk].size = 0;
    var.invalidate();
  }

  public void visitArraySparseAdd(ArraySparseAdd arraySparseAdd) {
    MemArrayVariable var = (MemArrayVariable) arraySparseAdd.variable();
    MemHeading heading = (MemHeading) arraySparseAdd.sparseVector().type().heading();
    int indexCol = heading.pointerForAttribute(arraySparseAdd.indexAttribute()).pointer;
    int valueCol = heading.pointerForAttribute(arraySparseAdd.valueAttribute()).pointer;
    AbstractMemExpression scale = (AbstractMemExpression) arraySparseAdd.scale();
    AbstractMemExpression sparse = (AbstractMemExpression) arraySparseAdd.sparseVector();
    MemChunk buffer = new MemChunk(1, 1, MemDim.DOUBLE_CHUNK_DIM);
    MemEvaluator.evaluate(scale.compile(), null, null, buffer, MemVector.ZERO);
    MemEvaluator.evaluate(sparse.compile(), null, null, buffer, MemVector.ZERO);
    switch (arraySparseAdd.sign()) {
      case FREE:
        MemMath.sparseAdd(var.getContainerChunk().chunkData[var.getPointer().xChunk],
                buffer.chunkData[0], buffer.doubleData[0], indexCol, valueCol);
        break;
      case NONNEGATIVE:
        MemMath.sparseAdd(var.getContainerChunk().chunkData[var.getPointer().xChunk],
                buffer.chunkData[0], buffer.doubleData[0], indexCol, valueCol, true);
        break;
      case NONPOSITIVE:
        MemMath.sparseAdd(var.getContainerChunk().chunkData[var.getPointer().xChunk],
                buffer.chunkData[0], buffer.doubleData[0], indexCol, valueCol, true);
        break;

    }
    var.invalidate();

  }

  public void visitArrayAdd(ArrayAdd arrayAdd) {
    MemArrayVariable var = (MemArrayVariable) arrayAdd.variable();
    AbstractMemExpression scale = (AbstractMemExpression) arrayAdd.scale();
    AbstractMemExpression arg = (AbstractMemExpression) arrayAdd.argument();
    MemChunk buffer = new MemChunk(1, 1, MemDim.DOUBLE_CHUNK_DIM);
    MemEvaluator.evaluate(scale.compile(), null, null, buffer, MemVector.ZERO);
    MemEvaluator.evaluate(arg.compile(), null, null, buffer, MemVector.ZERO);
    MemChunk dst = var.getContainerChunk().chunkData[var.getPointer().xChunk];
    MemMath.add(dst, buffer.chunkData[0], buffer.doubleData[0]);
    var.invalidate();

  }


  public void append(ArrayVariable arrayVariable, int howmany, Object constant) {
    MemArrayVariable var = (MemArrayVariable) arrayVariable;
    //var.own();
    MemChunk chunk = var.getContainerChunk().chunkData[var.getPointer().xChunk];
    if (chunk.size + howmany > chunk.capacity)
      chunk.increaseCapacity(chunk.size + howmany - chunk.capacity);
    if (arrayVariable.type().instanceType() instanceof DoubleType) {
      double value = (Double) constant;
      Arrays.fill(chunk.doubleData, chunk.size, chunk.size + howmany, value);
    } else if (arrayVariable.type().instanceType() instanceof IntType) {
      int value = (Integer) constant;
      Arrays.fill(chunk.intData, chunk.size, chunk.size + howmany, value);
    } else if (arrayVariable.type().instanceType() instanceof CategoricalType) {
      CategoricalType type = (CategoricalType) arrayVariable.type().instanceType();
      int value = type.index((String) constant);
      Arrays.fill(chunk.intData, chunk.size, chunk.size + howmany, value);
    } else
      throw new IllegalArgumentException("Can't append other types then ints, doubles and categoricals with this method");
    chunk.size += howmany;
    var.invalidate();
  }

  public String getMemoryString(){
    int gced = 0;
    int alive = 0;
    int totalRowCount = 0;
    int totalCapacity = 0;
    int totalBytesize = 0;
    int instanceCount = 0;
    int maxRowCount = 0;
    int maxCapacity = 0;
    RelationVariable maxRelVar = null;
    for (WeakReference<RelationVariable> ref : relVarReferences){
      if (ref.isEnqueued())
        ++gced;
      else {
        totalRowCount += ref.get().value().size();
        int capacity = ((MemRelationVariable) ref.get()).getContainerChunk().chunkData[0].capacity;
        totalCapacity += capacity;
        totalBytesize += ref.get().byteSize();
        ++alive;
        if (ref.get().value().size()> maxRowCount){
          maxRowCount = ref.get().value().size();
          maxRelVar = ref.get();
        }
        if (capacity > maxCapacity){
          maxCapacity = capacity;
        }

      }
    }

    System.out.println(maxRelVar.type().heading());

    instanceCount = relVarReferences.size();
    Formatter formatter = new Formatter();
    printStats("RelVars", formatter, instanceCount, gced, alive, totalRowCount, totalCapacity, totalBytesize);
    formatter.format("%-30s%-7d\n","RelVars max row count", maxRowCount);
    formatter.format("%-30s%-7d\n","RelVars max capacity", maxCapacity);

    totalRowCount = 0;
    totalCapacity = 0;
    totalBytesize = 0;
    gced = 0;
    alive = 0;
    instanceCount = 0;
    for (WeakReference<ArrayVariable> ref : arrayVarReferences){
      if (ref.isEnqueued())
        ++gced;
      else {
        totalRowCount += ref.get().value().size();
        totalCapacity += ((MemArrayVariable)ref.get()).getContainerChunk().chunkData[0].capacity;
        totalBytesize += ref.get().byteSize();
        ++alive;
      }
      ++instanceCount;
    }
    printStats("ArrayVars", formatter, instanceCount, gced, alive, totalRowCount, totalCapacity, totalBytesize);
    

    gced = 0;
    alive = 0;
    totalBytesize = 0;
    for (WeakReference<Expression> ref : AbstractMemExpression.references()){
      if (ref.isEnqueued())
        ++gced;
      else {
        ++alive;
        AbstractMemExpression expr = (AbstractMemExpression) ref.get();
        if (!(expr instanceof AbstractMemVariable))
          totalBytesize += expr.byteSize();
        //System.out.println(expr);
      }
    }
    formatter.format("%-30s%-7d\n","Expressions instantiated", AbstractMemExpression.references().size());
    formatter.format("%-30s%-7d\n","Expressions gced", gced);
    formatter.format("%-30s%-7d\n","Expressions alive", alive);
    formatter.format("%-30s%-7s\n","Expressions total bytesize", Util.toMemoryString(totalBytesize));

    gced = 0;
    alive = 0;
    totalBytesize = 0;
    for (WeakReference<Insert> ref : MemInsert.references()){
      if (ref.isEnqueued())
        ++gced;
      else {
        ++alive;
        MemInsert expr = (MemInsert) ref.get();
        totalBytesize += expr.byteSize();
      }
    }
    formatter.format("%-30s%-7d\n","Inserts instantiated", MemInsert.references().size());
    formatter.format("%-30s%-7d\n","Inserts gced", gced);
    formatter.format("%-30s%-7d\n","Inserts alive", alive);
    formatter.format("%-30s%-7s\n","Inserts total bytesize", Util.toMemoryString(totalBytesize));

    gced = 0;
    alive = 0;
    totalBytesize = 0;
    for (WeakReference<RelationValue> ref : MemRelation.references()){
      if (ref.isEnqueued())
        ++gced;
      else {
        ++alive;
        MemRelation expr = (MemRelation) ref.get();
        totalBytesize += expr.chunk().byteSize();
      }
    }
    formatter.format("%-30s%-7d\n","Relations instantiated", MemRelation.references().size());
    formatter.format("%-30s%-7d\n","Relations gced", gced);
    formatter.format("%-30s%-7d\n","Relations alive", alive);
    formatter.format("%-30s%-7s\n","Relations total bytesize", Util.toMemoryString(totalBytesize));


    gced = 0;
    alive = 0;
    totalBytesize = 0;
    totalRowCount = 0;
    totalCapacity = 0;
    maxRowCount = 0;
    maxCapacity = 0;
    MemChunk maxChunk = null;
    int withIndex = 0;
    for (WeakReference<MemChunk> ref : MemChunk.references()){
      if (ref.isEnqueued())
        ++gced;
      else {
        ++alive;
        totalBytesize += ref.get().byteSize();
        totalRowCount += ref.get().size;
        totalCapacity += ref.get().capacity;
        if (ref.get().rowIndex != null)  ++withIndex;
        if (ref.get().size > maxRowCount){
          maxRowCount = ref.get().size;
          maxChunk = ref.get();
        }
        if (ref.get().capacity > maxCapacity)
          maxCapacity = ref.get().capacity;
      }
    }

    //System.out.println(maxChunk);
    //System.out.println(maxChunk.dim);
    //System.out.println(maxChunk.size);
    //System.out.println(Arrays.toString(maxChunk.intData));

    formatter.format("%-30s%-7d\n","Chunks instantiated", MemChunk.references().size());
    formatter.format("%-30s%-7d\n","Chunks gced", gced);
    formatter.format("%-30s%-7d\n","Chunks alive", alive);
    formatter.format("%-30s%-7s\n","Chunks total bytesize", Util.toMemoryString(totalBytesize));
    formatter.format("%-30s%-7s\n","Chunks avg bytesize", Util.toMemoryString((double) totalBytesize / (double) alive));
    formatter.format("%-30s%-7f\n","Chunks avg capacity", (double) totalCapacity / (double) alive);
    formatter.format("%-30s%-7d\n","Chunks total row count", totalRowCount);
    formatter.format("%-30s%-7f\n","Chunks avg row count", (double) totalRowCount / (double) alive);
    formatter.format("%-30s%-7d\n","Chunks max row count", maxRowCount);
    formatter.format("%-30s%-7d\n","Chunks max capacity", maxCapacity);
    formatter.format("%-30s%-7d\n","Chunks with indices", withIndex);

    //formatter.format("\n");
    formatter.format("%-30s%-7s\n","Java mem usage", Util.toMemoryString(Runtime.getRuntime().totalMemory()));
    formatter.format("%-30s%-7s\n","Java free mem", Util.toMemoryString(Runtime.getRuntime().freeMemory()));

    formatter.format("%s\n", TheBeast.getInstance().getNodServer().expressionBuilder().stackSize());

    return formatter.toString();
  }

  private void printStats(String name, Formatter formatter, int instanceCount, int gced, int alive, int totalRowCount, int totalCapacity, int totalBytesize) {
    formatter.format("%-30s%-7d\n", name + " instantiated", instanceCount);
    formatter.format("%-30s%-7d\n",name + " gced", gced);
    formatter.format("%-30s%-7d\n",name + " alive", alive);
    formatter.format("%-30s%-7d\n",name + " total row count", totalRowCount);
    formatter.format("%-30s%-7f\n",name + " avg row count", (double) totalRowCount / (double) alive);
    formatter.format("%-30s%-7d\n",name + " total capacity", totalCapacity);
    formatter.format("%-30s%-7f\n",name + " avg capacity", (double) totalCapacity / (double) alive);
    formatter.format("%-30s%-7s\n",name + " total bytesize", Util.toMemoryString(totalBytesize));
  }


}
