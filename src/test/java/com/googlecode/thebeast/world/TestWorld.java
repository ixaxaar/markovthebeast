package com.googlecode.thebeast.world;

import junit.framework.TestCase;

/**
 * Tests for the World class.
 *
 * @author Sebastian Riedel
 * @see World
 */
public final class TestWorld extends TestCase {
  /**
   * Binary predicate.
   */
  private UserPredicate pred1;
  /**
   * Unary predicate.
   */
  private UserPredicate pred2;
  /**
   * The signature to use.
   */
  private Signature signature;


  /**
   * Sets up a basic test fixture with type1 = {A,B}, type2={C,D},
   * pred1(type1,type2) and pred2(type1).
   *
   * @throws Exception if parent setup method fails.
   */
  protected void setUp() throws Exception {
    super.setUp();
    signature = new Signature();
    UserType type1 = signature.createType("type1", false);
    type1.createConstant("A");
    type1.createConstant("B");
    UserType type2 = signature.createType("type2", false);
    type2.createConstant("C");
    type2.createConstant("D");
    pred1 = signature.createPredicate("pred1", type1, type2);
    pred2 = signature.createPredicate("pred2", type1);
  }

  /**
   * Tests the constructor of the World class.
   */
  public void testConstructor() {
    World world = new World(signature, 1);
    assertEquals("Ids do not match", 1, world.getId());
    assertEquals("Signature not set properly", signature, world.getSignature());
  }

  /**
   * Tests whether multiply getRelation method return the same relation object.
   */
  public void testGetRelationWithoutParent() {
    World world = signature.createWorld();
    assertEquals(world.getRelation(pred1), world.getRelation(pred1));
    assertEquals(world.getRelation(pred2), world.getRelation(pred2));
  }

  /**
   * Tests whether the child world returns the relations of the corresponding
   * parent worlds.
   */
  public void testGetRelationWithParent() {
    World parent = signature.createWorld();
    World child = signature.createWorld();
    child.addParent(pred1, parent);
    assertEquals(child.getRelation(pred1), parent.getRelation(pred1));
    assertFalse(child.getRelation(pred2).equals(parent.getRelation(pred2)));
  }

  /**
   * Tests whether the addParent method throws the proper exception when a local
   * relation for the given predicate already exists.
   */
  public void testAddParent() {
    World parent = signature.createWorld();
    World child = signature.createWorld();
    Relation relation = child.getRelation(pred1);
    try {
      child.addParent(pred1, parent);
      fail("Should throw an exception here");
    } catch (PredicateAlreadyInUseException e){
      assertEquals(relation, child.getRelation(pred1));
    }
  }

}
