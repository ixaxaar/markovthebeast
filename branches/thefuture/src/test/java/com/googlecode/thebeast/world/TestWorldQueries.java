package com.googlecode.thebeast.world;

import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.query.NestedSubstitutionSet;
import com.googlecode.thebeast.query.Query;
import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class TestWorldQueries {

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
   */
  @BeforeMethod
  protected void setUp() {
    signature = SQLSignature.createSignature();
    person = signature.createType("Person", false);
    peter = person.createConstant("Peter");
    anna = person.createConstant("Anna");
    sebastian = person.createConstant("Sebastian");

    friends = signature.createPredicate("friends", person, person);
    smokes = signature.createPredicate("smokes", person);
    cancer = signature.createPredicate("cancer", person);

    world = signature.createWorld();
  }

  /**
   * Tests a simple query with only one atom in the outer conjunction and zero
   * in the inner conjunction.
   */
  @Test
  public void testOneAtomInOuterConjunctionQuery() {
    world.getRelation(friends).add(new Tuple(peter, anna));
    world.getRelation(friends).add(new Tuple(peter, sebastian));
    world.getRelation(friends).add(new Tuple(sebastian, peter));
    Query query =
      QueryFactory.build().atom(friends, "x", "y").outer().inner();
    NestedSubstitutionSet result = world.query(query);
    HashSet<NestedSubstitution> actual = new HashSet<NestedSubstitution>();
    for (NestedSubstitution nestedSubstitution : result) {
      actual.add(nestedSubstitution);
    }
    HashSet<NestedSubstitution> expected = new HashSet<NestedSubstitution>();
    expected.add(NestedSubstitution.createNestedSubstitution(
      signature, "x/Peter y/Anna"));
    expected.add(NestedSubstitution.createNestedSubstitution(
      signature, "x/Peter y/Sebastian"));
    expected.add(NestedSubstitution.createNestedSubstitution(
      signature, "x/Sebastian y/Peter"));

    assertEquals(expected, actual);

  }

  /**
   * Tests a simple query with two atoms in the outer conjunction and zero in
   * the inner conjunction.
   */
  @Test
  public void testTwoAtomsInOuterConjunctionQuery() {
    world.getRelation(friends).add(new Tuple(peter, anna));
    world.getRelation(friends).add(new Tuple(peter, sebastian));
    world.getRelation(smokes).add(new Tuple(anna));
    Query query =
      QueryFactory.build()
        .atom(friends, "x", "y").atom(smokes, "y").outer().inner();
    NestedSubstitutionSet result = world.query(query);
    HashSet<NestedSubstitution> actual = new HashSet<NestedSubstitution>();
    for (NestedSubstitution nestedSubstitution : result) {
      actual.add(nestedSubstitution);
    }
    HashSet<NestedSubstitution> expected = new HashSet<NestedSubstitution>();
    expected.add(NestedSubstitution.createNestedSubstitution(
      signature, "x/Peter y/Anna"));

    assertEquals(expected, actual);

  }

  /**
   * Tests a simple query with two atoms in the outer conjunction and one in the
   * inner conjunction.
   */
  @Test
  public void testSimpleImplication() {
    world.getRelation(friends).add(new Tuple(peter, anna));
    world.getRelation(friends).add(new Tuple(anna, peter));
    world.getRelation(friends).add(new Tuple(peter, sebastian));
    world.getRelation(smokes).add(new Tuple(anna));
    world.getRelation(smokes).add(new Tuple(peter));
    Query query =
      QueryFactory.build()
        .atom(friends, "x", "y").atom(smokes, "x").outer()
        .atom(smokes, "y").inner();
    NestedSubstitutionSet result = world.query(query);
    HashSet<NestedSubstitution> actual = new HashSet<NestedSubstitution>();
    for (NestedSubstitution nestedSubstitution : result) {
      actual.add(nestedSubstitution);
    }
    HashSet<NestedSubstitution> expected = new HashSet<NestedSubstitution>();
    expected.add(NestedSubstitution.createNestedSubstitution(
      signature, "x/Peter y/Anna"));
    expected.add(NestedSubstitution.createNestedSubstitution(
      signature, "x/Anna y/Peter"));

    assertEquals(expected, actual);
  }

  /**
   * Test a query with an existential variable.
   */
  @Test
  public void testExistentialImplication() {
    world.getRelation(friends).add(new Tuple(peter, anna));
    world.getRelation(friends).add(new Tuple(anna, peter));
    world.getRelation(friends).add(new Tuple(peter, sebastian));
    world.getRelation(smokes).add(new Tuple(anna));
    world.getRelation(smokes).add(new Tuple(peter));
    Query query =
      QueryFactory.build()
        .atom(smokes, "x").outer()
        .atom(friends, "x", "y").atom(smokes, "y").inner();
    NestedSubstitutionSet result = world.query(query);
    HashSet<NestedSubstitution> actual = new HashSet<NestedSubstitution>();
    for (NestedSubstitution nestedSubstitution : result) {
      actual.add(nestedSubstitution);
    }
    HashSet<NestedSubstitution> expected = new HashSet<NestedSubstitution>();
    expected.add(NestedSubstitution.createNestedSubstitution(
      signature, "x/Anna {y/Peter}"));
    expected.add(NestedSubstitution.createNestedSubstitution(
      signature, "x/Peter {y/Anna}"));
    assertEquals(expected, actual);
  }

}
