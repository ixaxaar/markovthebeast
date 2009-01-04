package com.googlecode.thebeast.inference;

import com.googlecode.thebeast.pml.Assignment;
import com.googlecode.thebeast.pml.PMLVector;
import com.googlecode.thebeast.pml.SocialNetworkGroundMarkovNetworkFixture;
import com.googlecode.thebeast.world.IntegerType;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestExhaustiveMAPSolverSolveSimpleNetwork {
  private ExhaustiveMAPSolver solver;
  private SocialNetworkGroundMarkovNetworkFixture fixture;
  private UserPredicate friends;

  @BeforeMethod
  public void setUp() {

    fixture = new SocialNetworkGroundMarkovNetworkFixture(
      SQLSignature.createSignature());

    fixture.groundFriendsPeterAnnaImpliesFriendsAnnaPeter();
    fixture.groundLocalPeterAnnaAreFriendsClause();

    PMLVector weights = new PMLVector();
    weights.setValue(fixture.reflexityClause, 0, 1.0);
    weights.setValue(fixture.localClause, 0, 2.0);
    solver = new ExhaustiveMAPSolver(fixture.gmn, weights);
    friends = fixture.socialNetworkFixture.friends;
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
    assertEquals(result.getValue(intType.getEquals(), 0, 0), 1.0);
  }

  @Test
  public void testSolverPerformsCorrectNumberOfEvaluations() {
    solver.solve();
    assertEquals(solver.getEvaluationCount(), 4, "Solver has to do 4 " +
      "iterations because there are 4 possible states");
  }


  @Test
  public void testSolveResultAssignsCorrectValuesToUserPredicates() {
    Assignment result = solver.solve();
    assertEquals(result.getValue(friends, "Peter", "Anna"), 1.0);
  }

}
