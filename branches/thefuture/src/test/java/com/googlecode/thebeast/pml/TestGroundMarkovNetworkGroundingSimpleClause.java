package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.world.IntegerType;
import com.googlecode.thebeast.world.SocialNetworkSignatureFixture;
import com.googlecode.thebeast.world.Tuple;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class TestGroundMarkovNetworkGroundingSimpleClause {
    private GroundFactorGraph groundFactorGraph;
    private SocialNetworkSignatureFixture signatureFixture;
    private PMLFormula formula;
    private List<NestedSubstitution> substitutions;


    @BeforeMethod
    public void setUp() {

        groundFactorGraph = new GroundFactorGraph();
        signatureFixture = new SocialNetworkSignatureFixture(SQLSignature.createSignature());
        formula = PMLFormula.createFormula(signatureFixture.signature,
            "friends(x,y) ^ integerEquals(+i,0) ^ doubleEquals(#s,1.0) => friends(y,x)");
        substitutions = NestedSubstitution.createNestedSubstitutions(signatureFixture.signature,
            "x/Peter y/Anna i/0 s/1.0",
            "x/Peter y/Anna i/1 s/1.0");
    }

    @Test
    public void testGroundCreatesFactorWithOriginalClause() {
        List<GroundFormulaFactor> factors = groundFactorGraph.ground(formula,
            substitutions);

        GroundFormulaFactor factor = factors.get(0);
        assertEquals(formula, factor.getPmlFormula());
    }

    @Test
    public void testGroundCreatesFactorWithRightNumberOfNodes() {
        List<GroundFormulaFactor> factors = groundFactorGraph.ground(formula,
            substitutions);

        GroundFormulaFactor factor = factors.get(0);
        assertEquals(4, factor.getNodes().size());
    }

    @Test
    public void testGroundCreatesRightNumberOfFactors() {
        List<GroundFormulaFactor> factors = groundFactorGraph.ground(formula,
            substitutions);
        assertEquals(2, factors.size());
    }

    @Test
    public void testGroundCreatesNodeThatCanBeAccessedByAnAtomSpec() {
        groundFactorGraph.ground(formula, substitutions);
        IntegerType intType = signatureFixture.signature.getIntegerType();
        assertNotNull(groundFactorGraph.getNode(intType.getEquals(),
            new Tuple(intType.getEquals(), 0, 0)));
    }


}
