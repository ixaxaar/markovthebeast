package com.googlecode.thebeast.world;

import junit.framework.TestCase;

/**
 * Tests for the World class.
 *
 * @author Sebastian Riedel
 * @see World
 */
public final class WorldTest extends TestCase {

  /**
   * Tests the constructor of the World class.
   */
  public void testConstructor() {
    Signature signature = new Signature();
    signature.createType("type",false);
    signature.createPredicate("pred", signature.getType("type"));
    World world = new World(signature, 1);
    assertEquals("Ids do not match", 1, world.getId());
    assertEquals("Signature not set properly", signature, world.getSignature());

  }

}
