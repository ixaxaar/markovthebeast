package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Substitution;
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


    @Test
    public void testExtractedFeatureVectorHasCorrectValueForFalseClause() {

        GroundAtomAssignment assignment = new GroundAtomAssignment(fixture.groundFactorGraph);
        assignment.setValue(true, fixture.socialNetworkSignatureFixture.friends,
            fixture.socialNetworkSignatureFixture.peter, fixture.socialNetworkSignatureFixture.anna);
        assignment.setValue(false, fixture.socialNetworkSignatureFixture.friends,
            fixture.socialNetworkSignatureFixture.anna, fixture.socialNetworkSignatureFixture.peter);

        PMLVector featureVector = fixture.groundFactorGraph.extractFeatureVector(assignment);
        assertEquals(featureVector.getValue(fixture.localClause, index), 1.0,
            "If friends(Peter,Anna) then the local feature should be 1.0 " +
                "but it isn't");
    }

    @Test
    public void testExtractedFeatureVectorHasCorrectValueForTrueClause() {

        GroundAtomAssignment assignment = new GroundAtomAssignment(fixture.groundFactorGraph);
        assignment.setValue(true, fixture.socialNetworkSignatureFixture.friends,
            fixture.socialNetworkSignatureFixture.peter, fixture.socialNetworkSignatureFixture.anna);
        assignment.setValue(false, fixture.socialNetworkSignatureFixture.friends,
            fixture.socialNetworkSignatureFixture.anna, fixture.socialNetworkSignatureFixture.peter);

        PMLVector featureVector = fixture.groundFactorGraph.extractFeatureVector(assignment);
        assertEquals(featureVector.getValue(fixture.symmetryClause, index), 0.0,
            "If friends(Peter,Anna) but not friends(Anna,Peter) then " +
                "the reflective feature should be 0.0 but it isn't");
    }


    @BeforeMethod
    protected void setUp() throws Exception {
        fixture = new SocialNetworkGroundMarkovNetworkFixture(
            SQLSignature.createSignature());
        fixture.groundLocalPeterAnnaAreFriendsClause();
        fixture.groundFriendsPeterAnnaImpliesFriendsAnnaPeter();

        index = new FeatureIndex(Substitution.createSubstitution(
            fixture.signature, "i/0"));

    }
}
