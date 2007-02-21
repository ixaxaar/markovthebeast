package thebeast.nod.test;

import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.expression.RelationSelector;

/**
 * @author Sebastian Riedel
 */
public class TestExpressionBuilder extends NoDTest {

    private ExpressionBuilder builder;

    protected void setUp(){
        super.setUp();
        builder = new ExpressionBuilder(server);
    }

    public void testBuildTuple(){
        builder.id("position").integer(positionType,0);
        builder.id("word").categorical(wordType, "the");
        builder.tuple();
        assertEquals(positionID, token0Expr.components().get(0).name());
        assertEquals(zeroConstant, token0Expr.components().get(0).expression());
        assertEquals(2, token0Expr.components().size());
        assertEquals(token0Expr,builder.getExpression());

        builder.clear();
        builder.heading(tokenTupleType.heading());
        builder.asTuple(0,"the");
        assertEquals(token0Expr,builder.getExpression());
    }

    public void testBuildRelationSelector(){
        exprBuilder.expr(token0Expr).expr(token1Expr).expr(token2Expr).expr(token3Expr).expr(token4Expr);
        exprBuilder.relation(5);
        RelationSelector selector = (RelationSelector) exprBuilder.getExpression();
        assertEquals(5,selector.tupleExpressions().size());
        assertEquals(token1Expr,selector.tupleExpressions().get(1));
    }

}
