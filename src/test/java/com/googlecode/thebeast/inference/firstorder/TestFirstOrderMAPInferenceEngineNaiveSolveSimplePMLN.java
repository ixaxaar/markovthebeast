package com.googlecode.thebeast.inference.firstorder;

import com.googlecode.thebeast.inference.propositional.ExhaustivePropositionalMAPInferenceEngine;
import com.googlecode.thebeast.pml.*;
import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.world.Constant;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.UserType;
import com.googlecode.thebeast.world.World;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestFirstOrderMAPInferenceEngineNaiveSolveSimplePMLN {

    private NaiveFirstOrderMAPInferenceEngine inferenceEngine;
    private UserPredicate friends;
    private UserType person;

    @BeforeMethod
    public void setUp() {
        Signature signature = SQLSignature.createSignature();
        person = signature.createType("Person", false, "Peter", "Anna", "Sebastian");
        friends = signature.createPredicate("friends", person, person);

//        ClauseBuilder builder = new ClauseBuilder(QueryFactory.getInstance(), signature);
        PMLFormula localClause = null;//builder.head(friends, "x", "y").clause("x", "y");
        PMLFormula reflexity = null;//builder.atom(friends, "x", "y").body().head(friends, "y", "x").clause();

        PseudoMarkovLogicNetwork pmln = new PseudoMarkovLogicNetwork();
        pmln.addFormula(localClause);
        pmln.addFormula(reflexity);

        PMLVector weights = new PMLVector();
        weights.setValue(localClause, "x/Peter y/Anna", 20.0);
        weights.setValue(localClause, "x/Anna y/Peter", -1.0);
        weights.setValue(localClause, "x/Sebastian y/Peter", 20.0);
        weights.setValue(localClause, "x/Peter y/Sebastian", -1.0);
        weights.setValue(localClause, "x/Sebastian y/Anna", -2.0);

        weights.setValue(reflexity, 10.0);

        World observation = signature.createWorld();
        observation.getRelation(friends).setOpen(true);

        inferenceEngine = new NaiveFirstOrderMAPInferenceEngine(new ExhaustivePropositionalMAPInferenceEngine());
        inferenceEngine.setPseudoMarkovLogicNetwork(pmln);
        inferenceEngine.setWeights(weights);
        inferenceEngine.setObservation(observation);
    }

    @Test
    public void testSolveResultIsNotNull() {
        World result = inferenceEngine.infer().getWorld();
        assertNotNull(result, "The inference engine should not return null.");
    }

    @Test
    public void testSolveResultReflexityHolds() {
        World result = inferenceEngine.infer().getWorld();
        for (Constant p1 : person)
            for (Constant p2 : person)
                assertTrue(!result.containsGroundAtom(friends, p1, p2)
                    || result.containsGroundAtom(friends, p2, p1),
                    "Reflexity does not hold for " + p1 + " and " + p2);
    }

    @Test
    public void testSolveResultContainsAtomWithHighLocalWeight() {
        World result = inferenceEngine.infer().getWorld();
        assertTrue(result.containsGroundAtom(friends, "Peter", "Anna"),
            "Result should contain (Peter,Anna) because we gave it high weight");

    }

    @Test
    public void testSolveResultDoesNotContainAtomWithLowLocalWeightAndNoInversePairWithHighWeight() {
        World result = inferenceEngine.infer().getWorld();
        assertTrue(!result.containsGroundAtom(friends, "Sebastian", "Anna"),
            "Result should not contain (Sebastian,Anna) because we gave it a very low weight " +
                "and the inverse pair has zero local weight---hence the reflexity clause  has" +
                "no effect.");

    }


}
