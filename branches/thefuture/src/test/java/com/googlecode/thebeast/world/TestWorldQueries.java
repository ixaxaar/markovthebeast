package com.googlecode.thebeast.world;

import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.query.Query;
import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.query.NestedSubstitutionSet;
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
    Query clause =
      QueryFactory.build().atom(friends, "x", "y").outer().inner();
    NestedSubstitutionSet result = world.query(clause);
    HashSet<NestedSubstitution> actual = new HashSet<NestedSubstitution>();
    for (NestedSubstitution nestedSubstitution : result) {
      actual.add(nestedSubstitution);
    }
    HashSet<NestedSubstitution> expected = new HashSet<NestedSubstitution>();
    expected.add(NestedSubstitution.createGrounding(signature, "x/Peter y/Anna"));
    expected.add(NestedSubstitution.createGrounding(signature, "x/Peter y/Sebastian"));
    expected.add(NestedSubstitution.createGrounding(signature, "x/Sebastian y/Peter"));

    assertEquals(expected, actual);

  }

  /**
   * Tests a simple clause with two atoms in the body and zero in the head.
   */
  public void testTwoAtomsInBodyClause() {
    world.getRelation(friends).add(new Tuple(peter, anna));
    world.getRelation(friends).add(new Tuple(peter, sebastian));
    world.getRelation(smokes).add(new Tuple(anna));
    Query clause =
      QueryFactory.build()
        .atom(friends, "x", "y").atom(smokes, "y").outer().inner();
    NestedSubstitutionSet result = world.query(clause);
    HashSet<NestedSubstitution> actual = new HashSet<NestedSubstitution>();
    for (NestedSubstitution nestedSubstitution : result) {
      actual.add(nestedSubstitution);
    }
    HashSet<NestedSubstitution> expected = new HashSet<NestedSubstitution>();
    expected.add(NestedSubstitution.createGrounding(signature, "x/Peter y/Anna"));

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
    Query clause =
      QueryFactory.build()
        .atom(friends, "x", "y").atom(smokes, "x").outer()
        .atom(smokes,"y").inner();
    NestedSubstitutionSet result = world.query(clause);
    HashSet<NestedSubstitution> actual = new HashSet<NestedSubstitution>();
    for (NestedSubstitution nestedSubstitution : result) {
      actual.add(nestedSubstitution);
    }
    HashSet<NestedSubstitution> expected = new HashSet<NestedSubstitution>();
    expected.add(NestedSubstitution.createGrounding(signature, "x/Peter y/Anna"));
    expected.add(NestedSubstitution.createGrounding(signature, "x/Anna y/Peter"));

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
    Query clause =
      QueryFactory.build()
        .atom(smokes, "x").outer()
        .atom(friends,"x","y").atom(smokes,"y").inner();
    NestedSubstitutionSet result = world.query(clause);
    HashSet<NestedSubstitution> actual = new HashSet<NestedSubstitution>();
    for (NestedSubstitution nestedSubstitution : result) {
      actual.add(nestedSubstitution);
    }
    HashSet<NestedSubstitution> expected = new HashSet<NestedSubstitution>();
    expected.add(NestedSubstitution.createGrounding(signature, "x/Anna {y/Peter}"));
    expected.add(NestedSubstitution.createGrounding(signature, "x/Peter {y/Anna}"));
    assertEquals(expected, actual);
  }

}
