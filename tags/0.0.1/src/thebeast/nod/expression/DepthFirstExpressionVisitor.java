package thebeast.nod.expression;

/**
 * @author Sebastian Riedel
 */
public class DepthFirstExpressionVisitor extends AbstractExpressionVisitor {


  public void visitNAryExpression(NAryExpression nAryExpression) {
    for (Object obj : nAryExpression.arguments())
      ((Expression)obj).acceptExpressionVisitor(this);
  }


  public void visitBinaryExpression(BinaryExpression binaryExpression) {
    binaryExpression.leftHandSide().acceptExpressionVisitor(this);
    binaryExpression.rightHandSide().acceptExpressionVisitor(this);
  }

  public void visitQuery(Query query) {
    for (RelationExpression rel : query.relations())
      rel.acceptExpressionVisitor(this);
    query.where().acceptExpressionVisitor(this);
    query.select().acceptExpressionVisitor(this);
  }

  public void visitIntBin(IntBins intBins) {
    intBins.argument().acceptExpressionVisitor(this);
  }

  public void visitDoubleAbs(DoubleAbs doubleAbs) {
    doubleAbs.argument().acceptExpressionVisitor(this);
  }
}
