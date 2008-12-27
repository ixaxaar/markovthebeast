package com.googlecode.thebeast.world;

import com.googlecode.thebeast.clause.ClauseFactory;
import com.googlecode.thebeast.clause.GeneralizedClause;
import com.googlecode.thebeast.clause.Grounding;
import com.googlecode.thebeast.clause.GroundingSet;
import com.googlecode.thebeast.world.sql.SQLSignature;
import junit.framework.TestCase;

import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class TestWorldQueries extends TestCase {

  /**
   * The signature we use in these tests.
   */
  private Signature signature;
  /**
   * The type that contains person constants.
   */
  private UserType person;

  /**
   * The predicate representing the friends relation.
   */
  private UserPredicate friends;

  /**
   * The predicate representing the (unary) smokes relation.
   */
  private UserPredicate smokes;

  /**
   * The predicate representing the (unary) cancer relation.
   */
  private UserPredicate cancer;

  /**
   * A constant representing peter.
   */
  private UserConstant peter;
  /**
   * A constant representing anna.
   */
  private UserConstant anna;
  /**
   * A constant representing Sebastian.
   */
  private UserConstant sebastian;
  /**
   * A world created with the signature.
   */
  private World world;

  /**
   * Sets up the basic test fixture.
   *
   * @throws Exception if the super method throws an exception.
   */
  protected void setUp() throws Exception {
    signature = SQLSignature.createSignature();
    person = signature.createType("Person", false);
    peter = person.createConstant("Peter");
    anna = person.createConstant("Anna");
    sebastian = person.createConstant("Sebastian");

    friends = signature.createPredicate("friends", person, person);
    smokes = signature.createPredicate("smokes", person);
    cancer = signature.createPredicate("cancer", person);

    world = signature.createWorld();

    super.setUp();
  }

  /**
   * Tests a simple clause with only one atom in the body and zero in the head.
   */
  public void testOneAtomInBodyClause() {
    world.getRelation(friends).add(new Tuple(peter, anna));
    world.getRelation(friends).add(new Tuple(peter, sebastian));
    world.getRelation(friends).add(new Tuple(sebastian, peter));
    GeneralizedClause clause =
      ClauseFactory.build().atom(friends, "x", "y").body().head();
    GroundingSet result = world.query(clause);
    HashSet<Grounding> actual = new HashSet<Grounding>();
    for (Grounding grounding : result) {
      actual.add(grounding);
    }
    HashSet<Grounding> expected = new HashSet<Grounding>();
    expected.add(Grounding.createGrounding(signature, "x/Peter y/Anna"));
    expected.add(Grounding.createGrounding(signature, "x/Peter y/Sebastian"));
    expected.add(Grounding.createGrounding(signature, "x/Sebastian y/Peter"));

    assertEquals(expected, actual);

  }

  /**
   * Tests a simple clause with two atoms in the body and zero in the head.
   */
  public void testTwoAtomsInBodyClause() {
    world.getRelation(friends).add(new Tuple(peter, anna));
    world.getRelation(friends).add(new Tuple(peter, sebastian));
    world.getRelation(smokes).add(new Tuple(anna));
    GeneralizedClause clause =
      ClauseFactory.build()
        .atom(friends, "x", "y").atom(smokes, "y").body().head();
    GroundingSet result = world.query(clause);
    HashSet<Grounding> actual = new HashSet<Grounding>();
    for (Grounding grounding : result) {
      actual.add(grounding);
    }
    HashSet<Grounding> expected = new HashSet<Grounding>();
    expected.add(Grounding.createGrounding(signature, "x/Peter y/Anna"));

    assertEquals(expected, actual);

  }

  /**
   * Tests a simple clause with two atoms in the body and one in the head.
   */
  public void testSimpleImplication() {
    world.getRelation(friends).add(new Tuple(peter, anna));
    world.getRelation(friends).add(new Tuple(anna, peter));
    world.getRelation(friends).add(new Tuple(peter, sebastian));
    world.getRelation(smokes).add(new Tuple(anna));
    world.getRelation(smokes).add(new Tuple(peter));
    GeneralizedClause clause =
      ClauseFactory.build()
        .atom(friends, "x", "y").atom(smokes, "x").body()
        .atom(smokes,"y").head();
    GroundingSet result = world.query(clause);
    HashSet<Grounding> actual = new HashSet<Grounding>();
    for (Grounding grounding : result) {
      actual.add(grounding);
    }
    HashSet<Grounding> expected = new HashSet<Grounding>();
    expected.add(Grounding.createGrounding(signature, "x/Peter y/Anna"));
    expected.add(Grounding.createGrounding(signature, "x/Anna y/Peter"));

    assertEquals(expected, actual);
  }

  /**
   * Test a clause with an existential variable.
   */
  public void testExistentialImplication() {
    world.getRelation(friends).add(new Tuple(peter, anna));
    world.getRelation(friends).add(new Tuple(anna, peter));
    world.getRelation(friends).add(new Tuple(peter, sebastian));
    world.getRelation(smokes).add(new Tuple(anna));
    world.getRelation(smokes).add(new Tuple(peter));
    GeneralizedClause clause =
      ClauseFactory.build()
        .atom(smokes, "x").body()
        .atom(friends,"x","y").atom(smokes,"y").head();
    GroundingSet result = world.query(clause);
    HashSet<Grounding> actual = new HashSet<Grounding>();
    for (Grounding grounding : result) {
      actual.add(grounding);
    }
    HashSet<Grounding> expected = new HashSet<Grounding>();
    expected.add(Grounding.createGrounding(signature, "x/Anna {y/Peter}"));
    expected.add(Grounding.createGrounding(signature, "x/Peter {y/Anna}"));
    assertEquals(expected, actual);
  }

}
