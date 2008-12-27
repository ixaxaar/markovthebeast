package com.googlecode.thebeast.world;

import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Tests the methods of the Relation class.
 *
 * @author Sebastian Riedel
 * @see Relation
 */
public final class TestRelation  {
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
  @BeforeMethod
  protected void setUp() throws Exception {
    Signature signature = SQLSignature.createSignature();
    type1 = signature.createType("type1", false, "A", "B");
    type2 = signature.createType("type2", false, "C", "D");
    pred = signature.createPredicate("pred", type1, type2);
    world = signature.createWorld();
  }

  /**
   * Tests adding of a single tuple.
   */
  @Test
  public void testAddTuple() {
    MutableRelation relation = world.getMutableRelation(pred);
    Tuple tuple = new Tuple(
      type1.getConstant("A"),
      type2.getConstant("C"));
    relation.add(tuple);
    assertEquals(1, relation.size());
    assertEquals(tuple, relation.iterator().next());
  }

  /**
   * Test the contains method.
   */
  @Test
  public void testContains() {
    MutableRelation relation = world.getMutableRelation(pred);
    Tuple tuple = new Tuple(
      type1.getConstant("A"),
      type2.getConstant("C"));
    relation.add(tuple);
    assertTrue("Relation does not contain the tuple just added",
      relation.contains(tuple));
  }

  /**
   * Test the relation iterator.
   */
  @Test
  public void testIterator() {
    MutableRelation relation = world.getMutableRelation(pred);
    Tuple tuple1 = new Tuple(
      type1.getConstant("A"),
      type2.getConstant("C"));
    Tuple tuple2 = new Tuple(
      type1.getConstant("B"),
      type2.getConstant("C"));
    relation.add(tuple1);
    relation.add(tuple2);
    HashSet<Tuple> expected = new HashSet<Tuple>();
    expected.add(tuple1);
    expected.add(tuple2);
    HashSet<Tuple> actual = new HashSet<Tuple>();
    Iterator<Tuple> iterator = relation.iterator();
    while (iterator.hasNext()) {
      actual.add(iterator.next());
    }
    assertEquals(expected, actual,"Set of constants returned by the iterator is not equal"
      + " to set of constants added to the relation");
    assertEquals(2, relation.size());
  }

}
