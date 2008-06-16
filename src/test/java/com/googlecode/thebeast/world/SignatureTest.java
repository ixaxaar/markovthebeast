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
    try {
      signature.createType("type");
      fail("Should throw SymbolAlreadyExistsException");
    } catch (SymbolAlreadyExistsException e) {
      assertEquals(type, e.getSymbol());
    }
  }

  /**
   * Tests the creation of predicates via the signature class.
   */
  public void testCreatePredicate() {
    Signature signature = new Signature();
    UserType type = signature.createType("type");
    UserPredicate pred = signature.createPredicate("pred", type);
    assertEquals("pred", pred.getName());
    assertTrue(signature.getPredicateNames().contains(pred.getName()));
    assertEquals(pred, signature.getPredicate("pred"));
    try {
      signature.createPredicate("pred", type, type);
      fail("Should throw SymbolAlreadyExistsException");
    } catch (SymbolAlreadyExistsException e) {
      assertEquals(pred, e.getSymbol());
    }
  }

  /**
   * Tests whether the signature class fires type added events properly.
   */
  public void testTypeEvents() {
    Signature signature = new Signature();
    Listener listener = new Listener();
    signature.addSignatureListener(listener);
    UserType type = signature.createType("type");

    assertEquals("Signature did not fire symbol added event properly",
      type, listener.symbol);

    signature.removeType(type);

    assertNull("Signature did not fire symbol removed event properly",
      listener.symbol);


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
    UserPredicate predicate = signature.createPredicate("pred", argumentTypes);

    assertEquals("Signature did not fire symbol added event properly",
      predicate, listener.symbol);

    signature.removePredicate(predicate);
    assertNull("Signature did not fire symbol removed event properly",
      listener.symbol);

  }

  /**
   * This class helps to test whether the signature sends out the right events.
   */
  private static final class Listener implements SignatureListener {

    /**
     * The type that was last added.
     */
    private Symbol symbol;


    /**
     * {@inheritDoc}
     */
    public void symbolAdded(final Symbol symbol) {
      this.symbol = symbol;
    }

    /**
     * {@inheritDoc}
     */
    public void symbolRemoved(Symbol symbol) {
      this.symbol = null;
    }


  }


}
