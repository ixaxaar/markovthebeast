package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Substitution;
import com.googlecode.thebeast.world.UserConstant;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestGroundMarkovNetworkExtractFeatureVector {
    private SocialNetworkGroundMarkovNetworkFixture fixture;
    private FeatureIndex index;
    private UserPredicate friends;
    private UserConstant peter;
    private UserConstant anna;


    @Test
    public void testExtractedFeatureVectorHasCorrectValueForFalseClause() {

        GroundAtomAssignment assignment = new GroundAtomAssignment(fixture.groundFactorGraph);
        assignment.setValue(true, friends, peter, anna);
        assignment.setValue(false, friends, anna, peter);

        PMLVector featureVector = fixture.groundFactorGraph.extractFeatureVector(assignment);
        assertEquals(featureVector.getValue(fixture.localClause, index), 1.0,
            "If friends(Peter,Anna) then the local feature should be 1.0 but it isn't");
    }

    @Test
    public void testExtractedFeatureVectorHasCorrectValueForTrueClause() {

        GroundAtomAssignment assignment = new GroundAtomAssignment(fixture.groundFactorGraph);
        assignment.setValue(true, friends, peter, anna);
        assignment.setValue(false, friends, anna, peter);

        PMLVector featureVector = fixture.groundFactorGraph.extractFeatureVector(assignment);
        assertEquals(featureVector.getValue(fixture.symmetryClause, index), 0.0,
            "If friends(Peter,Anna) but not friends(Anna,Peter) then " +
                "the symmetry feature should be 0.0 but it isn't");
    }


    @BeforeMethod
    protected void setUp() throws Exception {
        fixture = new SocialNetworkGroundMarkovNetworkFixture(
            SQLSignature.createSignature());
        fixture.groundLocalPeterAnnaAreFriendsClause();
        fixture.groundFriendsPeterAnnaImpliesFriendsAnnaPeter();

        friends = fixture.socialNetworkSignatureFixture.friends;
        peter = fixture.socialNetworkSignatureFixture.peter;
        anna = fixture.socialNetworkSignatureFixture.anna;


        index = new FeatureIndex(Substitution.createSubstitution(
            fixture.signature, "i/0"));

    }
}
