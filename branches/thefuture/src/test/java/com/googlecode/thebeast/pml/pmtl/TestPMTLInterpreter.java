package com.googlecode.thebeast.pml.pmtl;

import com.googlecode.thebeast.pml.PMLClause;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public final class TestPMTLInterpreter {

    @Test
    public void testInterpretCorrectBodySize() {
        Signature signature = SQLSignature.createSignature();
        signature.interpret("type TypeA: a,b,c; predicate pred: TypeA x TypeA;");

        PMTLInterpreter interpreter = new PMTLInterpreter(signature);
        PMLClause clause = interpreter.interpret("pred(X,Y):-pred(Y,X),pred(X,a)").get(0);
        assertEquals(clause.getBody().size(), 2);
    }

    @Test
    public void testInterpretCorrectIndexVariables() {
        Signature signature = SQLSignature.createSignature();
        signature.interpret("type TypeA: a,b,c; predicate pred: TypeA x TypeA;");

        PMTLInterpreter interpreter = new PMTLInterpreter(signature);
        PMLClause clause = interpreter.interpret("pred(+X,Y):-pred(Y,X),pred(X,a)").get(0);
        assertEquals(clause.getIndexVariables().get(0), new Variable("X", signature.getType("TypeA")));
    }

    @Test
    public void testInterpretCorrectScaleVariable() {
        Signature signature = SQLSignature.createSignature();
        signature.interpret("type Type: a,b,c; predicate pred: Double x Type;");

        PMTLInterpreter interpreter = new PMTLInterpreter(signature);
        PMLClause clause = interpreter.interpret("pred(#X,Y):-pred(X,a)").get(0);
        assertEquals(clause.getScaleVariable(), new Variable("X", signature.getDoubleType()));
    }

}
