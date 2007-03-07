package thebeast.nod.expression;

import thebeast.nod.variable.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 19:26:58
 */
public abstract class AbstractExpressionVisitor implements ExpressionVisitor {


  public void visitExpression(Expression expr){

  }

  public void visitVariable(Variable variable){
    visitExpression(variable);
  }

  public void visitConstant(Constant constant){
    visitExpression(constant);
  }

  private void visitArrayAccess(ArrayAccess arrayAccess) {
    visitExpression(arrayAccess);
  }


  public void visitIntConstant(IntConstant constant) {
    visitConstant(constant);
  }

  public void visitJoin(Join join) {
    visitExpression(join);
  }

  public void visitQuery(Query query) {
    visitExpression(query);
  }

  public void visitQueryInsert(QueryInsert queryInsert) {
    visitExpression(queryInsert);
  }

  public void visitBinaryExpression(BinaryExpression binaryExpression){
    visitExpression(binaryExpression);
  }

  public void visitEquality(Equality equality) {
    visitBinaryExpression(equality);
  }

  public void visitInequality(Inequality inequality) {
    visitBinaryExpression(inequality);
  }

  public void visitNAryExpression(NAryExpression nAryExpression){
    visitExpression(nAryExpression);
  }

  public void visitAnd(And and) {
    visitNAryExpression(and);
  }

  public void visitAttribute(AttributeExpression attribute){
    visitExpression(attribute);
  }

  public void visitIntAttribute(IntAttribute intAttribute) {
    visitAttribute(intAttribute);
  }

  public void visitIntVariable(IntVariable intVariable) {
    visitVariable(intVariable);
  }

  public void visitTupleVariable(TupleVariable tupleVariable) {
    visitVariable(tupleVariable);
  }

  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {
    visitConstant(categoricalConstant);
  }

  public void visitCategoricalVariable(CategoricalVariable categoricalVariable) {
    visitVariable(categoricalVariable);
  }

  public void visitRelationVariable(RelationVariable relationVariable) {
    visitVariable(relationVariable);
  }

  public void visitBoolVariable(BoolVariable boolVariable) {
    visitVariable(boolVariable);
  }

  public void visitContains(Contains contains) {
    visitExpression(contains);
  }

  public void visitArrayCreator(ArrayCreator arrayCreator) {
    visitExpression(arrayCreator);
  }

  public void visitArrayVariable(ArrayVariable arrayVariable) {
    visitVariable(arrayVariable);
  }

  public void visitIntPostIncrement(IntPostIncrement intPostIncrement) {
    visitExpression(intPostIncrement);
  }

  public void visitDoubleConstant(DoubleConstant doubleConstant) {
    visitConstant(doubleConstant);
  }

  public void visitDoubleVariable(DoubleVariable doubleVariable) {
    visitVariable(doubleVariable);
  }

  public void visitTupleFrom(TupleFrom tupleFrom) {
    visitExpression(tupleFrom);
  }

  public void visitIntArrayAccess(IntArrayAccess arrayAccess) {
    visitArrayAccess(arrayAccess);
  }


  public void visitDoubleArrayAccess(DoubleArrayAccess doubleArrayAccess) {
    visitArrayAccess(doubleArrayAccess);
  }

  public void visitIntExtractComponent(IntExtractComponent intExtractComponent) {
    visitExpression(intExtractComponent);
  }

  public void visitRestrict(Restrict restrict) {
    visitExpression(restrict);
  }

  public void visitCategoricalAttribute(CategoricalAttribute categoricalAttribute) {
    visitAttribute(categoricalAttribute);
  }

  public void visitBoolConstant(BoolConstant boolConstant) {
    visitConstant(boolConstant);
  }

  public void visitGroup(Group group) {
    visitExpression(group);
  }

  public void visitIndexedSum(IndexedSum indexedSum) {
    visitExpression(indexedSum);
  }

  public void visitDoubleAdd(DoubleAdd doubleAdd) {
    visitBinaryExpression(doubleAdd);
  }

  public void visitDoubleMinus(DoubleMinus doubleMinus) {
    visitBinaryExpression(doubleMinus);
  }

  public void visitDoubleTimes(DoubleTimes doubleTimes) {
    visitBinaryExpression(doubleTimes);
  }

  public void visitDoubleCast(DoubleCast doubleCast) {
    visitExpression(doubleCast);
  }

  public void visitIntAdd(IntAdd intAdd) {
    visitBinaryExpression(intAdd);
  }

  public void visitIntMinus(IntMinus intMinus) {
    visitBinaryExpression(intMinus);
  }

  public void visitIntLEQ(IntLEQ intLEQ) {
    visitBinaryExpression(intLEQ);
  }

  public void visitIntLessThan(IntLessThan intLessThan) {
    visitBinaryExpression(intLessThan);
  }

  public void visitIntGreaterThan(IntGreaterThan intGreaterThan) {
    visitBinaryExpression(intGreaterThan);
  }

  public void visitDoubleLEQ(DoubleLEQ doubleLEQ) {
    visitBinaryExpression(doubleLEQ);
  }

  public void visitDoubleGEQ(DoubleGEQ doubleGEQ) {
    visitBinaryExpression(doubleGEQ);
  }

  public void visitRelationAttribute(RelationAttribute relationAttribute) {
    visitAttribute(relationAttribute);
  }

  public void visitDoubleExtractComponent(DoubleExtractComponent doubleExtractComponent) {
    visitExpression(doubleExtractComponent);
  }

  public void visitDoubleAttribute(DoubleAttribute doubleAttribute) {
    visitAttribute(doubleAttribute);
  }

  public void visitDoubleGreaterThan(DoubleGreaterThan doubleGreaterThan) {
    visitBinaryExpression(doubleGreaterThan);
  }

  public void visitDoubleLessThan(DoubleLessThan doubleLessThan) {
    visitBinaryExpression(doubleLessThan);
  }

  public void visitNot(Not not) {
    visitExpression(not);
  }

  public void visitAllConstants(AllConstants allConstants) {
    visitExpression(allConstants);
  }

  public void visitIntOperatorInvocation(IntOperatorInvocation intOperatorInvocation) {
    visitExpression(intOperatorInvocation);
  }

  public void visitRelationOperatorInvocation(RelationOperatorInvocation relationOperatorInvocation) {
    visitExpression(relationOperatorInvocation);
  }

  public void visitTupleOperatorInvocation(TupleOperatorInvocation tupleOperatorInvocation) {
    visitExpression(tupleOperatorInvocation);
  }

  public void visitGet(Get get) {
    visitExpression(get);
  }

  public void visitCycles(Cycles cycles) {
    visitExpression(cycles);
  }

  public void visitCount(Count count) {
    visitExpression(count);
  }

  public void visitRelationMinus(RelationMinus relationMinus) {
    visitExpression(relationMinus);
  }

  public void visitSparseAdd(SparseAdd sparseAdd) {
    visitExpression(sparseAdd);
  }

  public void visitSummarize(Summarize summarize) {
    visitExpression(summarize);
  }

  public void visitUnion(Union union) {
    visitExpression(union);
  }

  public void visitIntBin(IntBins intBins) {
    visitExpression(intBins);
  }

  public void visitIndexCollector(IndexCollector indexCollector) {
    visitExpression(indexCollector);
  }

  public void visitRelationSelector(RelationSelector relationSelector) {
    visitExpression(relationSelector);
  }

  public void visitTupleSelector(TupleSelector tupleSelector) {
    visitExpression(tupleSelector);
  }
}
