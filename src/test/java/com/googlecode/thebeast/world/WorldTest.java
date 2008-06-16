package com.googlecode.thebeast.world;

import junit.framework.TestCase;

/**
 * @author Sebastian Riedel
 */
public class WorldTest extends TestCase {

  public void testConstructor() {
    Signature signature = new Signature();
    World world = new World(signature, 10);
    assertEquals("Ids do not match", 10, world.getId());
    assertEquals("Signature not set properly", signature, world.getSignature());

  }

}
