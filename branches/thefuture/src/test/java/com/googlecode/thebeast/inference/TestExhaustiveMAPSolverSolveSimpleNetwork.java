package com.googlecode.thebeast.inference;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import com.googlecode.thebeast.pml.*;
import com.googlecode.thebeast.world.SocialNetworkFixture;
import com.googlecode.thebeast.world.Tuple;
import com.googlecode.thebeast.world.IntegerType;
import com.googlecode.thebeast.world.sql.SQLSignature;
import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.query.NestedSubstitution;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class TestExhaustiveMAPSolverSolveSimpleNetwork {
  private GroundMarkovNetwork gmn;
  private SocialNetworkFixture fixture;
  private PMLClause reflexityClause;
  private List<NestedSubstitution> substitutions;
  private ExhaustiveMAPSolver solver;
  private PMLClause localClause;

  @BeforeMethod
  public void setUp() {

    gmn = new GroundMarkovNetwork();
    fixture = new SocialNetworkFixture(SQLSignature.createSignature());
    ClauseBuilder builder = new ClauseBuilder(
      QueryFactory.getInstance(), fixture.signature);
    reflexityClause = builder.
      atom(fixture.friends, "x", "y").
      atom(fixture.signature.getIntegerType().getEquals(), "i", "0").
      atom(fixture.signature.getDoubleType().getEquals(), "s", "1.0").
      body().
      head(fixture.friends, "y", "x").
      clause(Exists.EXISTS, "i", "s");
    gmn.ground(reflexityClause,
      NestedSubstitution.createNestedSubstitutions(fixture.signature,
        "x/Peter y/Anna i/0 s/1.0",
        "x/Anna y/Peter i/1 s/1.0"));

    localClause = builder.
      atom(fixture.signature.getIntegerType().getEquals(), "i", "0").
      atom(fixture.signature.getDoubleType().getEquals(), "s", "1.0").
      body().
      head(fixture.friends, "x", "y").
      clause(Exists.EXISTS, "i", "s");

    gmn.ground(reflexityClause,
      NestedSubstitution.createNestedSubstitutions(fixture.signature,
        "x/Peter y/Anna i/0 s/1.0",
        "x/Anna y/Peter i/1 s/1.0"));

    Weights weights = new Weights();
    weights.setWeight(reflexityClause, 0, 1.0);
    weights.setWeight(reflexityClause, 1, 1.0);
    weights.setWeight(localClause, 0, 1.0);
    weights.setWeight(localClause, 1, 1.0);
    solver = new ExhaustiveMAPSolver(gmn, weights);
  }

  @Test
  public void testSolveResultIsNotNull() {
    Assignment result = solver.solve();
    assertNotNull(result, "solver returned a null result.");
  }


  @Test
  public void testSolveResultIsConsistentWithStaticPredicates() {
    Assignment result = solver.solve();
    IntegerType intType = fixture.signature.getIntegerType();
    assertEquals(
      1.0,
      result.getValue(gmn.getNode(
        intType.getEquals(),new Tuple(intType.getEquals(), 1, 1))));
  }
}
