package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.SocialNetworkSignatureFixture;
import com.googlecode.thebeast.world.UserPredicate;

/**
 * @author Sebastian Riedel
 */
public class SocialNetworkPMLNFixture {

    public final UserPredicate friends, friendsScore;
    public final PseudoMarkovLogicNetwork pmln;
    public final SocialNetworkSignatureFixture signatureFixture;
    public final Signature signature;
    public PMLFormula friendsScoreClause;
    public PMLFormula symmetryClause;

    public SocialNetworkPMLNFixture(SocialNetworkSignatureFixture signatureFixture) {
        this.friends = signatureFixture.friends;
        this.friendsScore = signatureFixture.friendsScore;
        this.pmln = new PseudoMarkovLogicNetwork();
        this.signatureFixture = signatureFixture;
        this.signature = signatureFixture.signature;
//        ClauseBuilder builder = new ClauseBuilder(
//            QueryFactory.getInstance(), signatureFixture.signature);
//        friendsScoreClause = builder.
//            atom(signature.getIntegerType().getEquals(), "i", "0").
//            atom(friendsScore, "x", "y", "s").
//            body().
//            head(friends, "x", "y").
//            clause(Exists.EXISTS, "i", "s");
//
//        symmetryClause = builder.
//            atom(friends, "x", "y").
//            atom(signature.getIntegerType().getEquals(), "i", "0").
//            atom(signature.getDoubleType().getEquals(), "s", "1.0").
//            body().
//            head(friends, "y", "x").
//            clause(Exists.EXISTS, "i", "s");
    }

    public void addFriendsScoreClause() {
        pmln.addFormula(friendsScoreClause);
    }

    public void addSymmetryClause() {
        pmln.addFormula(symmetryClause);
    }


}
