package com.googlecode.thebeast.util;

import junit.framework.TestCase;

/**
 * Tests the ReflectiveIterator class.
 *
 * @author Sebastian Riedel
 */
public final class TestReflectiveVisitor extends TestCase {

  /**
   * Empty Fruit class.
   */
  public abstract class Fruit {
  }

  /**
   * Empty Apple class.
   */
  public class Apple extends Fruit {
  }

  /**
   * Empty Orange class.
   */
  public class Orange extends Fruit {
  }

  /**
   * Tests the visit method of the visitor.
   *
   * @see ReflectiveVisitor#visit(Object)
   */
  public void testVisit() {
    Apple apple = new Apple();
    Orange orange = new Orange();

    Eater eater = new Eater();

    eater.visit(apple);
    assertEquals(true, eater.eaten);
    assertEquals(false, eater.peeled);

    eater = new Eater();
    eater.visit(orange);
    assertEquals(true, eater.eaten);
    assertEquals(true, eater.peeled);

  }

  /**
   * A test implementation of the ReflectiveIterator. Eats all fruits, peels
   * oranges before.
   */
  public final class Eater extends ReflectiveVisitor<Fruit> {

    /**
     * If the fruit was peeled.
     */
    private boolean peeled = false;
    /**
     * If the frut was eaten.
     */
    private boolean eaten = false;

    /**
     * Justs eats the fruit. Does not need to be annotated.
     *
     * @param fruit the fruit to eat.
     */
    void process(Fruit fruit) {
      eaten = true;
    }

    /**
     * Peels the orange and eats it afterwards. Needs to be annotated in
     * order to be found by this visit method.
     *
     * @param fruit the orange to eat.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    @Dispatchs
    void process(Orange fruit) {
      peeled = true;
      eaten = true;
    }
  }


}
