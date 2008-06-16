package com.googlecode.thebeast.world;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

/**
 * Testing the UserPredicate class.
 *
 * @author Sebastian Riedel
 */
public final class UserPredicateTest extends TestCase {

  /**
   * Tests the UserPredicate constructor.
   */
  public void testConstructor() {
    Signature signature = new Signature();
    List<Type> argumentTypes = new ArrayList<Type>();
    UserType type1 = signature.createType("Type1");
    UserType type2 = signature.createType("Type2");
    argumentTypes.add(type1);
    argumentTypes.add(type2);
    UserPredicate predicate =
      new UserPredicate("pred", argumentTypes, signature);

    assertFalse("Type list not copied",
      argumentTypes == predicate.getArgumentTypes());
    assertEquals("Type list not identical",
      argumentTypes, predicate.getArgumentTypes());
    assertEquals("Name not identical", "pred", predicate.getName());

  }

}
