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
import thebeast.nodmem.expression.AbstractMemExpression;
import thebeast.nodmem.expression.MemDoubleConstant;
import thebeast.nodmem.mem.*;
import thebeast.nodmem.type.*;
import thebeast.nodmem.variable.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemInterpreter implements Interpreter, StatementVisitor {

  private MemStatementFactory factory = new MemStatementFactory();
  private MemTypeFactory typeFactory;
  private MemNoDServer server;

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
    type.loadFromRows(inputStream, var.getContainerChunk(), var.getPointer());
  }


  public DoubleVariable createDoubleVariable(DoubleExpression expr) {
    MemDoubleVariable var = new MemDoubleVariable(server, MemDoubleType.DOUBLE, new MemChunk(1, 1, 0, 1, 0));
    interpret(factory.createAssign(var, expr));
    return var;
  }

  public IntVariable createIntVariable(IntExpression expr) {
    MemIntVariable var = new MemIntVariable(server, expr.type(), new MemChunk(1, 1, 1, 0, 0));
    interpret(factory.createAssign(var, expr));
    return var;
  }

  public IntVariable createIntVariable() {
    return new MemIntVariable(server, MemIntType.INT, new MemChunk(1, 1, 1, 0, 0));
  }

  public TupleVariable createTupleVariable(TupleExpression expr) {
    MemTupleVariable var = new MemTupleVariable(server, expr.type());
    interpret(factory.createAssign(var, expr));
    return var;
  }

  public ArrayVariable createArrayVariable(Type instanceType) {
    return new MemArrayVariable(server, typeFactory.createArrayType(instanceType));
  }

  public ArrayVariable createArrayVariable(Type instanceType, int size) {
    return new MemArrayVariable(server, typeFactory.createArrayType(instanceType), size);
  }

  public ArrayVariable createDoubleArrayVariable(int size) {
    return createArrayVariable(typeFactory.doubleType(),size);
  }

  public ArrayVariable createArrayVariable(ArrayExpression expr) {
    MemArrayVariable var = new MemArrayVariable(server, expr.type());
    interpret(factory.createAssign(var, expr));
    return var;
  }

  public RelationVariable createRelationVariable(RelationExpression expr) {
    MemRelationVariable var = new MemRelationVariable(server, expr.type());
    interpret(factory.createAssign(var, expr));
    return var;
  }

  public RelationVariable createRelationVariable(Heading heading) {
    return new MemRelationVariable(server, new MemRelationType((MemHeading) heading));
  }

  public CategoricalVariable createCategoricalVariable(CategoricalExpression categorical) {
    MemCategoricalVariable var = new MemCategoricalVariable(server, categorical.type(), new MemChunk(1, 1, 1, 0, 0));
    interpret(factory.createAssign(var, categorical));
    return var;
  }

  public CategoricalVariable createCategoricalVariable(CategoricalType type) {
    return new MemCategoricalVariable(server, type, new MemChunk(1, 1, 1, 0, 0));
  }


  public BoolVariable createBoolVariable() {
    return new MemBoolVariable(server, typeFactory.boolType(), new MemChunk(1, 1, 1, 0, 0));
  }

  public BoolVariable createBoolVariable(BoolExpression expr) {
    MemBoolVariable var = new MemBoolVariable(server, typeFactory.boolType(), new MemChunk(1, 1, 1, 0, 0));
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
    return new MemDoubleVariable(server, MemDoubleType.DOUBLE, new MemChunk(1, 1, 0, 1, 0));
  }

  public void append(ArrayVariable arrayVariable, ArrayExpression expression) {
    interpret(new MemArrayAppend(arrayVariable, expression));
  }

  public void add(ArrayVariable arrayVariable, ArrayExpression argument, DoubleExpression scale){
    interpret(new MemArrayAdd(arrayVariable, argument, scale));
  }

  public void scale(ArrayVariable arrayVariable, DoubleExpression scale) {
    MemChunk dst = new MemChunk(1,1,0,1,0);
    MemEvaluator.evaluate(((AbstractMemExpression)scale).compile(), null,null,dst, MemVector.ZERO);
    AbstractMemVariable var = (AbstractMemVariable) arrayVariable;
    MemMath.scale(var.getContainerChunk().chunkData[var.getPointer().xChunk],dst.doubleData[0]);
    var.invalidate();
  }

   public void scale(ArrayVariable arrayVariable, double scale) {
    AbstractMemVariable var = (AbstractMemVariable) arrayVariable;
    MemMath.scale(var.getContainerChunk().chunkData[var.getPointer().xChunk],scale);
    var.invalidate();
  }
  public void add(ArrayVariable arrayVariable, ArrayExpression argument, double scale) {
    add(arrayVariable, argument, new MemDoubleConstant(MemDoubleType.DOUBLE,scale));
  }

  public void sparseAdd(ArrayVariable var, RelationExpression sparse, DoubleExpression scale,
                        String indexAttribute, String valueAttribute) {
    interpret(new MemArraySparseAdd(var, sparse, scale, indexAttribute, valueAttribute));
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
      buffer.chunkData[0].size = 0;
      buffer.chunkData[0].rowIndexedSoFar = 0;
      buffer.chunkData[0].rowIndex.clear();
      AbstractMemExpression expr = (AbstractMemExpression) insert.relationExp();
      MemEvaluator.evaluate(expr.compile(), null, null, buffer, new MemVector(0, 0, 0));
      src = buffer.chunkData[0];
    }
    MemInserter.insert(src, result);
    //var.invalidate();
  }

  public void visitAssign(Assign assign) {
    typeCheck(assign.target().type(), assign.expression().type(), assign);
    //System.out.println(assign.expression());
    if (assign.expression() instanceof AbstractMemVariable) {
      AbstractMemVariable other = (AbstractMemVariable) assign.expression();
      AbstractMemVariable var = (AbstractMemVariable) assign.target();
      var.copy(other);
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
    chunk.addMemChunkMultiIndex(index.memIndex());
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
    MemChunk buffer = new MemChunk(1, new int[0], new double[0], new MemChunk[]{
            new MemChunk(0, 0, result.getDim())});
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
    MemChunk buffer = new MemChunk(1, 1, 0, 1, 1);
    MemEvaluator.evaluate(scale.compile(), null, null, buffer, MemVector.ZERO);
    MemEvaluator.evaluate(sparse.compile(), null, null, buffer, MemVector.ZERO);
    MemMath.sparseAdd(var.getContainerChunk().chunkData[var.getPointer().xChunk],
            buffer.chunkData[0], buffer.doubleData[0], indexCol, valueCol);
    var.invalidate();

  }

  public void visitArrayAdd(ArrayAdd arrayAdd) {
    MemArrayVariable var = (MemArrayVariable) arrayAdd.variable();
    AbstractMemExpression scale = (AbstractMemExpression) arrayAdd.scale();
    AbstractMemExpression arg = (AbstractMemExpression) arrayAdd.argument();
    MemChunk buffer = new MemChunk(1, 1, 0, 1, 1);
    MemEvaluator.evaluate(scale.compile(), null, null, buffer, MemVector.ZERO);
    MemEvaluator.evaluate(arg.compile(), null, null, buffer, MemVector.ZERO);
    MemChunk dst = var.getContainerChunk().chunkData[var.getPointer().xChunk];
    MemMath.add(dst, buffer.chunkData[0],buffer.doubleData[0]);
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


}
