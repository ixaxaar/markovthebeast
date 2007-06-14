package thebeast.nodmem.expression;

import thebeast.nod.expression.*;
import thebeast.nod.type.*;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.*;
import thebeast.nodmem.identifier.MemIdentifierFactory;
import thebeast.nodmem.mem.*;
import thebeast.nodmem.statement.IndexInformation;
import thebeast.nodmem.type.*;
import thebeast.nodmem.variable.AbstractMemVariable;
import thebeast.nodmem.variable.MemHashIndex;
import thebeast.nodmem.variable.MemRelationVariable;
import thebeast.util.HashMultiMap;

import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class MemExpressionCompiler implements ExpressionVisitor {

  private MemFunction function;
  private MemChunk dst;
  private MemSearchAction[] searchActions;
  private boolean indexable, queryMode;
  private IndexRequirementsExtractor extractor = new IndexRequirementsExtractor();
  private HashMap<String, HashMap<String, Equality>> equalities;
  private HashMap<String, Integer> prefix2index = new HashMap<String, Integer>();
  private HashMap<String, MemHeading> prefix2heading = new HashMap<String, MemHeading>();
  private IndexInformation indexInformation;
  private IndexInformation[] indexInformations;
  protected HashSet<Expression> toSkip = new HashSet<Expression>();
  private boolean skipBranch = false;
  private AbstractMemExpression root;
  private MemExpressionFactory factory = new MemExpressionFactory();
  private ScopeStack scopeStack = new ScopeStack();

  private ExpressionBuilder builder = new ExpressionBuilder(factory, new MemIdentifierFactory(), new MemTypeFactory());

  public MemFunction compile(AbstractMemExpression expr) {
    root = expr;
    prefix2heading.clear();
    prefix2index.clear();
    try {
      expr.acceptExpressionVisitor(this);
    } catch (Exception e) {
      throw new RuntimeException("Exception when compiling " + expr, e);
    }
    return function;
  }

  public MemFunction compile(AbstractMemExpression expr, AbstractMemExpression root) {
    this.root = root;
    prefix2heading.clear();
    prefix2index.clear();
    try {
      expr.acceptExpressionVisitor(this);
    } catch (Exception e) {
      throw new RuntimeException("Exception when compiling " + expr, e);
    }
    return function;
  }

  public MemFunction compile(AbstractMemExpression expr, MemHeading context) {
    root = expr;
    prefix2heading.clear();
    prefix2index.clear();

    prefix2heading.put(null, context);
    prefix2index.put(null, 0);

    expr.acceptExpressionVisitor(this);

    return function;
  }


  public void visitIntConstant(IntConstant constant) {
    function = new MemFunction(constant.getInt());
  }

  public void visitTupleSelector(TupleSelector tupleSelector) {
    MemTupleType type = (MemTupleType) tupleSelector.type();
    MemHeading heading = type.heading();
    MemDim dim = heading.getDim();
    List<Expression> expressions = tupleSelector.expressions();
    int size = expressions.size();
    MemFunction[] functions = new MemFunction[size];
    MemChunk argHolder = new MemChunk(1, 1, dim);
    MemVector[] argPointers = new MemVector[size];
    int index = 0;
    for (Expression expr : expressions) {
      expr.acceptExpressionVisitor(this);
      functions[index] = function;
      argPointers[index] = new MemVector(heading.pointerForIndex(index));
      ++index;
    }
    function = new MemFunction(MemFunction.Type.TUPLE_SELECTOR, argHolder, argPointers, functions);
  }

  public void visitRelationSelector(RelationSelector relationSelector) {
    MemRelationType type = (MemRelationType) relationSelector.type();
    int size = relationSelector.tupleExpressions().size();
    MemDim dim = type.getDim();
    MemFunction[] functions = new MemFunction[size];
    MemVector[] argPointers = new MemVector[size];
    MemDim chunkDim = MemDim.create(0,0,size);
    MemChunk argHolder = new MemChunk(1, 1, chunkDim);
    int index = 0;
    for (Expression expr : relationSelector.tupleExpressions()) {
      argHolder.chunkData[index] = new MemChunk(1, 1, dim);
      argPointers[index] = new MemVector(0, 0, index);
      expr.acceptExpressionVisitor(this);
      functions[index] = function;
      ++index;
    }
    function = new MemFunction(relationSelector.unify() ? MemFunction.Type.RELATION_SELECTOR :
            MemFunction.Type.RELATION_SELECTOR_NO_UNIFY, dim, argHolder, argPointers, functions);
  }

  public void visitJoin(Join join) {

  }

  public void visitQuery(Query query) {
    int index = 0;
    prefix2index.clear();
    List<String> prefixes = query.prefixes();
    for (String prefix : query.prefixes()) {
      prefix2index.put(prefix, index++);
    }
    //compile relation expressions for chunk function
    int relationCount = query.relations().size();
    MemFunction[] chunkFunctions = new MemFunction[relationCount];
    IndexInformation[] indexInformations = new IndexInformation[relationCount];
    MemVector[] chunkPointer = new MemVector[relationCount];
    index = 0;
    prefix2heading.clear();
    for (RelationExpression expr : query.relations()) {
      expr.acceptExpressionVisitor(this);
      indexInformations[index] = indexInformation;
      chunkPointer[index] = new MemVector(0, 0, index);
      prefix2heading.put(query.prefixes().get(index), (MemHeading) expr.type().heading());
      chunkFunctions[index++] = function;

    }
    MemDim dim = MemDim.create(0,0,relationCount);
    MemFunction chunkFunction = new MemFunction(chunkPointer, new MemChunk(1, 1, dim), chunkFunctions);

    //get index candidates for equality expressions
    extractor.clear();
    if (query.where() != null)
      query.where().acceptExpressionVisitor(extractor);
    equalities = extractor.getPrefix2Attribute2Equality();

    toSkip = new HashSet<Expression>();

    //System.out.println(query);
    //System.out.println(equalities);

    MemSearchAction[] actions = new MemSearchAction[relationCount + 1];
    fillSearchActions(actions, relationCount, indexInformations, prefixes);
    //creating remaining "where" function
    //System.out.println(toSkip);
    function = null;
    if (query.where() != null) query.where().acceptExpressionVisitor(this);
    toSkip.clear();
    MemFunction validation = function;
    //create select function
    query.select().acceptExpressionVisitor(this);

    //todo: if function is a TUPLE_SELECTOR we can replace it with a COPY function
    //else we need a TUPLE_EXPAND
    MemFunction select;
//    if (function.type == MemFunction.Type.TUPLE_SELECTOR) {
//      select = new MemFunction(new MemChunk(function.argHolder.getDim()),function.argPointersVec, function.arguments);
//    } else
    select = new MemFunction(MemFunction.Type.TUPLE_COPY,
              new MemChunk(1, 1, MemDim.CHUNK_DIM), new MemVector[]{new MemVector(0, 0, 0)}, function);


    if (validation == null)
      actions[relationCount] = new MemSearchAction(MemSearchAction.Type.WRITE, select);
    else
      actions[relationCount] = new MemSearchAction(MemSearchAction.Type.VALIDATE_WRITE, validation, select);

    MemDim resultDim = ((MemHeading) query.select().type().heading()).getDim();

    MemSearchPlan plan = new MemSearchPlan(resultDim, actions);

    //voila
    function = new MemFunction(plan, chunkFunction, null);

  }

  private void fillSearchActions(MemSearchAction[] actions, int relationCount, IndexInformation[] indexInformations, List<String> prefixes) {
    for (int relation = 0; relation < relationCount; ++relation) {
      //get the index information for the current relation
      IndexInformation info = indexInformations[relation];
      //if (info.getIndexCount() == 0) continue;
      String prefix = prefixes.get(relation);
      HashMap<String, Equality> available = equalities.get(prefix);
      if (available != null) {
        ArrayList<String> names = new ArrayList<String>(available.keySet());
        String bestIndex = info.getMostCovering(names);
        if (bestIndex != null) {
          MemHashIndex memIndex = (MemHashIndex) info.getIndex(bestIndex);
          List<String> attributes = memIndex.attributes();
          MemFunction[] args = new MemFunction[attributes.size()];
          MemVector[] pointers = new MemVector[attributes.size()];
          MemVector dim = new MemVector();
          int attIndex = 0;
          for (String name : attributes) {
            Expression equality = available.get(name);
            toSkip.add(equality);
            Expression result = extractor.getPrefix2Attribute2Result().get(prefix).get(name);
            result.acceptExpressionVisitor(this);
            MemFunction arg = function;
            pointers[attIndex] = new MemVector(dim);
            dim.add(arg.getReturnDim());
            args[attIndex] = arg;
            ++attIndex;
          }
          int bestIndexId = info.getIndexNr(bestIndex);

          root.addDependendIndex(memIndex);


          MemColumnSelector cols = new MemColumnSelector(dim.xInt, dim.xDouble, dim.xChunk);
          MemFunction select = new MemFunction(new MemChunk(1, 1, MemDim.create(dim)), pointers, args);
          MemSearchAction action = new MemSearchAction(MemSearchAction.Type.MULTI_INDEX,
                  bestIndexId, cols, select);
          actions[relation] = action;
        } else {
          actions[relation] = new MemSearchAction(MemSearchAction.Type.ALL);
        }
      } else {
        actions[relation] = new MemSearchAction(MemSearchAction.Type.ALL);
      }
    }
  }

  public void visitQueryInsert(QueryInsert query) {
    int index = 0;
    prefix2index.clear();
    List<String> prefixes = query.prefixes();
    for (String prefix : query.prefixes()) {
      prefix2index.put(prefix, index++);
    }
    //compile relation expressions for chunk function
    int relationCount = query.relations().size();
    MemFunction[] chunkFunctions = new MemFunction[relationCount];
    IndexInformation[] indexInformations = new IndexInformation[relationCount];
    MemVector[] chunkPointer = new MemVector[relationCount];
    index = 0;
    prefix2heading.clear();
    for (RelationExpression expr : query.relations()) {
      expr.acceptExpressionVisitor(this);
      indexInformations[index] = indexInformation;
      chunkPointer[index] = new MemVector(0, 0, index);
      prefix2heading.put(query.prefixes().get(index), (MemHeading) expr.type().heading());
      chunkFunctions[index++] = function;

    }
    MemDim dim = MemDim.create(0,0,relationCount);
    MemFunction chunkFunction = new MemFunction(chunkPointer, new MemChunk(1, 1, dim), chunkFunctions);

    //get index candidates for equality expressions
    extractor.clear();
    if (query.where() != null)
      query.where().acceptExpressionVisitor(extractor);
    equalities = extractor.getPrefix2Attribute2Equality();

    toSkip = new HashSet<Expression>();

    MemSearchAction[] actions = new MemSearchAction[relationCount + 1];
    fillSearchActions(actions, relationCount, indexInformations, prefixes);
    //creating remaining "where" function
    function = null;
    if (query.where() != null) query.where().acceptExpressionVisitor(this);
    toSkip.clear();
    MemFunction validation = function;
    //create select function
    query.insert().acceptExpressionVisitor(this);

    //todo: if function is a TUPLE_SELECTOR we can replace it with a COPY function
    //else we need a TUPLE_EXPAND
    MemFunction select = new MemFunction(MemFunction.Type.RELATION_COPY,
            new MemChunk(1, 1, MemDim.CHUNK_DIM), new MemVector[]{new MemVector(0, 0, 0)}, function);


    if (validation == null)
      actions[relationCount] = new MemSearchAction(MemSearchAction.Type.WRITE, select);
    else
      actions[relationCount] = new MemSearchAction(MemSearchAction.Type.VALIDATE_WRITE, validation, select);

    MemDim resultDim = ((MemHeading) query.insert().type().heading()).getDim();

    MemSearchPlan plan = new MemSearchPlan(resultDim, actions);

    //voila
    function = new MemFunction(plan, chunkFunction, null);

  }


  public void visitEquality(Equality equality) {
    if (toSkip.contains(equality)) {
      function = null;
      return;
    }

    Expression lhs = equality.leftHandSide();
    Expression rhs = equality.rightHandSide();
    if (!lhs.type().equals(rhs.type())) {
      throw new IllegalArgumentException("Equality arguments must have same type: " +
              lhs.type() + " vs. " + rhs.type());
    }
    lhs.acceptExpressionVisitor(this);
    MemFunction functionLhs = function;
    rhs.acceptExpressionVisitor(this);
    MemFunction functionRhs = function;
    if (lhs.type() instanceof IntType || lhs.type() instanceof CategoricalType) {
      function = new MemFunction(MemFunction.Type.INT_EQUAL, functionLhs, functionRhs);
    } else if (lhs.type() instanceof RelationType || lhs.type() instanceof TupleType) {
      function = new MemFunction(MemFunction.Type.CHUNK_EQUAL, functionLhs, functionRhs);
    } else if (lhs.type() instanceof DoubleType) {
      function = new MemFunction(MemFunction.Type.DOUBLE_EQUAL, functionLhs, functionRhs);
    }
  }

  public void visitInequality(Inequality inequality) {
    Expression lhs = inequality.leftHandSide();
    Expression rhs = inequality.rightHandSide();
    if (!lhs.type().equals(rhs.type())) {
      throw new IllegalArgumentException("Inequality arguments must have same type: " +
              lhs.type() + " vs. " + rhs.type());
    }
    lhs.acceptExpressionVisitor(this);
    MemFunction functionLhs = function;
    rhs.acceptExpressionVisitor(this);
    MemFunction functionRhs = function;
    if (lhs.type() instanceof IntType || lhs.type() instanceof CategoricalType) {
      function = new MemFunction(MemFunction.Type.INT_NOTEQUAL, functionLhs, functionRhs);
    } else if (lhs.type() instanceof RelationType || lhs.type() instanceof TupleType) {
      function = new MemFunction(MemFunction.Type.CHUNK_NOTEQUAL, functionLhs, functionRhs);
    } else if (lhs.type() instanceof DoubleType) {
      function = new MemFunction(MemFunction.Type.DOUBLE_NOTEQUAL, functionLhs, functionRhs);
    }

  }

  public void visitAnd(And and) {
    LinkedList<MemFunction> args = new LinkedList<MemFunction>();
    LinkedList<MemVector> argPointers = new LinkedList<MemVector>();
    int index = 0;
    for (BoolExpression expr : and.arguments()) {
      expr.acceptExpressionVisitor(this);
      if (function != null) {
        args.add(function);
        argPointers.add(new MemVector(index++, 0, 0));
      }
    }
    int size = args.size();
    MemChunk argHolder = new MemChunk(size, size, MemDim.INT_DIM);
    function = new MemFunction(MemFunction.Type.AND, argHolder,
            argPointers.toArray(new MemVector[size]), args.toArray(new MemFunction[size]));
  }

  public void visitIntAttribute(IntAttribute intAttribute) {
    MemIntAttribute att = (MemIntAttribute) intAttribute;
    int chunkIndex = prefix2index.get(att.prefix());
    MemHeading heading = prefix2heading.get(att.prefix());
    MemPointer pointer = heading.pointerForAttribute(intAttribute.attribute().name());
    if (pointer == null)
      throw new RuntimeException(heading + " has no " + intAttribute.attribute().name() + " attribute");
    function = new MemFunction(chunkIndex, pointer);
  }

  public void visitIntVariable(IntVariable intVariable) {
    AbstractMemVariable var = (AbstractMemVariable) intVariable;
    function = new MemFunction(MemFunction.Type.INT_VARIABLE, var.getContainerChunk(), var.getPointer());
  }


  public void visitTupleVariable(TupleVariable tupleVariable) {
    AbstractMemVariable var = (AbstractMemVariable) tupleVariable;
    function = new MemFunction(MemFunction.Type.CHUNK_VARIABLE, var.getContainerChunk(), var.getPointer());
  }

  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {
    function = new MemFunction(categoricalConstant.type().index(categoricalConstant.representation()));
  }

  public void visitCategoricalVariable(CategoricalVariable categoricalVariable) {
    AbstractMemVariable var = (AbstractMemVariable) categoricalVariable;
    function = new MemFunction(MemFunction.Type.INT_VARIABLE, var.getContainerChunk(), var.getPointer());
  }

  public void visitRelationVariable(RelationVariable relationVariable) {
    MemRelationVariable var = (MemRelationVariable) relationVariable;
    function = new MemFunction(MemFunction.Type.CHUNK_VARIABLE, var.getContainerChunk(), var.getPointer());
    indexInformation = var.indexInformation();
  }

  public void visitBoolVariable(BoolVariable boolVariable) {
    AbstractMemVariable var = (AbstractMemVariable) boolVariable;
    function = new MemFunction(MemFunction.Type.INT_VARIABLE, var.getContainerChunk(), var.getPointer());
  }

  public void visitContains(Contains contains) {
    contains.relation().acceptExpressionVisitor(this);
    MemFunction relation = function;
    contains.tuple().acceptExpressionVisitor(this);
    MemFunction tuple = function;
    //todo: test whether the tuple has all components, just some or none.
    function = new MemFunction(MemFunction.Type.CONTAINS, new MemChunk(1, 1, MemDim.CHUNK2_DIM),
            new MemVector[]{new MemVector(0, 0, 0), new MemVector(0, 0, 1)}, relation, tuple);
  }

  public void visitArrayCreator(ArrayCreator arrayCreator) {
    AbstractMemType type = (AbstractMemType) arrayCreator.type().instanceType();
    int size = arrayCreator.elements().size();
    MemDim dim = type.getDim();
    MemFunction[] functions = new MemFunction[size];
    MemVector[] argPointers = new MemVector[size];
    MemChunk argHolder = new MemChunk(size, size, dim);
    int index = 0;
    for (Expression expr : arrayCreator.elements()) {
      argPointers[index] = new MemVector(index, dim);
      expr.acceptExpressionVisitor(this);
      functions[index] = function;
      ++index;
    }
    function = new MemFunction(MemFunction.Type.ARRAY_CREATOR, argHolder, argPointers, functions);

  }

  public void visitArrayVariable(ArrayVariable arrayVariable) {
    AbstractMemVariable var = (AbstractMemVariable) arrayVariable;
    function = new MemFunction(MemFunction.Type.CHUNK_VARIABLE, var.getContainerChunk(), var.getPointer());
  }

  public void visitIntPostIncrement(IntPostIncrement intPostIncrement) {
    AbstractMemVariable var = (AbstractMemVariable) intPostIncrement.variable();
    function = new MemFunction(MemFunction.Type.INT_POST_INC, var.getContainerChunk(), var.getPointer());
  }

  public void visitDoubleConstant(DoubleConstant doubleConstant) {
    function = new MemFunction(doubleConstant.getDouble());
  }

  public void visitDoubleVariable(DoubleVariable doubleVariable) {
    AbstractMemVariable var = (AbstractMemVariable) doubleVariable;
    function = new MemFunction(MemFunction.Type.DOUBLE_VARIABLE, var.getContainerChunk(), var.getPointer());
  }

  public void visitTupleFrom(TupleFrom tupleFrom) {
    MemChunk argHolder = new MemChunk(1, 1, MemDim.CHUNK_DIM);
    tupleFrom.relation().acceptExpressionVisitor(this);
    MemFunction relation = function;
    function = new MemFunction(MemFunction.Type.TUPLE_FROM,
            argHolder, new MemVector[]{new MemVector(0, 0, 0)}, relation);
  }

  public void visitIntArrayAccess(IntArrayAccess arrayAccess) {
    visitArrayAccess(arrayAccess);
  }

  public void visitDoubleArrayAccess(DoubleArrayAccess doubleArrayAccess) {
    visitArrayAccess(doubleArrayAccess);
  }

  public void visitIntExtractComponent(IntExtractComponent intExtractComponent) {
    intExtractComponent.tuple().acceptExpressionVisitor(this);
    MemFunction tuple = function;
    MemHeading memHeading = (MemHeading) intExtractComponent.tuple().type().heading();
    int attributeIndex = memHeading.
            pointerForAttribute(intExtractComponent.name()).pointer;
    MemChunk argHolder = new MemChunk(1, new int[0], new double[0], new MemChunk[]{
            new MemChunk(memHeading.getDim())});
    function = new MemFunction(MemFunction.Type.INT_EXTRACT, attributeIndex, argHolder, tuple);
  }

  public void visitRestrict(Restrict restrict) {
    //convert into query
    LinkedList<String> prefixes = new LinkedList<String>();
    prefixes.add(null);
    LinkedList<RelationExpression> from = new LinkedList<RelationExpression>();
    from.add(restrict.relation());
    LinkedList<TupleComponent> attributes = new LinkedList<TupleComponent>();
    for (Attribute attribute : restrict.relation().type().heading().attributes()) {
      attributes.add(new MemTupleComponent(attribute.name(), factory.createAttribute(null, attribute)));
    }
    TupleExpression select = factory.createTupleSelectorInvocation(attributes);
    MemQuery query = new MemQuery(restrict.type(), prefixes, from, restrict.where(), select);
    query.acceptExpressionVisitor(this);
  }

  public void visitCategoricalAttribute(CategoricalAttribute categoricalAttribute) {
    MemCategoricalAttribute att = (MemCategoricalAttribute) categoricalAttribute;
    Integer chunkIndex = prefix2index.get(att.prefix());
    if (chunkIndex == null) throw new RuntimeException(att.prefix() + " has not been defined!");
    MemHeading heading = prefix2heading.get(att.prefix());
    MemPointer pointer = heading.pointerForAttribute(categoricalAttribute.attribute().name());
    function = new MemFunction(chunkIndex, pointer);
  }

  public void visitBoolConstant(BoolConstant boolConstant) {
    function = new MemFunction(boolConstant.getBoolean() ? 1 : 0);
  }

  public void visitGroup(Group group) {
    //create the key columns
    MemHeading headingUngrouped = (MemHeading) group.relation().type().heading();
    MemHeading headingGrouped = (MemHeading) group.type().heading();
    HashSet<String> groupNames = new HashSet<String>(group.attributes());
    LinkedList<MemPointer> keyPointers = new LinkedList<MemPointer>();
    LinkedList<MemPointer> dstPointers = new LinkedList<MemPointer>();
    LinkedList<MemPointer> groupPointers = new LinkedList<MemPointer>();
    for (String name : headingUngrouped.getAttributeNames()) {
      if (groupNames.contains(name)) {
        groupPointers.add(headingUngrouped.pointerForAttribute(name));
      } else {
        keyPointers.add(headingUngrouped.pointerForAttribute(name));
        dstPointers.add(headingGrouped.pointerForAttribute(name));
      }
    }
    MemColumnSelector keyCols = new MemColumnSelector(keyPointers.toArray(new MemPointer[0]));
    MemColumnSelector dstCols = new MemColumnSelector(dstPointers.toArray(new MemPointer[0]));
    MemColumnSelector groupCols = new MemColumnSelector(groupPointers.toArray(new MemPointer[0]));
    int dstGroupCol = headingGrouped.pointerForAttribute(group.as()).pointer;
    group.relation().acceptExpressionVisitor(this);
    MemFunction rel = function;
    function = new MemFunction(keyCols, dstCols, groupCols, dstGroupCol, rel);

  }

  public void visitIndexedSum(IndexedSum indexedSum) {
    indexedSum.indexRelation().acceptExpressionVisitor(this);
    MemFunction indexRelation = function;
    indexedSum.array().acceptExpressionVisitor(this);
    MemFunction array = function;
    MemHeading heading = (MemHeading) indexedSum.indexRelation().type().heading();
    int indexCol = heading.pointerForAttribute(indexedSum.indexAttribute()).pointer;
    int scaleCol = indexedSum.scaleAttribute() == null ? -1 :
            heading.pointerForAttribute(indexedSum.scaleAttribute()).pointer;
    function = new MemFunction(indexCol, scaleCol, array, indexRelation);
  }

  public void visitDoubleAdd(DoubleAdd doubleAdd) {
    doubleAdd.leftHandSide().acceptExpressionVisitor(this);
    MemFunction lhs = function;
    doubleAdd.rightHandSide().acceptExpressionVisitor(this);
    MemFunction rhs = function;
    function = new MemFunction(MemFunction.Type.DOUBLE_ADD, lhs, rhs);
  }

  public void visitDoubleMinus(DoubleMinus doubleMinus) {
    doubleMinus.leftHandSide().acceptExpressionVisitor(this);
    MemFunction lhs = function;
    doubleMinus.rightHandSide().acceptExpressionVisitor(this);
    MemFunction rhs = function;
    function = new MemFunction(MemFunction.Type.DOUBLE_MINUS, lhs, rhs);
  }

  public void visitDoubleTimes(DoubleTimes doubleTimes) {
    doubleTimes.leftHandSide().acceptExpressionVisitor(this);
    MemFunction lhs = function;
    doubleTimes.rightHandSide().acceptExpressionVisitor(this);
    MemFunction rhs = function;
    function = new MemFunction(MemFunction.Type.DOUBLE_TIMES, lhs, rhs);

  }

  public void visitDoubleCast(DoubleCast doubleCast) {
    doubleCast.intExpression().acceptExpressionVisitor(this);
    MemFunction intFunction = function;
    function = new MemFunction(MemFunction.Type.DOUBLE_CAST, new MemChunk(1, 1, MemDim.INT_DIM),
            new MemVector[]{new MemVector(0, 0, 0), new MemVector(0, 1, 0)}, intFunction);

  }

  public void visitIntAdd(IntAdd intAdd) {
    intAdd.leftHandSide().acceptExpressionVisitor(this);
    MemFunction lhs = function;
    intAdd.rightHandSide().acceptExpressionVisitor(this);
    MemFunction rhs = function;
    function = new MemFunction(MemFunction.Type.INT_ADD, lhs, rhs);
  }

  public void visitIntMinus(IntMinus intMinus) {
    intMinus.leftHandSide().acceptExpressionVisitor(this);
    MemFunction lhs = function;
    intMinus.rightHandSide().acceptExpressionVisitor(this);
    MemFunction rhs = function;
    function = new MemFunction(MemFunction.Type.INT_MINUS, lhs, rhs);
  }

  public void visitIntLEQ(IntLEQ intLEQ) {
    if (toSkip.contains(intLEQ)) {
      function = null;
      return;
    }

    Expression lhs = intLEQ.leftHandSide();
    Expression rhs = intLEQ.rightHandSide();
    if (!lhs.type().equals(rhs.type())) {
      throw new IllegalArgumentException("Equality arguments must have same type");
    }
    lhs.acceptExpressionVisitor(this);
    MemFunction functionLhs = function;
    rhs.acceptExpressionVisitor(this);
    MemFunction functionRhs = function;
    function = new MemFunction(MemFunction.Type.INT_LEQ, functionLhs, functionRhs);

  }

  public void visitIntGEQ(IntGEQ intGEQ) {
    if (toSkip.contains(intGEQ)) {
      function = null;
      return;
    }

    Expression lhs = intGEQ.leftHandSide();
    Expression rhs = intGEQ.rightHandSide();
    if (!lhs.type().equals(rhs.type())) {
      throw new IllegalArgumentException("Equality arguments must have same type");
    }
    lhs.acceptExpressionVisitor(this);
    MemFunction functionLhs = function;
    rhs.acceptExpressionVisitor(this);
    MemFunction functionRhs = function;
    function = new MemFunction(MemFunction.Type.INT_GEQ, functionLhs, functionRhs);


  }

  public void visitIntLessThan(IntLessThan intLessThan) {
    if (toSkip.contains(intLessThan)) {
      function = null;
      return;
    }

    Expression lhs = intLessThan.leftHandSide();
    Expression rhs = intLessThan.rightHandSide();
    if (!lhs.type().equals(rhs.type())) {
      throw new IllegalArgumentException("Equality arguments must have same type");
    }
    lhs.acceptExpressionVisitor(this);
    MemFunction functionLhs = function;
    rhs.acceptExpressionVisitor(this);
    MemFunction functionRhs = function;
    function = new MemFunction(MemFunction.Type.INT_LESSTHAN, functionLhs, functionRhs);

  }

  public void visitIntGreaterThan(IntGreaterThan intGreaterThan) {
    if (toSkip.contains(intGreaterThan)) {
      function = null;
      return;
    }

    Expression lhs = intGreaterThan.leftHandSide();
    Expression rhs = intGreaterThan.rightHandSide();
    if (!lhs.type().equals(rhs.type())) {
      throw new IllegalArgumentException("Equality arguments must have same type");
    }
    lhs.acceptExpressionVisitor(this);
    MemFunction functionLhs = function;
    rhs.acceptExpressionVisitor(this);
    MemFunction functionRhs = function;
    function = new MemFunction(MemFunction.Type.INT_GREATERTHAN, functionLhs, functionRhs);

  }

  public void visitDoubleLEQ(DoubleLEQ doubleLEQ) {
    if (toSkip.contains(doubleLEQ)) {
      function = null;
      return;
    }

    Expression lhs = doubleLEQ.leftHandSide();
    Expression rhs = doubleLEQ.rightHandSide();
    if (!lhs.type().equals(rhs.type())) {
      throw new IllegalArgumentException("Comparison arguments must have same type");
    }
    lhs.acceptExpressionVisitor(this);
    MemFunction functionLhs = function;
    rhs.acceptExpressionVisitor(this);
    MemFunction functionRhs = function;
    function = new MemFunction(MemFunction.Type.DOUBLE_LEQ, functionLhs, functionRhs);


  }

  public void visitDoubleGEQ(DoubleGEQ doubleGEQ) {
    if (toSkip.contains(doubleGEQ)) {
      function = null;
      return;
    }

    Expression lhs = doubleGEQ.leftHandSide();
    Expression rhs = doubleGEQ.rightHandSide();
    if (!lhs.type().equals(rhs.type())) {
      throw new IllegalArgumentException("Comparison arguments must have same type");
    }
    lhs.acceptExpressionVisitor(this);
    MemFunction functionLhs = function;
    rhs.acceptExpressionVisitor(this);
    MemFunction functionRhs = function;
    function = new MemFunction(MemFunction.Type.DOUBLE_GEQ, functionLhs, functionRhs);

  }

  public void visitRelationAttribute(RelationAttribute relationAttribute) {
    MemRelationAttribute att = (MemRelationAttribute) relationAttribute;
    int chunkIndex = prefix2index.get(att.prefix());
    MemHeading heading = prefix2heading.get(att.prefix());
    MemPointer pointer = heading.pointerForAttribute(relationAttribute.attribute().name());
    function = new MemFunction(chunkIndex, pointer);

  }

  public void visitDoubleExtractComponent(DoubleExtractComponent doubleExtractComponent) {
    doubleExtractComponent.tuple().acceptExpressionVisitor(this);
    MemFunction tuple = function;
    MemHeading memHeading = (MemHeading) doubleExtractComponent.tuple().type().heading();
    int attributeIndex = memHeading.
            pointerForAttribute(doubleExtractComponent.name()).pointer;
    MemChunk argHolder = new MemChunk(1, new int[0], new double[0], new MemChunk[]{
            new MemChunk(memHeading.getDim())});
    function = new MemFunction(MemFunction.Type.DOUBLE_EXTRACT, attributeIndex, argHolder, tuple);
  }

  public void visitDoubleAttribute(DoubleAttribute doubleAttribute) {
    MemDoubleAttribute att = (MemDoubleAttribute) doubleAttribute;
    int chunkIndex = prefix2index.get(att.prefix());
    MemHeading heading = prefix2heading.get(att.prefix());
    MemPointer pointer = heading.pointerForAttribute(doubleAttribute.attribute().name());
    function = new MemFunction(chunkIndex, pointer);
  }

  public void visitDoubleGreaterThan(DoubleGreaterThan doubleGreaterThan) {
    if (toSkip.contains(doubleGreaterThan)) {
      function = null;
      return;
    }

    Expression lhs = doubleGreaterThan.leftHandSide();
    Expression rhs = doubleGreaterThan.rightHandSide();
    if (!lhs.type().equals(rhs.type())) {
      throw new IllegalArgumentException("double types must match for greater than comparison");
    }
    lhs.acceptExpressionVisitor(this);
    MemFunction functionLhs = function;
    rhs.acceptExpressionVisitor(this);
    MemFunction functionRhs = function;
    function = new MemFunction(MemFunction.Type.DOUBLE_GT, functionLhs, functionRhs);


  }

  public void visitDoubleLessThan(DoubleLessThan doubleLessThan) {
    if (toSkip.contains(doubleLessThan)) {
      function = null;
      return;
    }

    Expression lhs = doubleLessThan.leftHandSide();
    Expression rhs = doubleLessThan.rightHandSide();
    if (!lhs.type().equals(rhs.type())) {
      throw new IllegalArgumentException("double types must match for  less than comparison");
    }
    lhs.acceptExpressionVisitor(this);
    MemFunction functionLhs = function;
    rhs.acceptExpressionVisitor(this);
    MemFunction functionRhs = function;
    function = new MemFunction(MemFunction.Type.DOUBLE_LT, functionLhs, functionRhs);


  }

  public void visitNot(Not not) {
    not.expression().acceptExpressionVisitor(this);
    MemFunction argument = function;
    MemChunk argHolder = new MemChunk(1, 1, MemDim.INT_DIM);
    MemVector[] argPointers = new MemVector[]{new MemVector(0, 0, 0)};
    function = new MemFunction(MemFunction.Type.NOT, argHolder, argPointers, argument);
  }

  public void visitAllConstants(AllConstants allConstants) {
    int size = allConstants.ofType().values().size();
    MemChunk all = new MemChunk(size, size, MemDim.INT_DIM);
    for (int i = 0; i < size; ++i) all.intData[i] = i;
    function = new MemFunction(all);
  }

  public void visitIntOperatorInvocation(IntOperatorInvocation intOperatorInvocation) {
    visitOperatorInvocation(intOperatorInvocation);
  }

  public void visitRelationOperatorInvocation(RelationOperatorInvocation relationOperatorInvocation) {
    visitOperatorInvocation(relationOperatorInvocation);
  }

  public void visitTupleOperatorInvocation(TupleOperatorInvocation tupleOperatorInvocation) {
    visitOperatorInvocation(tupleOperatorInvocation);
  }

  private void visitOperatorInvocation(OperatorInvocation intOperatorInvocation) {
    int size = intOperatorInvocation.args().size();
    MemFunction[] argFunctions = new MemFunction[size];
    int index = 0;
    for (Object obj : intOperatorInvocation.args()) {
      Expression expr = (Expression) obj;
      expr.acceptExpressionVisitor(this);
      argFunctions[index++] = function;
    }
    MemChunk[] argChunks = new MemChunk[size];
    MemVector[] argVectors = new MemVector[size];
    index = 0;
    for (Object obj : intOperatorInvocation.operator().args()) {
      Variable var = (Variable) obj;
      argChunks[index] = ((AbstractMemVariable) var).getContainerChunk();
      argVectors[index++] = ((AbstractMemVariable) var).getPointer();
    }
    MemExpressionCompiler compiler = new MemExpressionCompiler();
    //intOperatorInvocation.operator().result().acceptExpressionVisitor(this);
    MemFunction result = compiler.compile((AbstractMemExpression) intOperatorInvocation.operator().result(), root);
    function = new MemFunction(argFunctions, argVectors, argChunks, result);
  }


  public void visitGet(Get get) {
    MemHeading heading = (MemHeading) get.relation().type().heading();
    MemHeading headingResult = (MemHeading) get.type().heading();
    MemHeading headingArgs = (MemHeading) get.argument().type().heading();
    MemRelationVariable relVar = (MemRelationVariable) get.relation();
    //to get the dependend indices right etc.
    //relVar.acceptExpressionVisitor(this);
    MemChunk rel = relVar.getContainerChunk().chunkData[relVar.getPointer().xChunk];
    int argSize = headingArgs.attributes().size();
    int fullSize = heading.attributes().size();
    MemPointer[] argPointers = new MemPointer[argSize];
    int index = 0;
    for (String name : headingArgs.getAttributeNames())
      argPointers[index++] = heading.pointerForAttribute(name);
    MemColumnSelector argCols = new MemColumnSelector(argPointers);

    //get a good index
    MemColumnSelector indexCols = null;
    String best = relVar.indexInformation().getMostCovering(headingArgs.getAttributeNames());
    int indexNr = best == null ? -1 : relVar.indexInformation().getIndexNr(best);
    if (indexNr != -1) {
      MemHashIndex memIndex = (MemHashIndex) relVar.indexInformation().getIndex(best);
      root.addDependendIndex(memIndex);
      MemPointer[] indexPointers = new MemPointer[memIndex.attributes().size()];
      index = 0;
      for (String name : memIndex.attributes()) {
        indexPointers[index++] = headingArgs.pointerForAttribute(name);
      }
      indexCols = new MemColumnSelector(indexPointers);
    }

    //now do the result cols
    MemPointer[] resultPointers = new MemPointer[fullSize - argSize];
    index = 0;
    for (String name : headingResult.getAttributeNames())
      resultPointers[index++] = heading.pointerForAttribute(name);

    MemColumnSelector resultCols = new MemColumnSelector(resultPointers);

    //now the arg expression and backoff expression
    get.argument().acceptExpressionVisitor(this);
    MemFunction argFunction = function;
    get.backoff().acceptExpressionVisitor(this);
    MemFunction backoff = function;

    //then we create the function
    function = new MemFunction(rel, argFunction, argCols, resultCols, backoff, get.put(), indexCols, indexNr);
  }

  public void visitCycles(Cycles cycles) {
    MemHeading heading = (MemHeading) cycles.graph().type().heading();
    int from = heading.pointerForAttribute(cycles.fromAttribute()).pointer;
    int to = heading.pointerForAttribute(cycles.toAttribute()).pointer;
    cycles.graph().acceptExpressionVisitor(this);
    MemFunction graph = function;
    function = new MemFunction(graph, from, to);
  }

  public void visitCount(Count count) {
    MemChunk argHolder = new MemChunk(1, 1, MemDim.CHUNK_DIM);
    count.relation().acceptExpressionVisitor(this);
    MemFunction relation = function;
    MemVector[] argVecs = new MemVector[]{new MemVector(0, 0, 0)};
    function = new MemFunction(MemFunction.Type.COUNT, argHolder, argVecs, relation);
  }

  public void visitRelationMinus(RelationMinus relationMinus) {
    relationMinus.leftHandSide().acceptExpressionVisitor(this);
    MemFunction lhs = function;
    relationMinus.rightHandSide().acceptExpressionVisitor(this);
    MemFunction rhs = function;
    function = new MemFunction(MemFunction.Type.RELATION_MINUS, new MemChunk(1, 1, MemDim.CHUNK2_DIM),
            new MemVector[]{new MemVector(0, 0, 0), new MemVector(0, 0, 1)}, lhs, rhs);
  }

  public void visitSparseAdd(SparseAdd sparseAdd) {
    sparseAdd.leftHandSide().acceptExpressionVisitor(this);
    MemFunction lhs = function;
    IndexInformation lhsIndices = indexInformation;
    sparseAdd.rightHandSide().acceptExpressionVisitor(this);
    MemFunction rhs = function;
    IndexInformation rhsIndices = indexInformation;
    sparseAdd.scale().acceptExpressionVisitor(this);
    MemFunction scale = function;

    String lhsIndexName = lhsIndices.getMostCovering(sparseAdd.indexAttribute());
    String rhsIndexName = rhsIndices.getMostCovering(sparseAdd.indexAttribute());

    if (lhsIndexName != null) {
      MemHashIndex memIndex = (MemHashIndex) lhsIndices.getIndex(lhsIndexName);
      root.addDependendIndex(memIndex);
    }
    if (rhsIndexName != null) {
      MemHashIndex memIndex = (MemHashIndex) rhsIndices.getIndex(rhsIndexName);
      root.addDependendIndex(memIndex);
    }
    int lhsIndex = lhsIndexName == null ? -1 : lhsIndices.getIndexNr(lhsIndexName);
    int rhsIndex = rhsIndexName == null ? -1 : rhsIndices.getIndexNr(rhsIndexName);


    MemHeading heading = (MemHeading) sparseAdd.type().heading();
    int indexAtt = heading.pointerForAttribute(sparseAdd.indexAttribute()).pointer;
    int valueAtt = heading.pointerForAttribute(sparseAdd.valueAttribute()).pointer;

    function = new MemFunction(indexAtt, valueAtt, lhsIndex, rhsIndex, lhs, scale, rhs);
  }

  private MemSummarizer.Spec convertSpec(Summarize.Spec spec) {
    switch (spec) {
      case DOUBLE_COUNT:
        return MemSummarizer.Spec.DOUBLE_COUNT;
      case INT_COUNT:
        return MemSummarizer.Spec.INT_COUNT;
      case DOUBLE_SUM:
        return MemSummarizer.Spec.DOUBLE_SUM;
      case INT_SUM:
        return MemSummarizer.Spec.INT_SUM;
    }
    return null;
  }

  public void visitSummarize(Summarize summarize) {
    MemHeading headingOriginal = (MemHeading) summarize.relation().type().heading();
    MemHeading headingResult = (MemHeading) summarize.type().heading();
    MemPointer[] key2originalPts = new MemPointer[summarize.by().size()];
    MemPointer[] key2resultPts = new MemPointer[summarize.by().size()];

    prefix2heading.put(null, headingOriginal);
    prefix2index.put(null, 0);

    HashMap<String, Summarize.Spec> specs = new HashMap<String, Summarize.Spec>();
    builder.clear();
    for (int i = 0; i < summarize.add().size(); ++i) {
      specs.put(summarize.as().get(i), summarize.specs().get(i));
      builder.id(summarize.as().get(i)).expr(summarize.add().get(i));
    }
    builder.tupleForIds().getExpression().acceptExpressionVisitor(this);
    MemFunction tmpFunction = function;

    int index = 0;
    ArrayList<String> sortedBy = new ArrayList<String>(summarize.by());
    Collections.sort(sortedBy);
    for (String by : sortedBy) {
      key2resultPts[index] = headingResult.pointerForAttribute(by);
      key2originalPts[index++] = headingOriginal.pointerForAttribute(by);
    }
    MemPointer[] tmp2originalPts = new MemPointer[summarize.add().size()];
    MemPointer[] tmp2resultPts = new MemPointer[summarize.add().size()];
    index = 0;
    ArrayList<String> sortedAs = new ArrayList<String>(summarize.as());
    Collections.sort(sortedAs);
    HashMap<Integer, Summarize.Spec> intSpecMap = new HashMap<Integer, Summarize.Spec>();
    HashMap<Integer, Summarize.Spec> doubleSpecMap = new HashMap<Integer, Summarize.Spec>();
    for (String as : sortedAs) {
      MemPointer ptr = headingResult.pointerForAttribute(as);
      switch (ptr.type) {
        case INT:
          intSpecMap.put(ptr.pointer, specs.get(as));
          break;
        case DOUBLE:
          doubleSpecMap.put(ptr.pointer, specs.get(as));
          break;
      }
      tmp2resultPts[index++] = ptr;
    }

    MemColumnSelector tmp2result = new MemColumnSelector(tmp2resultPts);
    MemColumnSelector key2original = new MemColumnSelector(key2originalPts);
    MemColumnSelector key2result = new MemColumnSelector(key2resultPts);

    MemSummarizer.Spec[] intSpecs = new MemSummarizer.Spec[tmp2result.intCols.length];
    MemSummarizer.Spec[] doubleSpecs = new MemSummarizer.Spec[tmp2result.doubleCols.length];
    MemSummarizer.Spec[] chunkSpecs = new MemSummarizer.Spec[tmp2result.chunkCols.length];

    for (int i = 0; i < tmp2result.intCols.length; ++i)
      intSpecs[i] = convertSpec(intSpecMap.get(tmp2result.intCols[i]));

    for (int i = 0; i < tmp2result.doubleCols.length; ++i)
      doubleSpecs[i] = convertSpec(doubleSpecMap.get(tmp2result.doubleCols[i]));

    summarize.relation().acceptExpressionVisitor(this);
    MemFunction relFunction = function;


    function = new MemFunction(key2original, key2result, tmp2result,
            intSpecs, doubleSpecs, chunkSpecs, tmpFunction, relFunction, headingResult.getDim());


  }

  public void visitUnion(Union union) {
    MemHeading heading = (MemHeading) union.type().heading();
    MemDim returnDim = heading.getDim();
    MemFunction[] args = new MemFunction[union.arguments().size()];
    MemVector[] argPointers = new MemVector[args.length];
    MemDim chunkDim = MemDim.create(0,0,args.length);
    MemChunk argHolder = new MemChunk(1, 1, chunkDim);
    int index = 0;
    for (RelationExpression expr : union.arguments()) {
      expr.acceptExpressionVisitor(this);
      argPointers[index] = new MemVector(0, 0, index);
      args[index++] = function;
    }
    function = new MemFunction(MemFunction.Type.UNION, returnDim, argHolder, argPointers, args);
  }

  public void visitIntBin(IntBins intBins) {
    intBins.argument().acceptExpressionVisitor(this);
    MemFunction arg = function;
    int[] bins = new int[intBins.bins().size()];
    int index = 0;
    for (int bin : intBins.bins())
      bins[index++] = bin;
    function = new MemFunction(arg, bins);
  }

  public void visitIndexCollector(IndexCollector indexCollector) {
    MemHeading headingGrouped = (MemHeading) indexCollector.grouped().type().heading();
    int groupAttribute = headingGrouped.pointerForAttribute(indexCollector.groupAttribute()).pointer;
    indexCollector.grouped().acceptExpressionVisitor(this);
    MemFunction grouped = function;
    function = new MemFunction(MemFunction.Type.INDEX_COLLECTOR,
            new MemChunk(1, 1, MemDim.CHUNK_DIM), new MemVector[]{MemVector.ZERO}, grouped);
    function.index = new MemChunkIndex(0, MemDim.INT_DIM);
    function.groupAtt = groupAttribute;
  }

  public void visitArrayAccess(ArrayAccess arrayAccess) {
    MemChunk argHolder = new MemChunk(1, 1, MemDim.INT_CHUNK_DIM);
    arrayAccess.array().acceptExpressionVisitor(this);
    MemFunction array = function;
    arrayAccess.index().acceptExpressionVisitor(this);
    MemFunction index = function;
    MemVector[] argPointers = new MemVector[]{new MemVector(0, 0, 0), new MemVector(0, 0, 0)};
    function = new MemFunction(MemFunction.Type.ARRAY_ACCESS_ZERO, argHolder, argPointers, array, index);

  }

  public MemFunction getResult() {
    return function;
  }


  private class IndexRequirementsExtractor extends DepthFirstExpressionVisitor {

    HashMultiMap<AttributeExpression, AttributeExpression>
            requirements = new HashMultiMap<AttributeExpression, AttributeExpression>();

    HashMultiMap<String, Attribute> prefix2AvalaibleAttributes = new HashMultiMap<String, Attribute>();

    HashMap<String, HashMap<String, Equality>> prefix2Attribute2Equality =
            new HashMap<String, HashMap<String, Equality>>();

    HashMap<String, HashMap<String, Expression>> prefix2Attribute2Result =
            new HashMap<String, HashMap<String, Expression>>();

    boolean removable = true;
    boolean extraction = false;
    boolean validating = false;
    boolean valid = true;

    AttributeExpression current;

    int currentIndex;


    public void clear() {
      requirements.clear();
      prefix2AvalaibleAttributes.clear();
      prefix2Attribute2Equality.clear();
    }

    public HashMultiMap<AttributeExpression, AttributeExpression> getRequirements() {
      return requirements;

    }


    public HashMap<String, HashMap<String, Equality>> getPrefix2Attribute2Equality() {
      return prefix2Attribute2Equality;
    }


    public HashMap<String, HashMap<String, Expression>> getPrefix2Attribute2Result() {
      return prefix2Attribute2Result;
    }

    public void visitAnd(And and) {
      if (validating && !valid) return;
      for (Expression expr : and.arguments())
        expr.acceptExpressionVisitor(this);
    }


    public void visitAttribute(AttributeExpression attribute) {
      if (validating && !valid) return;
      if (validating && prefix2index.get(attribute.prefix()) > currentIndex) {
        valid = false;
      }
    }


    public void visitBinaryExpression(BinaryExpression binaryExpression) {
      if (validating && !valid) return;
      super.visitBinaryExpression(binaryExpression);
    }

    public void visitEquality(Equality equality) {
      if (validating && !valid) return;
      if (validating) {
        equality.leftHandSide().acceptExpressionVisitor(this);
        equality.rightHandSide().acceptExpressionVisitor(this);
        return;
      }
      if (equality.leftHandSide() instanceof AttributeExpression) {
        AttributeExpression attributeExpression = (AttributeExpression) equality.leftHandSide();
        currentIndex = prefix2index.get(attributeExpression.prefix());
        validating = true;
        valid = true;
        equality.rightHandSide().acceptExpressionVisitor(this);
        if (valid) {
          HashMap<String, Equality> map = prefix2Attribute2Equality.get(attributeExpression.prefix());
          HashMap<String, Expression> result = prefix2Attribute2Result.get(attributeExpression.prefix());
          if (map == null) {
            map = new HashMap<String, Equality>();
            result = new HashMap<String, Expression>();
            prefix2Attribute2Equality.put(attributeExpression.prefix(), map);
            prefix2Attribute2Result.put(attributeExpression.prefix(), result);
          }
          map.put(attributeExpression.attribute().name(), equality);
          result.put(attributeExpression.attribute().name(), equality.rightHandSide());
        }
        validating = false;
      }
      if (equality.rightHandSide() instanceof AttributeExpression) {
        AttributeExpression attributeExpression = (AttributeExpression) equality.rightHandSide();
        currentIndex = prefix2index.get(attributeExpression.prefix());
        validating = true;
        valid = true;
        equality.leftHandSide().acceptExpressionVisitor(this);
        if (valid) {
          HashMap<String, Equality> map = prefix2Attribute2Equality.get(attributeExpression.prefix());
          HashMap<String, Expression> result = prefix2Attribute2Result.get(attributeExpression.prefix());
          if (map == null) {
            map = new HashMap<String, Equality>();
            result = new HashMap<String, Expression>();
            prefix2Attribute2Equality.put(attributeExpression.prefix(), map);
            prefix2Attribute2Result.put(attributeExpression.prefix(), result);
          }
          map.put(attributeExpression.attribute().name(), equality);
          result.put(attributeExpression.attribute().name(), equality.leftHandSide());
        }
        validating = false;
      }

    }
  }

  private static class ScopeStack {

    private Stack<List<Variable>> varStack = new Stack<List<Variable>>();
    private HashMap<Variable, Integer> indexMap = new HashMap<Variable, Integer>();
    private HashMap<Variable, MemChunk[]> invChunkMap = new HashMap<Variable, MemChunk[]>();

    public int getIndex(Variable var) {
      Integer integer = indexMap.get(var);
      return integer == null ? -1 : integer;
    }

    public MemChunk[] getInvocationChunks(Variable var) {
      return invChunkMap.get(var);
    }

    public void push(List<Variable> vars, MemChunk[] invChunks) {
      int index = 0;
      for (Variable var : vars) {
        invChunkMap.put(var, invChunks);
        indexMap.put(var, index++);
      }
      varStack.push(vars);
    }

    public void pop() {
      List<Variable> vars = varStack.pop();
      for (Variable var : vars) {
        invChunkMap.remove(var);
        indexMap.remove(var);
      }
    }

  }

}
