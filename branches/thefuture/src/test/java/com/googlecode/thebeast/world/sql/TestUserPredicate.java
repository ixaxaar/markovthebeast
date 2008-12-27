package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.UserPredicate;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Testing the UserPredicate class.
 *
 * @author Sebastian Riedel
 */
public final class TestUserPredicate {

  /**
   * Tests the UserPredicate constructor.
   */
  @Test
  public void testConstructor() {
    SQLSignature signature = new SQLSignature();
    List<SQLRepresentableType> argumentTypes =
      new ArrayList<SQLRepresentableType>();
    SQLUserType type1 = signature.createType("Type1", false);
    SQLUserType type2 = signature.createType("Type2", false);
    argumentTypes.add(type1);
    argumentTypes.add(type2);
    UserPredicate predicate =
      new SQLUserPredicate("pred", argumentTypes, signature);

    assertFalse("Type list not copied",
      argumentTypes == predicate.getArgumentTypes());
    assertEquals("Type list not identical",
      argumentTypes, predicate.getArgumentTypes());
    assertEquals("Name not identical", "pred", predicate.getName());

  }

}
