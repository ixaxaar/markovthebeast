package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.SocialNetworkSignatureFixture;

/**
 * @author Sebastian Riedel
 */
public class SocialNetworkGroundMarkovNetworkFixture {
    public final GroundFactorGraph gmn;
    public final SocialNetworkSignatureFixture socialNetworkSignatureFixture;
    public final PMLClause symmetryClause;
    public final PMLClause localClause;
    public final Signature signature;


    public SocialNetworkGroundMarkovNetworkFixture(
        Signature signature) {

        this.signature = signature;
        gmn = new GroundMarkovNetwork();
        this.socialNetworkSignatureFixture = new SocialNetworkSignatureFixture(signature);
        ClauseBuilder builder = new ClauseBuilder(
            QueryFactory.getInstance(), signature);
        symmetryClause = builder.
            atom(socialNetworkSignatureFixture.friends, "x", "y").
            atom(socialNetworkSignatureFixture.signature.getIntegerType().getEquals(), "i", "0").
            atom(socialNetworkSignatureFixture.signature.getDoubleType().getEquals(), "s", "1.0").
            body().
            head(socialNetworkSignatureFixture.friends, "y", "x").
            clause(Exists.EXISTS, "i", "s");
        localClause = builder.
            atom(socialNetworkSignatureFixture.signature.getIntegerType().getEquals(), "i", "0").
            atom(socialNetworkSignatureFixture.signature.getDoubleType().getEquals(), "s", "1.0").
            body().
            head(socialNetworkSignatureFixture.friends, "x", "y").
            clause(Exists.EXISTS, "i", "s");

    }

    public void groundFriendsPeterAnnaImpliesFriendsAnnaPeter() {
        gmn.ground(symmetryClause,
            NestedSubstitution.createNestedSubstitutions(
                socialNetworkSignatureFixture.signature,
                "x/Peter y/Anna i/0 s/1.0"));

    }

    public void groundLocalPeterAnnaAreFriendsClause() {
        gmn.ground(localClause,
            NestedSubstitution.createNestedSubstitutions(
                socialNetworkSignatureFixture.signature,
                "x/Peter y/Anna i/0 s/1.0"));

    }


}
