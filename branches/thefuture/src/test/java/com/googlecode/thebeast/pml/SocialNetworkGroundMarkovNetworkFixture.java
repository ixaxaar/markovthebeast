package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.SocialNetworkFixture;

/**
 * @author Sebastian Riedel
 */
public class SocialNetworkGroundMarkovNetworkFixture {
  public final GroundMarkovNetwork gmn;
  public final SocialNetworkFixture socialNetworkFixture;
  public final PMLClause reflexityClause;
  public final PMLClause localClause;
  public final Signature signature;


  public SocialNetworkGroundMarkovNetworkFixture(
    Signature signature) {

    this.signature = signature;
    gmn = new GroundMarkovNetwork();
    this.socialNetworkFixture = new SocialNetworkFixture(signature);
    ClauseBuilder builder = new ClauseBuilder(
      QueryFactory.getInstance(), signature);
    reflexityClause = builder.
      atom(socialNetworkFixture.friends, "x", "y").
      atom(socialNetworkFixture.signature.getIntegerType().getEquals(), "i", "0").
      atom(socialNetworkFixture.signature.getDoubleType().getEquals(), "s", "1.0").
      body().
      head(socialNetworkFixture.friends, "y", "x").
      clause(Exists.EXISTS, "i", "s");
    localClause = builder.
      atom(socialNetworkFixture.signature.getIntegerType().getEquals(), "i", "0").
      atom(socialNetworkFixture.signature.getDoubleType().getEquals(), "s", "1.0").
      body().
      head(socialNetworkFixture.friends, "x", "y").
      clause(Exists.EXISTS, "i", "s");

  }

  public void groundFriendsPeterAnnaImpliesFriendsAnnaPeter(){
    gmn.ground(reflexityClause,
      NestedSubstitution.createNestedSubstitutions(
        socialNetworkFixture.signature, 
        "x/Peter y/Anna i/0 s/1.0",
        "x/Anna y/Peter i/1 s/1.0"));

  }

  public void groundLocalPeterAnnaAreFriendsClause(){
    gmn.ground(localClause,
      NestedSubstitution.createNestedSubstitutions(
        socialNetworkFixture.signature, 
        "x/Peter y/Anna i/0 s/1.0",
        "x/Anna y/Peter i/1 s/1.0"));

  }


}
