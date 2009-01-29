package com.googlecode.thebeast.inference.propositional;

import com.googlecode.thebeast.pml.*;
import com.googlecode.thebeast.query.Substitution;
import com.googlecode.thebeast.world.IntegerType;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestPropositionalMAPInferenceEngineExhaustiveSolveSimpleNetwork {
    private ExhaustivePropositionalMAPInferenceEngine solver;
    private SocialNetworkGroundMarkovNetworkFixture fixture;
    private UserPredicate friends;
    private GroundAtomAssignment emptyObserved;
    private FeatureIndex index;

    @BeforeMethod
    public void setUp() {

        fixture = new SocialNetworkGroundMarkovNetworkFixture(
            SQLSignature.createSignature());

        fixture.groundFriendsPeterAnnaImpliesFriendsAnnaPeter();
        fixture.groundLocalPeterAnnaAreFriendsClause();

        index = new FeatureIndex(Substitution.createSubstitution(
            fixture.signature, "i/0"));

        PMLVector weights = new PMLVector();
        weights.setValue(fixture.symmetryClause, index, 1.0);
        weights.setValue(fixture.localClause, index, 2.0);
        solver = new ExhaustivePropositionalMAPInferenceEngine(fixture.groundFactorGraph, weights);
        friends = fixture.socialNetworkSignatureFixture.friends;
        emptyObserved = new GroundAtomAssignment(fixture.groundFactorGraph);
        solver.setObservation(emptyObserved);
    }

    @Test
    public void testSolveResultIsNotNull() {
        GroundAtomAssignment result = solver.infer().getAssignment();
        assertNotNull(result, "solver returned a null result.");
    }


    @Test
    public void testSolveResultIsConsistentWithStaticPredicates() {
        GroundAtomAssignment result = solver.infer().getAssignment();
        IntegerType intType = fixture.signature.getIntegerType();
        assertEquals(result.getValue(intType.getEquals(), 0, 0), true);
    }

    @Test
    public void testSolverPerformsCorrectNumberOfEvaluations() {
        solver.infer();
        assertEquals(solver.getEvaluationCount(), 4, "Solver has to do 4 " +
            "iterations because there are 4 possible states");
    }


    @Test
    public void testSolveResultAssignsCorrectValuesToUserPredicates() {
        GroundAtomAssignment result = solver.infer().getAssignment();
        assertEquals(result.getValue(friends, "Peter", "Anna"), 1.0);
    }

}
