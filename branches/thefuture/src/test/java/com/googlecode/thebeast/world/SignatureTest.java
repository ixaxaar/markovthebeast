package com.googlecode.thebeast.world;

import junit.framework.TestCase;

/**
 * @author Sebastian Riedel
 */
public class SignatureTest extends TestCase {

  /**
   * Tests the (hidden) constructor of the signature class.
   */
  public final void testConstructor() {
    Signature signature = new Signature();
    assertNotNull(signature.getConnection());
  }

  /**
   * Tests the creation of types via the signature class.
   */
  public final void testCreateType() {
    Signature signature = new Signature();
    UserType type = signature.createType("type");
    assertEquals("type", type.getName());
    assertTrue(signature.getTypes().contains(type));
    assertEquals(type,signature.getType("type"));
  }

}
