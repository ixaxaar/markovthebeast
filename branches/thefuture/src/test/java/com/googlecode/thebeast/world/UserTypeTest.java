package com.googlecode.thebeast.world;

import junit.framework.TestCase;

/**
 * Testing the UserType.
 *
 * @author Sebastian Riedel
 */
public final class UserTypeTest extends TestCase {

  /**
   * Tests the constructor.
   */
  public void testConstructor() {
    Signature signature = new Signature();
    UserType type = new UserType("type", false, signature);
    assertEquals("type", type.getName());
    assertEquals("Incorrect Signature in type object",
      signature, type.getSignature());
  }

  /**
   * Tests the creation of constants.
   */
  public void testCreateConstant() {
    UserType type = new UserType("type", false, new Signature());
    UserConstant c1 = type.createConstant("c1");
    UserConstant c2 = type.createConstant("c2");
    assertEquals(2, type.getConstants().size());
    assertEquals(0, c1.getId());
    assertEquals(1, c2.getId());
    assertEquals(type, c1.getType());
    assertEquals("c1", c1.getName());
    assertEquals("type", type.getName());
  }
}
