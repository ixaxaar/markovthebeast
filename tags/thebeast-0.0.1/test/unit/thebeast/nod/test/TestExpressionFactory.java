package thebeast.nod.test;

import thebeast.nod.expression.*;
import thebeast.nod.type.RelationType;

import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public class TestExpressionFactory extends NoDTest {

    public void testCreateVariableReference(){
        VariableReference<RelationType> ref =
                expressionFactory.createRelationVariableReference(tokenRelationType, tokenRelVarID);
        assertEquals(tokenRelVarID,ref.identifier());
        assertEquals(ref.type(),tokenRelationType);
        assertEquals(tokenReference,ref);
    }


    public void testCreateRelationSelectorInvocation(){
        LinkedList<TupleExpression> tuples = new LinkedList<TupleExpression>();
        tuples.add(token0Expr);
        tuples.add(token1Expr);
        tuples.add(token2Expr);
        tuples.add(token3Expr);
        tuples.add(token4Expr);
        RelationSelector selector = expressionFactory.createRelationSelectorInvocation(tokenHeading,tuples);
        tuples.clear();
        assertEquals(tokenHeading, selector.type().heading());
        assertEquals(5,selector.tupleExpressions().size());
        assertEquals(token2Expr, selector.tupleExpressions().get(2));
        

    }

}
