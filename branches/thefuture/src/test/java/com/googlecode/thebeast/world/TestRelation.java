package com.googlecode.thebeast.world;

import com.googlecode.thebeast.world.sql.SQLSignature;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Tests the methods of the Relation class.
 *
 * @author Sebastian Riedel
 * @see Relation
 */
public final class TestRelation extends TestCase {
  /**
   * A type with constants A,B.
   */
  private UserType type1;
  /**
   * A type with constants C,D.
   */
  private UserType type2;
  /**
   * a predicate pred(type1,type2).
   */
  private UserPredicate pred;

  /**
   * An (empty possible world based on the above signature.
   */
  private World world;

  /**
   * Sets up the basic fixture: two types type1 and type2 and a binary predicate
   * pred(type1,type2) and a world with this signature.
   *
   * @throws Exception if the super method fails.
   */
  protected void setUp() throws Exception {
    super.setUp();
    Signature signature = new SQLSignature();
    type1 = signature.createType("type1", false, "A", "B");
    type2 = signature.createType("type2", false, "C", "D");
    pred = signature.createPredicate("pred", type1, type2);
    world = signature.createWorld();
  }

  /**
   * Tests adding of a single tuple.
   */
  public void testAddTuple() {
    MutableRelation relation = world.getMutableRelation(pred);
    Tuple tuple = new Tuple(
      type1.getConstant("A"),
      type2.getConstant("C"));
    relation.addTuple(tuple);
    assertEquals(1, relation.size());
    assertEquals(tuple, relation.iterator().next());
  }

  /**
   * Test the contains method.
   */
  public void testContains() {
    MutableRelation relation = world.getMutableRelation(pred);
    Tuple tuple = new Tuple(
      type1.getConstant("A"),
      type2.getConstant("C"));
    relation.addTuple(tuple);
    assertTrue("Relation does not contain the tuple just added",
      relation.contains(tuple));
  }

  /**
   * Test the relation iterator.
   */
  public void testIterator() {
    MutableRelation relation = world.getMutableRelation(pred);
    Tuple tuple1 = new Tuple(
      type1.getConstant("A"),
      type2.getConstant("C"));
    Tuple tuple2 = new Tuple(
      type1.getConstant("B"),
      type2.getConstant("C"));
    relation.addTuple(tuple1);
    relation.addTuple(tuple2);
    HashSet<Tuple> expected = new HashSet<Tuple>();
    expected.add(tuple1);
    expected.add(tuple2);
    HashSet<Tuple> actual = new HashSet<Tuple>();
    Iterator<Tuple> iterator = relation.iterator();
    while (iterator.hasNext()) {
      actual.add(iterator.next());
    }
    assertEquals("Set of constants returned by the iterator is not equal"
      + " to set of constants added to the relation", expected, actual);
    assertEquals(2, relation.size());
  }

}
