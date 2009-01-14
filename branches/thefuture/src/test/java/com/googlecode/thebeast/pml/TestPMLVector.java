package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Substitution;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestPMLVector {

    private SocialNetworkGroundMarkovNetworkFixture fixture;

    @BeforeMethod
    protected void setUp() throws Exception {
        fixture = new SocialNetworkGroundMarkovNetworkFixture(
            SQLSignature.createSignature());
        fixture.groundLocalPeterAnnaAreFriendsClause();
        fixture.groundFriendsPeterAnnaImpliesFriendsAnnaPeter();
    }

    @Test
    public void testSetAndGetWithIdenticalIndicesButDifferentObjects() {
        FeatureIndex index1 = new FeatureIndex(Substitution.createSubstitution(
            fixture.signature, "i/0"));
        FeatureIndex index2 = new FeatureIndex(Substitution.createSubstitution(
            fixture.signature, "i/0"));

        PMLVector vector = new PMLVector();
        vector.setValue(fixture.localClause, index1, 1.0);
        assertEquals(vector.getValue(fixture.localClause, index2), 1.0,
            "Vector must return same value for two feature indices with " +
                "identical substitution");

    }


}
