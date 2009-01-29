package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.query.Substitution;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.UserType;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class TestPMLUtilsGetAllSubstitutions {
    private PMLFormula clause;
    private UserType typeA;
    private Signature signature;

    @BeforeMethod
    public void setUp() {
        signature = SQLSignature.createSignature();
        typeA = signature.createType("typeA", false, "A", "B");
        UserPredicate pred = signature.createPredicate("pred", typeA, typeA);

//        ClauseBuilder clauseBuilder = new ClauseBuilder(QueryFactory.getInstance(), signature);
//        clauseBuilder.atom(pred, "x", "y").body().head(pred, "y", "z").atom(pred, "x", "z").restrict().inner("z");
//        clause = clauseBuilder.clause();
    }

    @Test
    public void testGetAllSubstitutionsForClauseRightNumber() {
        List<NestedSubstitution> result = PMLUtils.getAllSubstitutions(clause);
        assertEquals(result.size(), 4, "Not the right number of substitutions in the set of all substitutions.");
    }

    @Test
    public void testGetAllSubstitutionsForClauseRightNumberOfInnerSubstitutions() {
        List<NestedSubstitution> result = PMLUtils.getAllSubstitutions(clause);
        assertEquals(result.get(1).getInnerSubstitutions().size(), 2,
            "Not the right number of inner substitutions for the second outer substitution in the result.");
    }

    @Test
    public void testGetAllSubstitutionsContainsCorrectNestedSubstitution() {
        List<NestedSubstitution> result = PMLUtils.getAllSubstitutions(clause);
        NestedSubstitution expectedMember = NestedSubstitution.createNestedSubstitution(signature,
            "x/A y/A {z/A} {z/B}");
        assertTrue(result.contains(expectedMember), "result must contain " + expectedMember);
    }


    @Test
    public void testGetAllSubstitutionsForListOfVariablesRightNumber() {
        List<Variable> variables = Arrays.asList(new Variable("x", typeA), new Variable("y", typeA));
        List<Substitution> result = PMLUtils.getAllSubstitutions(variables);
        assertEquals(result.size(), 4, "Not the right number of substitutions in the set of all substitutions.");
    }


}
