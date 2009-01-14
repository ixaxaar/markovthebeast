package com.googlecode.thebeast.inference.firstorder;

import com.googlecode.thebeast.inference.propositional.ExhaustivePropositionalMAPInferenceEngine;
import com.googlecode.thebeast.pml.ClauseBuilder;
import com.googlecode.thebeast.pml.PMLClause;
import com.googlecode.thebeast.pml.PMLVector;
import com.googlecode.thebeast.pml.PseudoMarkovLogicNetwork;
import com.googlecode.thebeast.query.QueryFactory;
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

    private World observation;
    private PMLVector weights;
    private NaiveFirstOrderMAPInferenceEngine inferenceEngine;
    private UserPredicate friends;

    @BeforeMethod
    public void setUp() {
        Signature signature = SQLSignature.createSignature();
        UserType person = signature.createType("Person", false, "Peter", "Anna", "Sebastian");
        friends = signature.createPredicate("friends", person, person);

        ClauseBuilder builder = new ClauseBuilder(QueryFactory.getInstance(), signature);
        PMLClause localClause = builder.head(friends, "x", "y").clause("x", "y");
        PMLClause reflexity = builder.atom(friends, "x", "y").body().head(friends, "y", "x").clause();

        PseudoMarkovLogicNetwork pmln = new PseudoMarkovLogicNetwork();
        pmln.addClause(localClause);
        pmln.addClause(reflexity);

        PMLVector weights = new PMLVector();
        weights.setValue(localClause, "x/Peter y/Anna", 2.0);
        weights.setValue(localClause, "x/Anna y/Peter", 2.0);
        weights.setValue(localClause, "x/Sebastian y/Peter", 2.0);
        weights.setValue(localClause, "x/Peter y/Sebastian", 2.0);

        weights.setValue(reflexity, 2.0);

        observation = signature.createWorld();

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
    public void testSolveResultIsCorrectForHiddenUserPredicates() {
        World result = inferenceEngine.infer().getWorld();
        System.out.println("relation: " + result.getRelation(friends));
        assertTrue(result.getRelation(friends).
            containsTuple("Anna", "Peter"),
            "The result does not contain a ground atom that should be included.");
    }


}
