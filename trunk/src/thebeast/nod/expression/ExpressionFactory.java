package thebeast.nod.expression;

import thebeast.nod.identifier.Identifier;
import thebeast.nod.type.*;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.Variable;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface ExpressionFactory {

  TupleSelector createTupleSelectorInvocation(List<TupleComponent> components);

  RelationSelector createRelationSelectorInvocation(Heading heading, List<TupleExpression> tuples);

  VariableReference<RelationType> createRelationVariableReference(RelationType type, Identifier name);

  CategoricalConstant createCategoricalConstant(CategoricalType type, String representation);

  IntConstant createIntConstant(IntType type, int value);

  Contains createContains(RelationExpression relationExpression, TupleExpression tupleExpression);

  TupleComponent createTupleComponent(String name, Expression expression);

  Query createQuery(List<String> prefixes, List<? extends RelationExpression> from,
                    BoolExpression where, TupleExpression select, boolean unify);

  Query createQuery(List<String> prefixes, List<? extends RelationExpression> from,
                    BoolExpression where, TupleExpression select);

  QueryInsert createQueryInsert(List<String> prefixes, List<? extends RelationExpression> from,
                    BoolExpression where, RelationExpression insert);

  Union createUnion(List<RelationExpression> arguments);

  Equality createEquality(Expression lhs, Expression rhs);

  Inequality createInequality(Expression lhs, Expression rhs);

  IntAttribute createIntAttribute(String prefix, Attribute attr);

  DoubleAttribute createDoubleAttribute(String prefix, Attribute attr);

  AttributeExpression createAttribute(String prefix, Attribute attr);

  ArrayCreator createArrayCreator(List<Expression> elements);

  ArrayCreator createEmptyArray(ArrayType type);  

  IntPostIncrement createIntPostIncrement(IntVariable variable);

  DoubleConstant createDoubleConstant(DoubleType type, double value);

  And createAnd(List<BoolExpression> args);

  TupleFrom createTupleFrom(RelationExpression relationExpression);

  IntArrayAccess createIntArrayAccess(ArrayExpression array, IntExpression index);

  DoubleArrayAccess createDoubleArrayAccess(ArrayExpression array, IntExpression index);

  IntExtractComponent createIntExtractComponent(TupleExpression tuple, String attributeName);

  DoubleExtractComponent createDoubleExtractComponent(TupleExpression tuple, String attributeName);

  Restrict createRestrict(RelationExpression relation, BoolExpression where);

  BoolConstant createBoolConstant(boolean value);

  Group createGroup(RelationExpression relation, List<String> attributes, String as);

  IndexedSum createIndexedSum(ArrayExpression array, RelationExpression indexRelation, String indexAttribute);

  IntAdd createIntAdd(IntExpression leftHandSide, IntExpression rightHandSide);

  IntMin createIntMin(IntExpression leftHandSide, IntExpression rightHandSide);

  IntMax createIntMax(IntExpression leftHandSide, IntExpression rightHandSide);

  DoubleAdd createDoubleAdd(DoubleExpression leftHandSide, DoubleExpression rightHandSide);

  DoubleMinus createDoubleMinus(DoubleExpression leftHandSide, DoubleExpression rightHandSide);

  DoubleTimes createDoubleTimes(DoubleExpression leftHandSide, DoubleExpression rightHandSide);

  DoubleDivide createDoubleDivide(DoubleExpression leftHandSide, DoubleExpression rightHandSide);

  IntMinus createIntMinus(IntExpression leftHandSide, IntExpression rightHandSide);

  IntLEQ createIntLEQ(IntExpression leftHandSide, IntExpression rightHandSide);

  IntLessThan createIntLessThan(IntExpression leftHandSide, IntExpression rightHandSide);

  IntGreaterThan createIntGreaterThan(IntExpression leftHandSide, IntExpression rightHandSide);

  RelationMinus createRelationMinus(RelationExpression leftHandSide, RelationExpression rightHandSide);

  DoubleGreaterThan createDoubleGreaterThan(DoubleExpression leftHandSide, DoubleExpression rightHandSide);

  DoubleLessThan createDoubleLessThan(DoubleExpression leftHandSide, DoubleExpression rightHandSide);


  DoubleLEQ createDoubleLEQ(DoubleExpression leftHandSide, DoubleExpression rightHandSide);

  DoubleGEQ createDoubleGEQ(DoubleExpression leftHandSide, DoubleExpression rightHandSide);

  Not createNot(BoolExpression expression);

  AllConstants createAllConstants(CategoricalType ofType);

  Get createGet(RelationVariable relation, TupleExpression argument, TupleExpression backoff, boolean put);

  Cycles createCycles(RelationExpression graph, String from, String to);

  IntOperatorInvocation createIntOperatorInv(Operator<IntType> operator, List<Expression> args);

  RelationOperatorInvocation createRelationOperatorInv(Operator<RelationType> operator, List<Expression> args);

  TupleOperatorInvocation createTupleOperatorInv(Operator<TupleType> operator, List<Expression> args);

  <R extends Type> Operator<R> createOperator(String name, List<? extends Variable> args, Expression<R> result);

  <R extends Type> Operator<R> createOperator(String name, Expression<R> result, Variable ... args);


  Count createCount(RelationExpression relation);

  SparseAdd createSparseAdd(RelationExpression lhs, DoubleExpression scale, RelationExpression rhs,
                            String indexAttribute, String valueAttribute);
  Summarize createSummarize(RelationExpression relation, List<String> by, List<Summarize.Spec> specs, 
                            List<String> as, List<ScalarExpression> add);


  IndexedSum createIndexedSum(ArrayExpression array, RelationExpression indexRelation,
                                     String indexAttribute, String scaleAttribute);

  DoubleCast createDoubleCast(IntExpression expr);

  IntBins createIntBins(IntExpression argument, List<Integer> bins);

  IndexCollector createIndexCollector(RelationExpression relation,String groupAttribute,
                                      String indexAttribute, String valueAttribute);

  IntGEQ createIntGEQ(IntExpression lhs, IntExpression rhs);

  RelationSelector createRelationSelectorInvocation(Heading heading, List<TupleExpression> tuples, boolean unify);
}

