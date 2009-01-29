package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.SocialNetworkSignatureFixture;

/**
 * @author Sebastian Riedel
 */
public class SocialNetworkGroundMarkovNetworkFixture {
    public final GroundFactorGraph groundFactorGraph;
    public final SocialNetworkSignatureFixture socialNetworkSignatureFixture;
    public PMLFormula symmetryClause;
    public PMLFormula localClause;
    public final Signature signature;


    public SocialNetworkGroundMarkovNetworkFixture(
        Signature signature) {

        this.signature = signature;
        groundFactorGraph = new GroundFactorGraph();
        this.socialNetworkSignatureFixture = new SocialNetworkSignatureFixture(signature);
        symmetryClause = PMLFormula.createFormula(signature,
            "friends(x,y) ^ integerEquals(+i,0) ^ doubleEquals(#s,1.0) => friends(y,x)");

        localClause = PMLFormula.createFormula(signature,
            "integerEquals(+i,0) ^ doubleEquals(#s,1.0) => friends(x,y)");

    }

    public void groundFriendsPeterAnnaImpliesFriendsAnnaPeter() {
        groundFactorGraph.ground(symmetryClause,
            NestedSubstitution.createNestedSubstitutions(
                socialNetworkSignatureFixture.signature,
                "x/Peter y/Anna i/0 s/1.0"));

    }

    public void groundLocalPeterAnnaAreFriendsClause() {
        groundFactorGraph.ground(localClause,
            NestedSubstitution.createNestedSubstitutions(
                socialNetworkSignatureFixture.signature,
                "x/Peter y/Anna i/0 s/1.0"));

    }


}
