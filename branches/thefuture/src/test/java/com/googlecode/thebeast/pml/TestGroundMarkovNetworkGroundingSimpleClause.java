package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.world.SocialNetworkFixture;
import com.googlecode.thebeast.world.sql.SQLSignature;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.assertEquals;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class TestGroundMarkovNetworkGroundingSimpleClause {
  private GroundMarkovNetwork gmn;
  private SocialNetworkFixture fixture;
  private PMLClause clause;
  private List<NestedSubstitution> substitutions;


  @BeforeMethod
  public void setUp() {

    gmn = new GroundMarkovNetwork();
    fixture = new SocialNetworkFixture(SQLSignature.createSignature());
    ClauseBuilder builder = new ClauseBuilder(
      QueryFactory.getInstance(), fixture.signature);
    clause = builder.
      atom(fixture.friends, "x", "y").
      atom(fixture.signature.getIntegerType().getEquals(), "i", "0").
      atom(fixture.signature.getDoubleType().getEquals(), "s", "1.0").
      body().
      head(fixture.friends, "y", "x").
      clause(Exists.EXISTS, "i", "s");
    substitutions = NestedSubstitution.createNestedSubstitutions(fixture.signature,
      "x/Peter y/Anna i/0 s/1.0",
      "x/Peter y/Anna i/1 s/1.0");
  }

  @Test
  public void testGroundCreatesFactorWithOriginalClause() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);
    assertEquals(clause, factor.getClause());
  }

  @Test
  public void testGroundCreatesBodyWithRightNumberOfAtoms() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);
    assertEquals(3, factor.getBody().size());
  }

  @Test
  public void testGroundCreatesBodyAtomWithRightPredicate() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);
    assertEquals(fixture.friends, factor.getBody().get(0).getPredicate());
  }

  @Test
  public void testGroundCreatesBodyWithCorrectAtomArgument() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);
    assertEquals(fixture.anna, factor.getBody().get(0).getArguments().get(1));
  }

  @Test
  public void testGroundCreatesBodyWithRightStaticPredicate() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);
    assertEquals(fixture.signature.getIntegerType().getEquals(),
      factor.getBody().get(1).getPredicate());
  }

  @Test
  public void testGroundCreatesRightNumberOfFactors() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);
    assertEquals(2, factors.size());
  }

  @Test
  public void testGround() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);

    assertEquals(3, factor.getBody().size());
    assertEquals(fixture.friends, factor.getBody().get(0).getPredicate());
    assertEquals(fixture.peter, factor.getBody().get(0).getArguments().get(0));
    assertEquals(fixture.anna, factor.getBody().get(0).getArguments().get(1));
    assertEquals(fixture.signature.getIntegerType().getEquals(),
      factor.getBody().get(1).getPredicate());

    assertEquals(
      2, gmn.getNodes(fixture.friends).size());
    assertEquals(
      2, gmn.getNodes(fixture.signature.getIntegerType().getEquals()).size());
    assertEquals(
      1, gmn.getNodes(fixture.signature.getDoubleType().getEquals()).size());
    assertEquals(
      5, gmn.getNodes().size());


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
