package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.world.SocialNetworkFixture;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestPseudoGroundMarkovNetwork {

  @Test
  public void testGround() {
    PseudoGroundMarkovNetwork gmn = new PseudoGroundMarkovNetwork();
    SocialNetworkFixture fixture = new SocialNetworkFixture();

    ClauseBuilder builder = new ClauseBuilder(QueryFactory.getInstance());

    PMLClause clause = builder.
      atom(fixture.friends,"x","y").
      body().
      head(fixture.friends,"y","x").
      clause(null, null,null);

    //PMLClause clause = builder.atom"+("

    //let us create a simple formula
    //clause = builder.body("likes(x,y),+id=0,*s=1.0").head("likes(y,x)")
    //PMLClause clause = new PMLClause(null,null,null);

    //clause = builder.body("likes(x,y),_id(x,+id),*s=1.0").head("likes(y,x)")

    //more complex formula: if you like someone someone (else) will like you
    //clause = builder.atom("likes(x,y)").body().head(AtLeastOne, "likes(z,x)")

    //more complex formula: if you like someone someone else will like you
    //clause = builder.atom("likes(x,y)").body().atom("z!=y").condition(). head("likes(z,x)")

    // Add your code here
  }
}
