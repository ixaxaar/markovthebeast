package com.googlecode.thebeast.pml.pmtl;

import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.sql.SQLSignature;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestPMTLFormulaeBuilder {


    @Test
    public void testBuildFormula(){
        Signature signature = SQLSignature.createSignature();
        signature.interpret("type TypeA: a,b,c; predicate pred: TypeA x TypeA;");

        PMTLFormulaeBuilder builder = new PMTLFormulaeBuilder(signature);

        System.out.println(builder.interpret("pred(X,Y) ^ (pred(X,a) => pred(Y,X))"));
        

    }

}
