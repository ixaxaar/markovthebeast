package com.googlecode.thebeast.pml.pmtl;

import com.googlecode.thebeast.pml.BinaryOperatorFormula;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestPMTLFormulaeBuilder {

    @Test
    public void testBuildFormulaRightTopLevelFormulaType() {
        Signature signature = SQLSignature.createSignature();
        signature.interpret("type TypeA: a,b,c; predicate pred: TypeA x TypeA;");

        PMTLFormulaeBuilder builder = new PMTLFormulaeBuilder(signature);

        assertTrue(builder.interpret("pred(+X,Y) ^ (pred(X,a) => pred(Y,X))").get(0).getFormula()
            instanceof BinaryOperatorFormula);

    }

    @Test
    public void testBuildFormulaCorrectIndexVariable() {
        Signature signature = SQLSignature.createSignature();
        signature.interpret("type TypeA: a,b,c; predicate pred: TypeA x TypeA;");

        PMTLFormulaeBuilder builder = new PMTLFormulaeBuilder(signature);

        assertEquals(builder.interpret("pred(+X,Y) ^ (pred(X,a) => pred(Y,X))").
            get(0).getIndexVariables().get(0).getName(), "X");


    }
}
