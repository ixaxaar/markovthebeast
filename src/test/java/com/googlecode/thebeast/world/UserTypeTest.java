package com.googlecode.thebeast.world;

import junit.framework.TestCase;

/**
 * Testing the UserType.
 *
 * @author Sebastian Riedel
 */
public class UserTypeTest extends TestCase {

  /**
   * Tests the creation of constants.
   */
  public final void testConstantCreation() {
    UserType type = new UserType("type");
    UserConstant c1 = type.createConstant("c1");
    UserConstant c2 = type.createConstant("c2");
    assertEquals(2, type.getConstants().size());
    assertEquals(0, c1.getId());
    assertEquals(1, c2.getId());
  }
}
