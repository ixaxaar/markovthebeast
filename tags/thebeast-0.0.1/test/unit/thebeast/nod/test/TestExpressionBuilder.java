package thebeast.nod.test;

import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.expression.RelationSelector;

/**
 * @author Sebastian Riedel
 */
public class TestExpressionBuilder extends NoDTest {


    protected void setUp(){
        super.setUp();
    }

  
    public void testBuildRelationSelector(){
        exprBuilder.expr(token0Expr).expr(token1Expr).expr(token2Expr).expr(token3Expr).expr(token4Expr);
        exprBuilder.relation(5);
        RelationSelector selector = (RelationSelector) exprBuilder.getExpression();
        assertEquals(5,selector.tupleExpressions().size());
        assertEquals(token1Expr,selector.tupleExpressions().get(1));
    }

}
