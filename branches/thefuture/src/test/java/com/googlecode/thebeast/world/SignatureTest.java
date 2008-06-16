package com.googlecode.thebeast.world;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public final class SignatureTest extends TestCase {

  /**
   * Tests the (hidden) constructor of the signature class.
   */
  public void testConstructor() {
    Signature signature = new Signature();
    assertNotNull(signature.getConnection());
  }

  /**
   * Tests the creation of types via the signature class.
   */
  public void testCreateType() {
    Signature signature = new Signature();
    UserType type = signature.createType("type");
    assertEquals("type", type.getName());
    assertTrue(signature.getTypes().contains(type));
    assertEquals(type, signature.getType("type"));
  }

  /**
   * Tests whether the signature class fires type added events properly.
   */
  public void testTypeAddedEvent() {
    Signature signature = new Signature();
    Listener listener = new Listener();
    signature.addSignatureListener(listener);
    UserType type = signature.createType("type");

    assertEquals("Signature did not fire type added event properly",
      type, listener.type);
  }

  /**
   * Tests whether the signature class fires predicate added events properly.
   */
  public void testPredicateAddedEvent() {
    Signature signature = new Signature();
    Listener listener = new Listener();
    signature.addSignatureListener(listener);
    ArrayList<Type> argumentTypes = new ArrayList<Type>();
    argumentTypes.add(signature.createType("type"));
    UserPredicate predicate = signature.createPredicate("pred",argumentTypes);

    assertEquals("Signature did not fire predicate added event properly",
      predicate, listener.predicate);
  }

  /**
   * This class helps to test whether the signature sends out the right events.
   */
  private static final class Listener implements SignatureListener {

    /**
     * The type that was last added.
     */
    private Type type;

    /**
     * the predicate that was last added.
     */
    private Predicate predicate;


    /**
     * {@inheritDoc}
     */
    public void typeAdded(final Type type) {
      this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public void predicateAdded(final Predicate predicate) {
      this.predicate = predicate;
    }


  }


}
