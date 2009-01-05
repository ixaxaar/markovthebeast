package com.googlecode.thebeast.inference;

import com.googlecode.thebeast.pml.PMLVector;
import com.googlecode.thebeast.pml.SocialNetworkPMLNFixture;
import com.googlecode.thebeast.world.Relation;
import com.googlecode.thebeast.world.SocialNetworkSignatureFixture;
import com.googlecode.thebeast.world.World;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestNaiveFirstOrderMAPSolverSolveSimplePMLN {

  private SocialNetworkPMLNFixture fixture;
  private World observation;
  private PMLVector weights;
  private NaiveFirstOrderMAPSolver solver;

  @BeforeMethod
  public void setUp() {
    fixture = new SocialNetworkPMLNFixture(
      new SocialNetworkSignatureFixture(SQLSignature.createSignature()));
    observation = fixture.signature.createWorld();
    observation.setOpen(fixture.friends, true);
    Relation friendsScores = observation.getRelation(fixture.friendsScore);
    friendsScores.addTuple("Peter", "Anna", 2.0);
    friendsScores.addTuple("Anna", "Peter", -1.0);
    friendsScores.addTuple("Sebastian", "Peter", 1.0);
    friendsScores.addTuple("Peter", "Sebastian", -3.0);
    weights = new PMLVector();
    weights.setValue(fixture.symmetryClause, 0, 2.0);
    weights.setValue(fixture.friendsScoreClause, 0, 1.0);

    fixture.addFriendsScoreClause();
    fixture.addSymmetryClause();

    solver = new NaiveFirstOrderMAPSolver(fixture.pmln, weights,
      new ExhaustiveMAPSolver());
  }

  @Test
  public void testSolveResultIsNotNull() {
    World result = solver.solve(observation);
    assertNotNull(result, "The solver should not return null.");
  }

  @Test
  public void testSolveResultIsConsistentWithObservedUserPredicates() {
    World result = solver.solve(observation);
    assertTrue(result.containsGroundAtom(
      fixture.friendsScore, "Peter", "Anna", 2.0),
      "The result fails contain a ground atom that was contained in " +
        "the given observation");
  }

  @Test
  public void testSolveResultIsCorrectForHiddenUserPredicates() {
    World result = solver.solve(observation);
    assertTrue(result.getRelation(fixture.friends).
      containsTuple("Anna", "Peter"),
      "The result does not contain a ground atom that should be included.");
  }


}
