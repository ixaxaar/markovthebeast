package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.DoubleConstant;
import com.googlecode.thebeast.world.IntegerConstant;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.SignatureListener;
import com.googlecode.thebeast.world.Symbol;
import com.googlecode.thebeast.world.SymbolAlreadyExistsException;
import com.googlecode.thebeast.world.Type;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.UserType;
import com.googlecode.thebeast.world.World;
import junit.framework.Assert;
import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Tests methods of the Signature class.
 *
 * @author Sebastian Riedel
 * @see SQLSignature
 */
public final class TestSQLSignature {


  /**
   * Tests the (hidden) constructor of the signature class.
   */
  @Test
  public void testConstructor() {
    SQLSignature signature = new SQLSignature();
    assertNotNull(signature.getConnection());
  }

  /**
   * Tests the creation of types via the signature class.
   */
  @Test
  public void testCreateType() {
    Signature signature = new SQLSignature();
    UserType type = signature.createType("type", false);
    assertEquals("type", type.getName());
    assertTrue(signature.getTypes().contains(type));
    assertEquals(type, signature.getType("type"));
    try {
      signature.createType("type", false);
      fail("Should throw SymbolAlreadyExistsException");
    } catch (SymbolAlreadyExistsException e) {
      assertEquals(type, e.getSymbol());
    }
  }

  /**
   * Tests the creation of predicates via the signature class.
   */
  @Test
  public void testCreatePredicate() {
    Signature signature = new SQLSignature();
    UserType type = signature.createType("type", false);
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

  @Test
  public void testCreateWorldWithInheritedClosedPredicates() {
    Signature signature = new SQLSignature();
    UserType type = signature.createType("type", false, "A", "B");
    UserPredicate predOpen = signature.createPredicate("predOpen", type);
    UserPredicate predClosed =
      signature.createPredicate("predClosed", type, type);

    World world = signature.createWorld();
    world.getRelation(predClosed).addTuple("A", "B");
    world.setOpen(predOpen, true);

    World inherited = signature.createWorld(world);
    assertTrue("Inherited world need to contain the same closed relations as " +
      "the parent world",
      inherited.getRelation(predClosed).containsTuple("A", "B"));


  }


  /**
   * Tests whether the signature class fires type added events properly.
   */
  @Test
  public void testTypeEvents() {
    Signature signature = new SQLSignature();
    Listener listener = new Listener();
    signature.addSignatureListener(listener);
    UserType type = signature.createType("type", false);

    Assert.assertEquals("Signature did not fire symbol added event properly",
      type, listener.symbol);

    signature.removeType(type);

    assertNull("Signature did not fire symbol removed event properly",
      listener.symbol);


  }

  /**
   * Tests whether the signature class fires predicate added events properly.
   */
  @Test
  public void testPredicateAddedEvent() {
    Signature signature = new SQLSignature();
    Listener listener = new Listener();
    signature.addSignatureListener(listener);
    ArrayList<Type> argumentTypes = new ArrayList<Type>();
    argumentTypes.add(signature.createType("type", false));
    UserPredicate predicate = signature.createPredicate("pred", argumentTypes);

    Assert.assertEquals("Signature did not fire symbol added event properly",
      predicate, listener.symbol);

    signature.removePredicate(predicate);
    assertNull("Signature did not fire symbol removed event properly",
      listener.symbol);

  }

  /**
   * Test whether the get symbol method returns the right (default) symbols.
   */
  @Test
  public void testGetSymbolForBuiltInSymbols() {
    Signature signature = new SQLSignature();
    assertEquals(1.0, ((DoubleConstant) signature.getSymbol("1.0")).getValue());
    assertEquals(0, ((IntegerConstant) signature.getSymbol("0")).getValue());

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
    public void symbolRemoved(final Symbol symbol) {
      this.symbol = null;
    }


  }


}