package thebeast.nod.util;

import thebeast.nod.expression.*;
import thebeast.nod.variable.*;

import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class ExpressionPrinter extends AbstractExpressionVisitor {

  private PrintStream os;

  public ExpressionPrinter(PrintStream os) {
    this.os = os;
  }

  public void visitIntConstant(IntConstant constant) {
    os.print(constant.getInt());
  }

  public void visitTupleSelector(TupleSelector tupleSelector) {
    os.print("(");
    int index = 0;
    for (TupleComponent component : tupleSelector.components()) {
      if (index++ > 0) os.print(", ");
      os.print(component.name());
      os.print(" := ");
      component.expression().acceptExpressionVisitor(this);
    }
    os.print(")");
  }

  public void visitRelationSelector(RelationSelector relationSelector) {
    os.print("RELATION ");
    if (relationSelector.tupleExpressions().size() == 0) os.print("-heading-");
    else {
      os.print("{");
      int index = 0;
      for (TupleExpression expr : relationSelector.tupleExpressions()){
        if (index ++ > 0) os.print(", ");
        expr.acceptExpressionVisitor(this);
      }
      os.print("}");
    }

  }

  public void visitJoin(Join join) {

  }

  public void visitQuery(Query query) {
    os.print("SELECT ");
    query.select().acceptExpressionVisitor(this);
    os.print(" FROM ");
    for (int i = 0; i < query.prefixes().size(); ++i){
      if (i > 0) os.print(", ");
      query.relations().get(i).acceptExpressionVisitor(this);
      os.print(" " + query.prefixes().get(i));
    }
    if (query.where() != null){
      os.print(" WHERE ");
      query.where().acceptExpressionVisitor(this);
    }
  }

  public void visitQueryInsert(QueryInsert query) {
    os.print("INSERT ");
    query.insert().acceptExpressionVisitor(this);
    os.print(" FROM ");
    for (int i = 0; i < query.prefixes().size(); ++i){
      if (i > 0) os.print(", ");
      query.relations().get(i).acceptExpressionVisitor(this);
      os.print(" " + query.prefixes().get(i));
    }
    if (query.where() != null){
      os.print(" WHERE ");
      query.where().acceptExpressionVisitor(this);
    }

  }

  public void visitEquality(Equality equality) {
    os.print("(");
    equality.leftHandSide().acceptExpressionVisitor(this);
    os.print(" == ");
    equality.rightHandSide().acceptExpressionVisitor(this);
    os.print(")");
  }

  public void visitInequality(Inequality inequality) {
    os.print("(");
    inequality.leftHandSide().acceptExpressionVisitor(this);
    os.print(" != ");
    inequality.rightHandSide().acceptExpressionVisitor(this);
    os.print(")");
  }

  public void visitAnd(And and) {
    int index = 0;
    for (BoolExpression arg : and.arguments()){
      if (index++ > 0) os.print(" & ");
      arg.acceptExpressionVisitor(this);
    }
  }


  public void visitBoolConstant(BoolConstant boolConstant) {
    os.print(boolConstant.getBoolean());
  }


  public void visitBoolVariable(BoolVariable boolVariable) {
    os.print(boolVariable.label());
  }


  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {
    os.print("\"" + categoricalConstant.representation()+ "\"");
  }

  public void visitArrayCreator(ArrayCreator arrayCreator) {
    os.print("[");
    int index = 0;
    for (Expression arg : arrayCreator.elements()) {
      if (index ++ > 0) os.print(", ");
      arg.acceptExpressionVisitor(this);
    }
    os.print("]");
  }


  public void visitCategoricalVariable(CategoricalVariable categoricalVariable) {
    os.print(categoricalVariable.label());
  }


  public void visitContains(Contains contains) {
    contains.relation().acceptExpressionVisitor(this);
    os.print(" CONTAINS ");
    contains.tuple().acceptExpressionVisitor(this);
  }


  public void visitGroup(Group group) {
    os.print("GROUP ");
    group.relation().acceptExpressionVisitor(this);
    os.print(" BY (");
    int index = 0;
    for (String att : group.attributes()){
      if (index ++ > 0) os.print(", ");
      os.print(att);
    }
    os.print(")");
  }


  public void visitIntPostIncrement(IntPostIncrement intPostIncrement) {
    intPostIncrement.variable().acceptExpressionVisitor(this);
    os.print("++");
  }


  public void visitIntExtractComponent(IntExtractComponent intExtractComponent) {
    os.print("FROM ");
    intExtractComponent.tuple().acceptExpressionVisitor(this);
    os.print(" EXTRACT " + intExtractComponent.name());
  }


  public void visitIndexedSum(IndexedSum indexedSum) {
    os.print("SUM ");
    indexedSum.array().acceptExpressionVisitor(this);
    os.print(" OVER " + indexedSum.indexAttribute() + " OF ");
    indexedSum.indexRelation().acceptExpressionVisitor(this);
  }

  public void visitDoubleAdd(DoubleAdd doubleAdd) {
    os.print("(");
    doubleAdd.leftHandSide().acceptExpressionVisitor(this);
    os.print(" + ");
    doubleAdd.rightHandSide().acceptExpressionVisitor(this);
    os.print(")");

  }

  public void visitDoubleMinus(DoubleMinus doubleMinus) {
    os.print("(");
    doubleMinus.leftHandSide().acceptExpressionVisitor(this);
    os.print(" + ");
    doubleMinus.rightHandSide().acceptExpressionVisitor(this);
    os.print(")");

  }

  public void visitDoubleTimes(DoubleTimes doubleTimes) {
    os.print("(");
    doubleTimes.leftHandSide().acceptExpressionVisitor(this);
    os.print(" * ");
    doubleTimes.rightHandSide().acceptExpressionVisitor(this);
    os.print(")");
  }

  public void visitDoubleDivide(DoubleDivide doubleDivide) {
    os.print("(");
    doubleDivide.leftHandSide().acceptExpressionVisitor(this);
    os.print(" / ");
    doubleDivide.rightHandSide().acceptExpressionVisitor(this);
    os.print(")");
  }


  public void visitDoubleCast(DoubleCast doubleCast) {
    os.print("(double)");
    doubleCast.intExpression().acceptExpressionVisitor(this);
  }

  public void visitTupleFrom(TupleFrom tupleFrom) {
    os.print("TUPLE FROM ");
    tupleFrom.relation().acceptExpressionVisitor(this);
  }


  public void visitIntAttribute(IntAttribute intAttribute) {
    if (intAttribute.prefix() != null)
      os.print(intAttribute.prefix());
    os.print(":");
    os.print(intAttribute.attribute().name());
  }

  public void visitCategoricalAttribute(CategoricalAttribute categoricalAttribute) {
    if (categoricalAttribute.prefix() != null)
      os.print(categoricalAttribute.prefix());
    os.print(":");
    os.print(categoricalAttribute.attribute().name());
  }

  public void visitIntAdd(IntAdd intAdd) {
    os.print("(");
    intAdd.leftHandSide().acceptExpressionVisitor(this);
    os.print(" + ");
    intAdd.rightHandSide().acceptExpressionVisitor(this);
    os.print(")");
  }

  public void visitIntMinus(IntMinus intMinus) {
    os.print("(");
    intMinus.leftHandSide().acceptExpressionVisitor(this);
    os.print(" + ");
    intMinus.rightHandSide().acceptExpressionVisitor(this);
    os.print(")");
  }

  public void visitArrayVariable(ArrayVariable arrayVariable) {
    os.print(arrayVariable.label());
  }

  public void visitDoubleConstant(DoubleConstant doubleConstant) {
    os.print(doubleConstant.getDouble());
  }

  public void visitDoubleVariable(DoubleVariable doubleVariable) {
    os.print(doubleVariable.label());
  }

  public void visitIntVariable(IntVariable intVariable) {
    os.print(intVariable.label());
  }

  public void visitRelationVariable(RelationVariable relationVariable) {
    os.print(relationVariable.label());
  }

  public void visitIntArrayAccess(IntArrayAccess arrayAccess) {
    arrayAccess.array().acceptExpressionVisitor(this);
    os.print("[");
    arrayAccess.index().acceptExpressionVisitor(this);
    os.print("]");
  }

  public void visitDoubleArrayAccess(DoubleArrayAccess doubleArrayAccess) {
    doubleArrayAccess.array().acceptExpressionVisitor(this);
    os.print("[");
    doubleArrayAccess.index().acceptExpressionVisitor(this);
    os.print("]");
  }

  public void visitTupleVariable(TupleVariable tupleVariable) {
    os.print(tupleVariable.label());
  }

  public void visitRestrict(Restrict restrict) {
    os.print("RESTRICT ");
    restrict.relation().acceptExpressionVisitor(this);
    os.print(" WHERE ");
    restrict.where().acceptExpressionVisitor(this);
  }

  public void visitIntLEQ(IntLEQ intLEQ) {
    intLEQ.leftHandSide().acceptExpressionVisitor(this);
    os.print(" <= ");
    intLEQ.rightHandSide().acceptExpressionVisitor(this);
  }

  public void visitIntGEQ(IntGEQ intGEQ) {
    intGEQ.leftHandSide().acceptExpressionVisitor(this);
    os.print(" >= ");
    intGEQ.rightHandSide().acceptExpressionVisitor(this);
  }

  public void visitIntLessThan(IntLessThan intLessThan) {
    intLessThan.leftHandSide().acceptExpressionVisitor(this);
    os.print(" < ");
    intLessThan.rightHandSide().acceptExpressionVisitor(this);
  }

  public void visitIntGreaterThan(IntGreaterThan intGreaterThan) {
    intGreaterThan.leftHandSide().acceptExpressionVisitor(this);
    os.print(" > ");
    intGreaterThan.rightHandSide().acceptExpressionVisitor(this);

  }

  public void visitDoubleLEQ(DoubleLEQ doubleLEQ) {
    doubleLEQ.leftHandSide().acceptExpressionVisitor(this);
    os.print(" <= ");
    doubleLEQ.rightHandSide().acceptExpressionVisitor(this);
  }

  public void visitDoubleGEQ(DoubleGEQ doubleGEQ) {
    doubleGEQ.leftHandSide().acceptExpressionVisitor(this);
    os.print(" <= ");
    doubleGEQ.rightHandSide().acceptExpressionVisitor(this);
  }

  public void visitRelationAttribute(RelationAttribute relationAttribute) {
    if (relationAttribute.prefix() != null)
      os.print(relationAttribute.prefix());
    os.print(":");
    os.print(relationAttribute.attribute().name());
  }

  public void visitDoubleExtractComponent(DoubleExtractComponent doubleExtractComponent) {
    os.print("FROM ");
    doubleExtractComponent.tuple().acceptExpressionVisitor(this);
    os.print(" EXTRACT " + doubleExtractComponent.name());
  }

  public void visitDoubleAttribute(DoubleAttribute doubleAttribute) {
    if (doubleAttribute.prefix() != null)
      os.print(doubleAttribute.prefix());
    os.print(":");
    os.print(doubleAttribute.attribute().name());    
  }

  public void visitDoubleGreaterThan(DoubleGreaterThan doubleGreaterThan) {
    doubleGreaterThan.leftHandSide().acceptExpressionVisitor(this);
    os.print(" > ");
    doubleGreaterThan.rightHandSide().acceptExpressionVisitor(this);
  }

  public void visitDoubleLessThan(DoubleLessThan doubleLessThan) {
    doubleLessThan.leftHandSide().acceptExpressionVisitor(this);
    os.print(" < ");
    doubleLessThan.rightHandSide().acceptExpressionVisitor(this);

  }

  public void visitNot(Not not) {
    os.print("NOT ");
    not.expression().acceptExpressionVisitor(this);
  }

  public void visitAllConstants(AllConstants allConstants) {
    os.print("ALL OF " + allConstants.ofType().name().name());
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

  public void visitGet(Get get) {
    get.relation().acceptExpressionVisitor(this);
    os.print("[");
    get.argument().acceptExpressionVisitor(this);
    os.print("|");
    get.backoff().acceptExpressionVisitor(this);
    os.print("]");
  }

  public void visitCycles(Cycles cycles) {
    os.print("CYCLES IN ");
    os.print("(" + cycles.fromAttribute() + "," + cycles.toAttribute() + ")");
    os.print(" OF ");
    cycles.graph().acceptExpressionVisitor(this);

  }

  public void visitCount(Count count) {
    os.print("COUNT ");
    count.relation().acceptExpressionVisitor(this);

  }

  public void visitRelationMinus(RelationMinus relationMinus) {
    relationMinus.leftHandSide().acceptExpressionVisitor(this);
    os.print(" MINUS ");
    relationMinus.rightHandSide().acceptExpressionVisitor(this);
  }

  public void visitSparseAdd(SparseAdd sparseAdd) {
    sparseAdd.leftHandSide().acceptExpressionVisitor(this);
    os.print(" SPARSE PLUS ");
    sparseAdd.scale().acceptExpressionVisitor(this);
    os.print(" TIMES ");
    sparseAdd.rightHandSide().acceptExpressionVisitor(this);

  }

  public void visitSummarize(Summarize summarize) {
    os.print("SUMMARIZE ");
    summarize.relation().acceptExpressionVisitor(this);
    os.print(" BY ");
    int index = 0;
    for (String name : summarize.by()){
      if (index ++ > 0) os.print(", ");
      os.print(name);
    }
    for (int i = 0; i < summarize.add().size();++i) {
      os.print(" ");
      os.print(summarize.specs().get(i).name());
      os.print(" ");
      summarize.add().get(i).acceptExpressionVisitor(this);
      os.print(" AS " + summarize.as().get(i));
    }
  }

  public void visitUnion(Union union) {
    int index = 0;
    os.print("UNION ");
    for (RelationExpression rel : union.arguments()){
      if (index++ > 0) os.print(", ");
      rel.acceptExpressionVisitor(this);
    }
  }

  public void visitIntBin(IntBins intBins) {
    os.print("[");
    int index = 0;
    for (int bin : intBins.bins()){
      if (index++>0) os.print(", ");
      os.print(bin);
    }
    os.print("](");
    intBins.argument().acceptExpressionVisitor(this);
    os.print(")");
  }

  public void visitIndexCollector(IndexCollector indexCollector) {
    os.print("COLLECT " + indexCollector.groupAttribute() + " FROM ");
    indexCollector.grouped().acceptExpressionVisitor(this);
  }

  public void visitOperatorInvocation(OperatorInvocation operatorInvocation){
    os.print(operatorInvocation.operator().name());
    os.print("(");
    int index = 0;
    for (Object obj : operatorInvocation.args()){
      Expression expr  = (Expression) obj;
      if (index ++ > 0) os.print(", ");
      expr.acceptExpressionVisitor(this);

    }
    os.print(")");
  }

}
