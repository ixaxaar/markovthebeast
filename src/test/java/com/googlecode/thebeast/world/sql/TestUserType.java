package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.Constant;
import com.googlecode.thebeast.world.UserConstant;
import com.googlecode.thebeast.world.UserType;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Testing the UserType.
 *
 * @author Sebastian Riedel
 */
public final class TestUserType extends TestCase {

  /**
   * Tests the constructor.
   */
  public void testConstructor() {
    SQLSignature signature = new SQLSignature();
    UserType type = new SQLUserType("type", false, signature);
    assertEquals("type", type.getName());
    assertEquals("Incorrect Signature in type object",
      signature, type.getSignature());
  }

  /**
   * Tests the creation of constants.
   */
  public void testCreateConstant() {
    UserType type = new SQLUserType("type", false, new SQLSignature());
    UserConstant c1 = type.createConstant("c1");
    UserConstant c2 = type.createConstant("c2");
    assertEquals(2, type.getConstants().size());
    assertEquals(0, c1.getId());
    assertEquals(1, c2.getId());
    assertEquals(type, c1.getType());
    assertEquals("c1", c1.getName());
    assertEquals("type", type.getName());
  }

  /**
   * Tests the iterator method.
   *
   * @see SQLUserType#iterator()
   */
  public void testIterator() {
    UserType type = new SQLUserType("type", false, new SQLSignature());
    ArrayList<Constant> expected = new ArrayList<Constant>();
    expected.add(type.createConstant("c1"));
    expected.add(type.createConstant("c2"));
    ArrayList<Constant> actual = new ArrayList<Constant>();
    for (Constant c : type) {
      actual.add(c);
    }
    assertEquals(expected, actual);

  }


  /**
   * Tests the size method of the UserType class.
   *
   * @see SQLUserType#size()
   */
  public void testSize() {
    UserType type = new SQLUserType("type", false, new SQLSignature());
    type.createConstant("c1");
    type.createConstant("c2");
    assertEquals(2, type.size());
  }

  /**
   * Tests the isIterable method.
   */
  public void testIsIterable() {
    UserType type = new SQLUserType("type", false, new SQLSignature());
    assertTrue(type.isIterable());
  }



}
