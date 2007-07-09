package thebeast.nod.util;

import thebeast.nod.NoDServer;
import thebeast.nod.exception.NoDExpressionBuilderException;
import thebeast.nod.expression.*;
import thebeast.nod.identifier.IdentifierFactory;
import thebeast.nod.type.*;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.RelationVariable;

import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class ExpressionBuilder {

  private Stack<Expression> expressionStack = new Stack<Expression>();
  private Stack<String> nameStacks = new Stack<String>();
  private ExpressionFactory expressionFactory;
  private TypeFactory typeFactory;
  private Heading heading;
  private LinkedList<String> prefixes = new LinkedList<String>();
  private LinkedList<RelationExpression> from = new LinkedList<RelationExpression>();
  private TupleExpression select;
  private RelationExpression insert;
  private BoolExpression where;
  private IdentifierFactory identifierFactory;
  private HashMap<String,RelationExpression> prefix2rel = new HashMap<String, RelationExpression>();
  private LinkedList<String> by = new LinkedList<String>();
  private LinkedList<String> as = new LinkedList<String>();
  private LinkedList<ScalarExpression> add = new LinkedList<ScalarExpression>();
  private LinkedList<Summarize.Spec> specs = new LinkedList<Summarize.Spec>();


  public ExpressionBuilder(NoDServer server) {
    this.expressionFactory = server.expressionFactory();
    this.typeFactory = server.typeFactory();
    this.identifierFactory = server.identifierFactory();
  }


  public ExpressionBuilder(ExpressionFactory expressionFactory,
                           IdentifierFactory identifierFactory,
                           TypeFactory typeFactory) {
    this.expressionFactory = expressionFactory;
    this.identifierFactory = identifierFactory;
    this.typeFactory = typeFactory;
  }

  public ExpressionBuilder createBuilder(){
    return new ExpressionBuilder(expressionFactory, identifierFactory, typeFactory);
  }

  public ExpressionBuilder id(String id) {
    nameStacks.push(id);
    return this;
  }

  public ExpressionBuilder heading(Heading heading) {
    this.heading = heading;
    return this;
  }

  public ExpressionBuilder intPostInc() {
    expressionStack.push(expressionFactory.createIntPostIncrement((IntVariable) expressionStack.pop()));
    return this;
  }

  public ExpressionBuilder asTuple(Object... args) {
    for (int i = 0; i < args.length; ++i) {
      Attribute attribute = heading.attributes().get(i);
      id(attribute.name());
      Type type = attribute.type();
      if (type instanceof IntType) {
        IntType intType = (IntType) type;
        integer(intType, (Integer) args[i]);
      } else if (type instanceof CategoricalType) {
        CategoricalType categoricalType = (CategoricalType) type;
        categorical(categoricalType, (String) args[i]);
      } else if (type instanceof DoubleType) {
        DoubleType doubleType = (DoubleType) type;
        doubleValue(doubleType, (Double) args[i]);
      }
    }
    return tuple();
  }


  public ExpressionBuilder relVar(RelationType type, String id) {
    expressionStack.push(expressionFactory.createRelationVariableReference(type, identifierFactory.createName(id)));
    return this;
  }

  public ExpressionBuilder constant(Object value) {
    if (value instanceof Integer)
      expressionStack.push(expressionFactory.createIntConstant(typeFactory.intType(), (Integer) value));
    return this;
  }

  public ExpressionBuilder and(int howmany) {
    LinkedList<BoolExpression> args = new LinkedList<BoolExpression>();
    for (int i = 0; i < howmany; ++i)
      args.addFirst((BoolExpression) expressionStack.pop());
    expressionStack.push(expressionFactory.createAnd(args));
    return this;
  }

  public ExpressionBuilder array(int howMany) {
    LinkedList<Expression> elements = new LinkedList<Expression>();
    for (int i = 0; i < howMany; ++i) {
      elements.addFirst(expressionStack.pop());
    }
    expressionStack.push(expressionFactory.createArrayCreator(elements));
    return this;
  }

  public ExpressionBuilder constant(Type type, Object value) {
    try {
      if (type instanceof IntType) {
        expressionStack.push(expressionFactory.createIntConstant((IntType) type, (Integer) value));
      } else if (type instanceof CategoricalType) {
        expressionStack.push(expressionFactory.createCategoricalConstant((CategoricalType) type, (String) value));
      }
      return this;
    }
    catch (ClassCastException e) {
      throw new NoDExpressionBuilderException("Type mismatch: " + value + " can't be converted into a " + type);
    }
  }

  public ExpressionBuilder categorical(CategoricalType type, String representation) {
    expressionStack.push(expressionFactory.createCategoricalConstant(type, representation));
    return this;
  }

  public ExpressionBuilder categorical(String type, String representation) {
    expressionStack.push(expressionFactory.createCategoricalConstant(
            typeFactory.categoricalType(identifierFactory.createName(type)), representation));
    return this;
  }

  public ExpressionBuilder integer(IntType type, int value) {
    expressionStack.push(expressionFactory.createIntConstant(type, value));
    return this;
  }

  public ExpressionBuilder integer(int value) {
    expressionStack.push(expressionFactory.createIntConstant(typeFactory.intType(), value));
    return this;
  }

  public ExpressionBuilder num(Number number) {
    if (number instanceof Integer)
      expressionStack.push(expressionFactory.createIntConstant(typeFactory.intType(), (Integer) number));
    else if (number instanceof Double)
      expressionStack.push(expressionFactory.createDoubleConstant(typeFactory.doubleType(), (Double) number));
    else throw new RuntimeException("num can only take integers and doubles");
    return this;
  }

  public ExpressionBuilder integer(String type, int value) {
    expressionStack.push(expressionFactory.createIntConstant(
            typeFactory.intType(identifierFactory.createName(type)), value));
    return this;
  }

  public ExpressionBuilder equality() {
    Expression rhs = expressionStack.pop();
    Expression lhs = expressionStack.pop();
    expressionStack.push(expressionFactory.createEquality(lhs, rhs));
    return this;
  }

  public ExpressionBuilder expr(Expression expression) {
    expressionStack.push(expression);
    return this;
  }

  public ExpressionBuilder tuple() {
    LinkedList<TupleComponent> components = new LinkedList<TupleComponent>();
    int index = 0;
    while (!expressionStack.empty()) {
      Expression expression = expressionStack.pop();
      if (nameStacks.isEmpty()) id("_att" + index++);
      String id = nameStacks.pop();
      components.addFirst(expressionFactory.createTupleComponent(id, expression));
    }
    expressionStack.push(expressionFactory.createTupleSelectorInvocation(components));
    return this;
  }

  public ExpressionBuilder tupleForIds() {
    LinkedList<TupleComponent> components = new LinkedList<TupleComponent>();
    while (!nameStacks.empty()) {
      Expression expression = expressionStack.pop();
      assert expression != null;
      String id = nameStacks.pop();
      components.addFirst(expressionFactory.createTupleComponent(id, expression));
    }
    expressionStack.push(expressionFactory.createTupleSelectorInvocation(components));
    return this;
  }


  public ExpressionBuilder tuple(int howMany) {
    LinkedList<TupleComponent> components = new LinkedList<TupleComponent>();
    int index = howMany - 1;
    for (int i = 0; i < howMany; ++i) {
      Expression expression = expressionStack.pop();
      if (nameStacks.isEmpty()) id("_att" + index--);
      String id = nameStacks.pop();
      components.addFirst(expressionFactory.createTupleComponent(id, expression));
    }
    expressionStack.push(expressionFactory.createTupleSelectorInvocation(components));
    return this;
  }

  public ExpressionBuilder tuple(Heading heading) {
    LinkedList<TupleComponent> components = new LinkedList<TupleComponent>();
    LinkedList<Expression> args = new LinkedList<Expression>();
    int count = heading.attributes().size();
    for (int i = 0; i < count ;++i)
      args.add(expressionStack.pop());
    for (Attribute attribute : heading.attributes()) {
      Expression expression = args.removeLast();
      components.add(expressionFactory.createTupleComponent(attribute.name(), expression));
    }
    expressionStack.push(expressionFactory.createTupleSelectorInvocation(components));
    return this;
  }


  public ExpressionBuilder relation() {
    return relation(expressionStack.size());
  }

  public ExpressionBuilder relation(int howmany){
    return relation(howmany,true);
  }

  public ExpressionBuilder relation(int howMany, boolean unify) {
    LinkedList<TupleExpression> tuples = new LinkedList<TupleExpression>();
    for (int i = 0; i < howMany; ++i) {
      Expression expr = expressionStack.pop();
      if (!(expr.type() instanceof TupleType))
        throw new NoDExpressionBuilderException("Trying to build a relation " +
                "selector invocation with a non-tuple expression: " + expr);
      TupleExpression tuple = (TupleExpression) expr;
      tuples.addFirst(tuple);
    }
    expressionStack.push(expressionFactory.createRelationSelectorInvocation(
            tuples.get(0).type().heading(), tuples,unify));
    return this;
  }

  public ExpressionBuilder emptyRelation(RelationType type){
    expressionStack.push(expressionFactory.createRelationSelectorInvocation(
            type.heading(), new ArrayList<TupleExpression>(0)));    
    return this;
  }

  public Expression getExpression() {
    return expressionStack.pop();
  }

  public TupleExpression getTuple() {
    return (TupleExpression) expressionStack.pop();
  }

  public IntExpression getInt() {
    return (IntExpression) expressionStack.pop();
  }


  public ExpressionBuilder clear() {
    expressionStack.clear();
    nameStacks.clear();
    return this;
  }


  public CategoricalExpression getCategorical() {
    return (CategoricalExpression) expressionStack.pop();
  }

  public RelationExpression getRelation() {
    return (RelationExpression) expressionStack.pop();
  }

  public ExpressionBuilder from(String prefix) {
    prefixes.add(prefix);
    Expression expression = pop();
    if (!(expression instanceof RelationExpression))
      throw new NoDExpressionBuilderException("From expressions must be relation expressions!");
    from.add((RelationExpression) expression);
    prefix2rel.put(prefix, (RelationExpression) expression);
    return this;
  }

  private Expression pop(){
    Expression expression = expressionStack.pop();
    if (expression == null)
      throw new NoDExpressionBuilderException("Top of the stack is null!");
    return expression;
  }

  public ExpressionBuilder intAttribute(String prefix, String name) {
    IntType type = typeFactory.intType();
    Attribute attr = typeFactory.createAttribute(name, type);
    RelationExpression relationExpression = prefix2rel.get(prefix);
    if (relationExpression==null)
      throw new RuntimeException("The prefix " + prefix + " has not been defined!");
    Attribute attribute = relationExpression.type().heading().attribute(name);
    if (attribute == null)
      throw new RuntimeException(prefix + " does not have a " + name + " attribute!");
    if (!(attribute.type() instanceof IntType))
      throw new RuntimeException(prefix + ":" + name + " is not an int attribute");
    expressionStack.push(expressionFactory.createIntAttribute(prefix, attr));
    return this;
  }

  public ExpressionBuilder categoricalAttribute(String prefix, String name) {
    Attribute attribute = prefix2rel.get(prefix).type().heading().attribute(name);
    if (attribute == null)
      throw new RuntimeException(prefix + " does not have a " + name + " attribute!");
    if (!(attribute.type() instanceof CategoricalType))
      throw new RuntimeException(prefix + ":" + name + " is not an int attribute");
    expressionStack.push(expressionFactory.createAttribute(prefix, attribute));
    return this;
  }


  public ExpressionBuilder intAttribute(String name) {
    IntType type = typeFactory.intType();
    Attribute attr = typeFactory.createAttribute(name, type);
    expressionStack.push(expressionFactory.createIntAttribute(null, attr));
    return this;
  }



  public ExpressionBuilder doubleAttribute(String prefix, String name) {
    DoubleType type = typeFactory.doubleType();
    Attribute attr = typeFactory.createAttribute(name, type);
    if (prefix2rel.get(prefix).type().heading().attribute(name) == null)
      throw new RuntimeException(prefix + " does not have a " + name + " attribute!");
    expressionStack.push(expressionFactory.createDoubleAttribute(prefix, attr));
    return this;
  }

  public ExpressionBuilder doubleAttribute(String name) {
    DoubleType type = typeFactory.doubleType();
    Attribute attr = typeFactory.createAttribute(name, type);
    expressionStack.push(expressionFactory.createDoubleAttribute(null, attr));
    return this;
  }

  public ExpressionBuilder attribute(String prefix, Attribute attribute) {
    expressionStack.push(expressionFactory.createAttribute(prefix, attribute));
    return this;
  }

  public ExpressionBuilder attribute(Attribute attribute) {
    expressionStack.push(expressionFactory.createAttribute(null, attribute));
    return this;
  }

  public ExpressionBuilder select() {
    Expression expression = expressionStack.peek();
    if (!(expression instanceof TupleExpression))
      throw new NoDExpressionBuilderException("Select expressions must be tuple expressions!");
    select = (TupleExpression) expressionStack.pop();
    return this;
  }

  public ExpressionBuilder insert() {
    Expression expression = expressionStack.peek();
    if (!(expression instanceof RelationExpression))
      throw new NoDExpressionBuilderException("Insert expressions must be relation expressions!");
    insert = (RelationExpression) expressionStack.pop();
    return this;
  }

  public ExpressionBuilder where() {
    Expression expression = expressionStack.peek();
    if (!(expression instanceof BoolExpression))
      throw new NoDExpressionBuilderException("where expressions must be boolean expressions!");
    where = (BoolExpression) expressionStack.pop();
    return this;
  }

  public ExpressionBuilder query() {
    expressionStack.push(expressionFactory.createQuery(prefixes, from, where, select));
    prefixes.clear();
    from.clear();
    prefix2rel.clear();
    select = null;
    where = null;
    return this;
  }

  public ExpressionBuilder queryInsert() {
    expressionStack.push(expressionFactory.createQueryInsert(prefixes, from, where, insert));
    prefixes.clear();
    from.clear();
    prefix2rel.clear();
    select = null;
    insert = null;
    where = null;
    return this;
  }

  public ExpressionBuilder contains() {
    TupleExpression tuple = (TupleExpression) expressionStack.pop();
    RelationExpression relation = (RelationExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createContains(relation, tuple));
    return this;
  }

  public ExpressionBuilder doubleValue(DoubleType type, double value) {
    expressionStack.push(expressionFactory.createDoubleConstant(type, value));
    return this;
  }

  public ExpressionBuilder doubleValue(double value) {
    expressionStack.push(expressionFactory.createDoubleConstant(typeFactory.doubleType(), value));
    return this;
  }

  public BoolExpression getBool() {
    return (BoolExpression) expressionStack.pop();
  }

  public ArrayExpression getArray() {
    return (ArrayExpression) expressionStack.pop();
  }

  public DoubleExpression getDouble() {
    return (DoubleExpression) expressionStack.pop();
  }

  public ExpressionBuilder restrict() {
    BoolExpression where = (BoolExpression) expressionStack.pop();
    RelationExpression relation = (RelationExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createRestrict(relation, where));
    return this;
  }

  public ExpressionBuilder tupleFrom() {
    expressionStack.push(expressionFactory.createTupleFrom((RelationExpression) expressionStack.pop()));
    return this;
  }

  public ExpressionBuilder intArrayElement() {
    IntExpression index = (IntExpression) expressionStack.pop();
    ArrayExpression array = (ArrayExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createIntArrayAccess(array, index));
    return this;
  }

  public ExpressionBuilder doubleArrayElement() {
    IntExpression index = (IntExpression) expressionStack.pop();
    ArrayExpression array = (ArrayExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createDoubleArrayAccess(array, index));
    return this;
  }

  public ExpressionBuilder intExtractComponent(String attributeName) {
    TupleExpression tuple = (TupleExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createIntExtractComponent(tuple, attributeName));
    return this;
  }

  public ExpressionBuilder doubleExtractComponent(String attributeName) {
    TupleExpression tuple = (TupleExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createDoubleExtractComponent(tuple, attributeName));
    return this;
  }

  public ExpressionBuilder bool(boolean b) {
    expressionStack.push(expressionFactory.createBoolConstant(b));
    return this;
  }

  public ExpressionBuilder group(String as, String... attributes) {
    RelationExpression rel = (RelationExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createGroup(rel, Arrays.asList(attributes), as));
    return this;
  }

  public ExpressionBuilder group(String as, List<String> attributes) {
    RelationExpression rel = (RelationExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createGroup(rel, attributes, as));
    return this;
  }

  public ExpressionBuilder indexedSum(String indexAttribute) {
    return indexedSum(indexAttribute,null);
  }

   public ExpressionBuilder indexedSum(String indexAttribute, String scaleAttribute) {
    RelationExpression indexRelation = (RelationExpression) expressionStack.pop();
    ArrayExpression array = (ArrayExpression) expressionStack.pop();
    if (indexRelation == null)
      throw new RuntimeException("for an index sum the index relation must not be null!");
    expressionStack.push(expressionFactory.createIndexedSum(array, indexRelation, indexAttribute, scaleAttribute));
    return this;
  }


  public ExpressionBuilder or(int howmany) {
    return this;
  }

  public ExpressionBuilder not() {
    expressionStack.push(expressionFactory.createNot((BoolExpression) expressionStack.pop()));
    return this;
  }

  public ExpressionBuilder intAdd(int howmany) {
    IntExpression rhs = (IntExpression) expressionStack.pop();
    IntExpression lhs = (IntExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createIntAdd(lhs, rhs));
    return this;
  }

  public ExpressionBuilder intAdd() {
    return intAdd(2);
  }

  public ExpressionBuilder doubleAdd() {
    DoubleExpression rhs = (DoubleExpression) expressionStack.pop();
    DoubleExpression lhs = (DoubleExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createDoubleAdd(lhs, rhs));
    return this;
  }

   public ExpressionBuilder doubleMinus() {
    DoubleExpression rhs = (DoubleExpression) expressionStack.pop();
    DoubleExpression lhs = (DoubleExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createDoubleMinus(lhs, rhs));
    return this;
  }

  public ExpressionBuilder doubleTimes() {
    DoubleExpression rhs = (DoubleExpression) expressionStack.pop();
    DoubleExpression lhs = (DoubleExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createDoubleTimes(lhs, rhs));
    return this;
  }

  public ExpressionBuilder doubleDivide() {
    DoubleExpression rhs = (DoubleExpression) expressionStack.pop();
    DoubleExpression lhs = (DoubleExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createDoubleDivide(lhs, rhs));
    return this;
  }


  public ExpressionBuilder intMinus() {
    IntExpression rhs = (IntExpression) expressionStack.pop();
    IntExpression lhs = (IntExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createIntMinus(lhs, rhs));
    return this;
  }

  public ExpressionBuilder relationMinus() {
    RelationExpression rhs = (RelationExpression) expressionStack.pop();
    RelationExpression lhs = (RelationExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createRelationMinus(lhs, rhs));
    return this;
  }


  public ExpressionBuilder intLEQ() {
    IntExpression rhs = (IntExpression) expressionStack.pop();
    IntExpression lhs = (IntExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createIntLEQ(lhs, rhs));
    return this;
  }

  public ExpressionBuilder intGEQ() {
    IntExpression rhs = (IntExpression) expressionStack.pop();
    IntExpression lhs = (IntExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createIntGEQ(lhs, rhs));
    return this;
  }

  public ExpressionBuilder intLessThan() {
    IntExpression rhs = (IntExpression) expressionStack.pop();
    IntExpression lhs = (IntExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createIntLessThan(lhs, rhs));
    return this;
  }

   public ExpressionBuilder intGreaterThan() {
    IntExpression rhs = (IntExpression) expressionStack.pop();
    IntExpression lhs = (IntExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createIntGreaterThan(lhs, rhs));
    return this;
  }

  public ExpressionBuilder doubleGreaterThan() {
    DoubleExpression rhs = (DoubleExpression) expressionStack.pop();
    DoubleExpression lhs = (DoubleExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createDoubleGreaterThan(lhs, rhs));
    return this;
  }

  public ExpressionBuilder doubleLessThan() {
    DoubleExpression rhs = (DoubleExpression) expressionStack.pop();
    DoubleExpression lhs = (DoubleExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createDoubleLessThan(lhs, rhs));
    return this;
  }


  public ExpressionBuilder doubleLEQ() {
    DoubleExpression rhs = (DoubleExpression) expressionStack.pop();
    DoubleExpression lhs = (DoubleExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createDoubleLEQ(lhs, rhs));
    return this;
  }

  public ExpressionBuilder doubleGEQ() {
    DoubleExpression rhs = (DoubleExpression) expressionStack.pop();
    DoubleExpression lhs = (DoubleExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createDoubleGEQ(lhs, rhs));
    return this;
  }


  public ExpressionBuilder allConstants(CategoricalType ofType) {
    expressionStack.push(expressionFactory.createAllConstants(ofType));
    return this;
  }

  public ExpressionBuilder invokeIntOp(Operator<IntType> op) {
    LinkedList<Expression> args = new LinkedList<Expression>();
    int size = op.args().size();
    for (int i = 0; i < size; ++i) {
      args.addFirst(expressionStack.pop());
    }
    expressionStack.push(expressionFactory.createIntOperatorInv(op, args));
    return this;
  }

  public ExpressionBuilder invokeRelOp(Operator<RelationType> op) {
    LinkedList<Expression> args = new LinkedList<Expression>();
    int size = op.args().size();
    for (int i = 0; i < size; ++i) {
      args.addFirst(expressionStack.pop());
    }
    expressionStack.push(expressionFactory.createRelationOperatorInv(op, args));
    return this;
  }

  public ExpressionBuilder invokeTupleOp(Operator<TupleType> op) {
    LinkedList<Expression> args = new LinkedList<Expression>();
    int size = op.args().size();
    for (int i = 0; i < size; ++i) {
      args.addFirst(expressionStack.pop());
    }
    expressionStack.push(expressionFactory.createTupleOperatorInv(op, args));
    return this;
  }

  public ExpressionBuilder getPut() {
    TupleExpression backoff = (TupleExpression) pop();
    TupleExpression arg = (TupleExpression) pop();
    RelationVariable var = (RelationVariable) pop();
    expressionStack.push(expressionFactory.createGet(var, arg, backoff, true));
    return this;
  }

  public ExpressionBuilder get() {
    TupleExpression backoff = (TupleExpression) pop();
    TupleSelector arg = (TupleSelector) pop();
    RelationVariable var = (RelationVariable) pop();
    expressionStack.push(expressionFactory.createGet(var, arg, backoff, false));
    return this;

  }

  public ExpressionBuilder cycles(String from, String to) {
    RelationExpression graph = (RelationExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createCycles(graph, from, to));
    return this;
  }


  public ExpressionBuilder count() {
    RelationExpression relation = (RelationExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createCount(relation));
    return this;
  }

  public List<Expression> lastExpressions(int howmany) {
    LinkedList<Expression> result = new LinkedList<Expression>();
    for (int i = 0; i < howmany; ++i)
      result.addFirst(expressionStack.pop());
    return result;
  }

  public ExpressionBuilder expressions(List<Expression> expressions) {
    for (Expression expr : expressions)
      expressionStack.push(expr);
    return this;
  }

  public ExpressionBuilder value(Type type, Object ... objs){
    if (type instanceof TupleType) {
      TupleType tupleType = (TupleType) type;
      int index = 0;
      for (Attribute attribute : tupleType.heading().attributes()){
        value(attribute.type(), objs[index++]);
      }
      tuple(tupleType.heading());
    }
    if (type instanceof RelationType) {
      RelationType relationType = (RelationType) type;
      TupleType tupleType = typeFactory.createTupleType(relationType.heading());
      if (objs.length == 1 && objs[0] instanceof Object[])
        objs = (Object[]) objs[0];
      for (Object obj : objs){
        Object[] tuple = (Object[]) obj;
        value(tupleType, tuple);
      }
      if (objs.length == 0) emptyRelation(relationType);
      else relation(objs.length);
    }
    if (type instanceof IntType) {
      integer((IntType)type, (Integer) objs[0]);
    }
    if (type instanceof DoubleType) {
      doubleValue((DoubleType)type, (Double) objs[0]);
    }
    if (type instanceof BoolType) {
      bool((Boolean)objs[0]);
    }
    if (type instanceof CategoricalType) {
      categorical((CategoricalType) type, (String) objs[0]);
    }
    return this;
  }

  public ExpressionBuilder by(String name){
    by.add(name);
    return this;
  }

  public ExpressionBuilder summarizeAs(String name, Summarize.Spec spec){
    ScalarExpression expr = (ScalarExpression) expressionStack.pop();
    as.addFirst(name);
    add.addFirst(expr);
    specs.addFirst(spec);
    return this;
  }

  public ExpressionBuilder summarize(){
    RelationExpression relation = (RelationExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createSummarize(relation, by, specs, as, add));
    by.clear();
    specs.clear();
    as.clear();
    add.clear();
    return this;
  }

  public ExpressionBuilder sparseAdd(String index, String value) {
    RelationExpression rhs = (RelationExpression) expressionStack.pop();
    DoubleExpression scale = (DoubleExpression) expressionStack.pop();
    RelationExpression lhs = (RelationExpression) expressionStack.pop();
    expressionStack.push(expressionFactory.createSparseAdd(lhs, scale, rhs, index, value));
    return this;
  }

  public ExpressionBuilder union(int howmany){
    LinkedList<RelationExpression> arguments = new LinkedList<RelationExpression>();
    for (int i = 0; i < howmany; ++i){
      arguments.addFirst((RelationExpression) pop());
    }
    expressionStack.push(expressionFactory.createUnion(arguments));
    return this;
  }

  public ExpressionBuilder inequality() {
    Expression rhs = pop();
    Expression lhs = pop();
    expressionStack.push(expressionFactory.createInequality(lhs, rhs));
    return this;
  }

  public ExpressionBuilder doubleCast(){
    IntExpression expr = (IntExpression) pop();
    expressionStack.push(expressionFactory.createDoubleCast(expr));
    return this;
  }

  public boolean isEmpty() {
    return expressionStack.isEmpty();
  }

  public ExpressionBuilder bins(int ... bins){
    ArrayList<Integer> list = new ArrayList<Integer>(bins.length);
    for (int bin : bins) list.add(bin);
    IntExpression arg = (IntExpression) pop();
    expressionStack.push(expressionFactory.createIntBins(arg, list));
    return this;
  }

   public ExpressionBuilder bins(List<Integer> bins){
    IntExpression arg = (IntExpression) pop();
    expressionStack.push(expressionFactory.createIntBins(arg, bins));
    return this;
  }

  public ExpressionBuilder collect(String groupAttribute, String indexId, String valueId){
    RelationExpression grouped = (RelationExpression) pop();
    expressionStack.push(expressionFactory.createIndexCollector(grouped, groupAttribute, indexId, valueId));
    return this;
  }


  public int stackSize() {
    return expressionStack.size();
  }
}
