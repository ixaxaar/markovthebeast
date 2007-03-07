package thebeast.nodmem.expression;

import thebeast.nod.expression.*;
import thebeast.nod.identifier.Identifier;
import thebeast.nod.type.*;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.Variable;
import thebeast.nodmem.type.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemExpressionFactory implements ExpressionFactory {
  public TupleSelector createTupleSelectorInvocation(List<TupleComponent> components) {
    return new MemTupleSelector(new MemTupleType(new MemHeading(components, false)), components);
  }

  public RelationSelector createRelationSelectorInvocation(Heading heading, List<TupleExpression> tuples) {
    return new MemRelationSelector(heading, tuples);
  }

  public VariableReference<RelationType> createRelationVariableReference(RelationType type, Identifier name) {
    return new MemVariableReference<RelationType>(type, name);
  }

  public CategoricalConstant createCategoricalConstant(CategoricalType type, String represenation) {
    return new MemCategoricalConstant(type, represenation);
  }

  public IntConstant createIntConstant(IntType type, int value) {
    return new MemIntConstant(type, value);
  }

  public Contains createContains(RelationExpression relationExpression, TupleExpression tupleExpression) {
    return new MemContains(relationExpression, tupleExpression);
  }

  public DoubleConstant createDoubleConstant(DoubleType type, double value) {
    return new MemDoubleConstant(type,value);
  }

  public And createAnd(List<BoolExpression> args) {
    return new MemAnd(args);
  }

  public TupleFrom createTupleFrom(RelationExpression relationExpression) {
    return new MemTupleFrom(relationExpression);
  }

  public IntArrayAccess createIntArrayAccess(ArrayExpression array, IntExpression index) {
    return new MemIntArrayAccess(array, index);
  }

  public DoubleArrayAccess createDoubleArrayAccess(ArrayExpression array, IntExpression index) {
    return new MemDoubleArrayAccess(array, index);
  }

  public IntExtractComponent createIntExtractComponent(TupleExpression tuple, String attributeName) {
    return new MemIntExtractComponent(tuple, attributeName);
  }

  public DoubleExtractComponent createDoubleExtractComponent(TupleExpression tuple, String attributeName) {
    return new MemDoubleExtractComponent(tuple, attributeName);
  }

  public Restrict createRestrict(RelationExpression relation, BoolExpression where) {
    return new MemRestrict(relation, where);
  }

  public BoolConstant createBoolConstant(boolean value) {
    return new MemBoolConstant(MemBoolType.BOOL, value);
  }

  public Group createGroup(RelationExpression relation, List<String> attributes, String as) {
    return new MemGroup(relation,attributes, as);
  }

  public IndexedSum createIndexedSum(ArrayExpression array, RelationExpression indexRelation, String indexAttribute) {
    return new MemIndexedSum(array,indexRelation, indexAttribute, null);
  }

  public IndexedSum createIndexedSum(ArrayExpression array, RelationExpression indexRelation,
                                     String indexAttribute, String scaleAttribute) {
      return new MemIndexedSum(array,indexRelation, indexAttribute, scaleAttribute);
    }

  public DoubleCast createDoubleCast(IntExpression expr) {
    return new MemDoubleCast(MemDoubleType.DOUBLE, expr);
  }

  public IntBins createIntBins(IntExpression argument, List<Integer> bins) {
    return new MemIntBins(MemIntType.INT, argument, bins);
  }

  public IndexCollector createIndexCollector(RelationExpression relation, String groupAttribute, String indexAttribute, String valueAttribute) {
    return new MemIndexCollector(groupAttribute, relation, indexAttribute, valueAttribute);
  }

  public IntGEQ createIntGEQ(IntExpression lhs, IntExpression rhs) {
    return new MemIntGEQ(lhs, rhs);
  }


  public IntMinus createIntMinus(IntExpression leftHandSide, IntExpression rightHandSide) {
    return new MemIntMinus(MemIntType.INT, leftHandSide, rightHandSide);
  }

  public IntLEQ createIntLEQ(IntExpression leftHandSide, IntExpression rightHandSide) {
    return new MemIntLEQ(leftHandSide, rightHandSide);
  }

  public IntLessThan createIntLessThan(IntExpression leftHandSide, IntExpression rightHandSide) {
    return new MemIntLessThan(leftHandSide, rightHandSide);
  }

  public IntGreaterThan createIntGreaterThan(IntExpression leftHandSide, IntExpression rightHandSide) {
    return new MemIntGreaterThan(leftHandSide, rightHandSide);
  }

  public RelationMinus createRelationMinus(RelationExpression leftHandSide, RelationExpression rightHandSide) {
    if (!leftHandSide.type().equals(rightHandSide.type()))
      throw new RuntimeException("For a relation minus operation both operands must have the same type " +
              "but here we have " + leftHandSide.type() + " vs " + rightHandSide.type());
    return new MemRelationMinus(leftHandSide.type(), leftHandSide, rightHandSide);
  }

  public DoubleGreaterThan createDoubleGreaterThan(DoubleExpression leftHandSide, DoubleExpression rightHandSide) {
    return new MemDoubleGreaterThan(leftHandSide, rightHandSide);
  }

  public DoubleLessThan createDoubleLessThan(DoubleExpression leftHandSide, DoubleExpression rightHandSide) {
    return new MemDoubleLessThan(leftHandSide, rightHandSide);
  }

  public DoubleLEQ createDoubleLEQ(DoubleExpression leftHandSide, DoubleExpression rightHandSide) {
    return new MemDoubleLEQ(leftHandSide, rightHandSide);
  }

  public DoubleGEQ createDoubleGEQ(DoubleExpression leftHandSide, DoubleExpression rightHandSide) {
    return new MemDoubleGEQ(leftHandSide, rightHandSide);
  }

  public Not createNot(BoolExpression expression) {
    return new MemNot(expression);
  }

  public AllConstants createAllConstants(CategoricalType ofType) {
    return new MemAllConstants(ofType);
  }

  public Get createGet(RelationVariable relation, TupleExpression argument, TupleExpression backoff, boolean put) {
    return new MemGet(relation, argument, backoff, put);
  }

  public Cycles createCycles(RelationExpression graph, String from, String to) {
    return new MemCycles(graph, from, to);
  }

  public <R extends Type> Operator<R> createOperator(String name, List<? extends Variable> args, Expression<R> result) {
    return new MemOperator<R>(name, result, args);
  }

  public <R extends Type> Operator<R> createOperator(String name, Expression<R> result, Variable... args) {
    return createOperator(name, Arrays.asList(args),result);
  }

  public Count createCount(RelationExpression relation) {
    return new MemCount(relation);
  }

  public SparseAdd createSparseAdd(RelationExpression lhs, DoubleExpression scale, RelationExpression rhs,
                                   String indexAttribute, String valueAttribute) {
    return new MemSparseAdd(rhs.type(),lhs, rhs, scale, indexAttribute, valueAttribute);
  }

  public Summarize createSummarize(RelationExpression relation, List<String> by, List<Summarize.Spec> specs, List<String> as, List<ScalarExpression> add) {
    return new MemSummarize(relation, by, add, specs, as);
  }

  public IntOperatorInvocation createIntOperatorInv(Operator<IntType> operator, List<Expression> args) {
    return new MemIntOperatorInvocation(operator.result().type(), operator, args);
  }

  public RelationOperatorInvocation createRelationOperatorInv(Operator<RelationType> operator, List<Expression> args) {
    return new MemRelationOperatorInvocation(operator.result().type(), operator, args);
  }

  public TupleOperatorInvocation createTupleOperatorInv(Operator<TupleType> operator, List<Expression> args) {
    return new MemTupleOperatorInvocation(operator.result().type(), operator, args);
  }

  public IntAdd createIntAdd(IntExpression leftHandSide, IntExpression rightHandSide) {
    return new MemIntAdd(MemIntType.INT, leftHandSide, rightHandSide);
  }

  public DoubleAdd createDoubleAdd(DoubleExpression leftHandSide, DoubleExpression rightHandSide) {
    return new MemDoubleAdd(MemDoubleType.DOUBLE, leftHandSide, rightHandSide);
  }

  public DoubleMinus createDoubleMinus(DoubleExpression leftHandSide, DoubleExpression rightHandSide) {
    return new MemDoubleMinus(MemDoubleType.DOUBLE, leftHandSide, rightHandSide);
  }

  public DoubleTimes createDoubleTimes(DoubleExpression leftHandSide, DoubleExpression rightHandSide) {
    return new MemDoubleTimes(MemDoubleType.DOUBLE, leftHandSide, rightHandSide);
  }

  public TupleComponent createTupleComponent(String name, Expression expression) {
    return new MemTupleComponent(name, expression);
  }

  public Query createQuery(List<String> prefixes, List<? extends RelationExpression> from,
                           BoolExpression where, TupleExpression select) {
    return new MemQuery(new MemRelationType((MemHeading) select.type().heading()), prefixes, from, where, select);
  }

  public QueryInsert createQueryInsert(List<String> prefixes, List<? extends RelationExpression> from, BoolExpression where, RelationExpression insert) {
    return new MemQueryInsert(new MemRelationType((MemHeading) insert.type().heading()), prefixes, from, where, insert);
  }

  public Union createUnion(List<RelationExpression> arguments) {
    return new MemUnion(arguments);
  }

  public Equality createEquality(Expression lhs, Expression rhs) {
    return new MemEquality(lhs,rhs);
  }

  public Inequality createInequality(Expression lhs, Expression rhs) {
    return new MemInequality(lhs, rhs);
  }

  public IntAttribute createIntAttribute(String prefix, Attribute attr) {
    return new MemIntAttribute((IntType) attr.type(), prefix, attr);
  }

  public DoubleAttribute createDoubleAttribute(String prefix, Attribute attr) {
    return new MemDoubleAttribute((DoubleType) attr.type(), prefix, attr);
  }

  public AttributeExpression createAttribute(String prefix, Attribute attr) {    
    if (attr.type() instanceof IntType)
      return new MemIntAttribute((IntType) attr.type(), prefix, attr);
    else if (attr.type() instanceof CategoricalType)
      return new MemCategoricalAttribute((CategoricalType) attr.type(), prefix, attr);
    else if (attr.type() instanceof RelationType)
      return new MemRelationAttribute((RelationType) attr.type(), prefix, attr);
    else if (attr.type() instanceof DoubleType)
      return new MemDoubleAttribute((DoubleType) attr.type(), prefix, attr);
    else return null;
  }

  public ArrayCreator createArrayCreator(List<Expression> elements) {
    return new MemArrayCreator(elements);
  }

  public IntPostIncrement createIntPostIncrement(IntVariable variable) {
    return new MemIntPostIncrement(variable);
  }
}
