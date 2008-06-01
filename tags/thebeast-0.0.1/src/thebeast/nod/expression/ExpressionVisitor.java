package thebeast.nod.expression;

import thebeast.nod.variable.*;
import thebeast.nodmem.expression.MemDoubleDivide;
import thebeast.nodmem.expression.MemIntMin;
import thebeast.nodmem.expression.MemIntMax;
import thebeast.nodmem.expression.MemDoubleAbs;

/**
 * @author Sebastian Riedel
 */
public interface ExpressionVisitor {

  void visitIntConstant(IntConstant constant);

  void visitTupleSelector(TupleSelector tupleSelector);

  void visitRelationSelector(RelationSelector relationSelector);

  void visitJoin(Join join);

  void visitQuery(Query query);

  void visitQueryInsert(QueryInsert queryInsert);
  
  void visitEquality(Equality equality);

  void visitInequality(Inequality inequality);

  void visitAnd(And and);

  void visitIntAttribute(IntAttribute intAttribute);

  void visitIntVariable(IntVariable intVariable);

  void visitTupleVariable(TupleVariable tupleVariable);

  void visitCategoricalConstant(CategoricalConstant categoricalConstant);

  void visitCategoricalVariable(CategoricalVariable categoricalVariable);

  void visitRelationVariable(RelationVariable relationVariable);

  void visitBoolVariable(BoolVariable boolVariable);

  void visitContains(Contains contains);

  void visitArrayCreator(ArrayCreator arrayCreator);

  void visitArrayVariable(ArrayVariable arrayVariable);

  void visitIntPostIncrement(IntPostIncrement intPostIncrement);

  void visitDoubleConstant(DoubleConstant doubleConstant);

  void visitDoubleVariable(DoubleVariable doubleVariable);

  void visitTupleFrom(TupleFrom tupleFrom);

  void visitIntArrayAccess(IntArrayAccess arrayAccess);

  void visitDoubleArrayAccess(DoubleArrayAccess doubleArrayAccess);

  void visitIntExtractComponent(IntExtractComponent intExtractComponent);

  void visitRestrict(Restrict restrict);

  void visitCategoricalAttribute(CategoricalAttribute categoricalAttribute);

  void visitBoolConstant(BoolConstant boolConstant);

  void visitGroup(Group group);

  void visitIndexedSum(IndexedSum indexedSum);

  void visitDoubleAdd(DoubleAdd doubleAdd);

  void visitDoubleMinus(DoubleMinus doubleMinus);  

  void visitDoubleTimes(DoubleTimes doubleTimes);

  void visitDoubleDivide(DoubleDivide doubleDivide);  

  void visitDoubleCast(DoubleCast doubleCast);

  void visitIntAdd(IntAdd intAdd);

  void visitIntMin(IntMin intMin);

  void visitIntMax(IntMax intMax);  

  void visitIntMinus(IntMinus intMinus);

  void visitIntLEQ(IntLEQ intLEQ);

  void visitIntGEQ(IntGEQ intGEQ);  

  void visitIntLessThan(IntLessThan intLessThan);

  void visitIntGreaterThan(IntGreaterThan intGreaterThan);  

  void visitDoubleLEQ(DoubleLEQ doubleLEQ);

  void visitDoubleGEQ(DoubleGEQ doubleGEQ);

  void visitRelationAttribute(RelationAttribute relationAttribute);

  void visitDoubleExtractComponent(DoubleExtractComponent doubleExtractComponent);

  void visitDoubleAttribute(DoubleAttribute doubleAttribute);

  void visitDoubleGreaterThan(DoubleGreaterThan doubleGreaterThan);

  void visitDoubleLessThan(DoubleLessThan doubleLessThan);

  void visitNot(Not not);

  void visitAllConstants(AllConstants allConstants);

  void visitIntOperatorInvocation(IntOperatorInvocation intOperatorInvocation);

  void visitRelationOperatorInvocation(RelationOperatorInvocation relationOperatorInvocation);

  void visitTupleOperatorInvocation(TupleOperatorInvocation tupleOperatorInvocation);

  void visitGet(Get get);

  void visitCycles(Cycles cycles);

  void visitCount(Count count);

  void visitRelationMinus(RelationMinus relationMinus);

  void visitSparseAdd(SparseAdd sparseAdd);

  void visitSummarize(Summarize summarize);

  void visitUnion(Union union);

  void visitIntBin(IntBins intBins);

  void visitIndexCollector(IndexCollector indexCollector);

  void visitDoubleAbs(DoubleAbs doubleAbs);
}
