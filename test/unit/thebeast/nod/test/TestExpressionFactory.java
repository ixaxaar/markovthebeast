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

    public void testCreateConstant(){
        CategoricalConstant categoricalConstant =
                expressionFactory.createCategoricalConstant(wordType, "the");
        assertEquals("the",categoricalConstant.representation());
        assertEquals(theConstant,categoricalConstant);

        IntConstant intConstant =
                expressionFactory.createIntConstant(positionType, 0);
        assertEquals(positionType.value(0),intConstant.value());
        assertEquals(zeroConstant,intConstant);
        

    }

    public void testCreateTupleSelectorInvocation(){
        LinkedList<TupleComponent> components = new LinkedList<TupleComponent>();
        TupleComponent c1 = expressionFactory.createTupleComponent("position",zeroConstant);
        components.add(c1);
        TupleComponent c2 = expressionFactory.createTupleComponent("word", theConstant);
        components.add(c2);
        TupleSelector tupleSelector = expressionFactory.createTupleSelectorInvocation(components);
        assertEquals(c1, tupleSelector.components().get(0));
        assertEquals(c2, tupleSelector.components().get(1));
        assertEquals(2, tupleSelector.components().size());
        assertEquals(tokenTupleType, tupleSelector.type());
        assertEquals(token0Expr, tupleSelector);
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
