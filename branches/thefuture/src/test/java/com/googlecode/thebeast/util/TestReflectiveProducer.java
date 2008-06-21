package com.googlecode.thebeast.util;

import junit.framework.TestCase;

/**
 * Tests the ReflectiveProducer class.
 *
 * @author Sebastian Riedel
 * @see com.googlecode.thebeast.util.ReflectiveProducer
 */
public final class TestReflectiveProducer extends TestCase {

  /**
   * A fruit.
   */
  public class Fruit {
  }

  /**
   * An apple.
   */
  public class Apple extends Fruit {
  }

  /**
   * An orange.
   */
  public class Orange extends Fruit {
  }


  /**
   * Tests the vist method of the produce.
   *
   * @see com.googlecode.thebeast.util.ReflectiveProducer#visit(Object)
   */
  public void testVisit() {
    Apple apple = new Apple();
    Orange orange = new Orange();
    Baker baker = new Baker();
    assertEquals("Pie", baker.visit(orange));
    assertEquals("Apple Pie", baker.visit(apple));
  }

  /**
   * A Baker bakes pies. For apples he does a special applie pie, for other
   * fruits just a normal pie.
   */
  public final class Baker extends ReflectiveProducer<Fruit, String> {

    /**
     * Produce a pie.
     *
     * @param fruit the fruit to include
     * @return string version of a pie.
     */
    String produce(Fruit fruit) {
      return "Pie";
    }

    /**
     * Bake an apple pie.
     *
     * @param apple the apple to put in the pie
     * @return a String version of an apple pie.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    @Dispatchs
    String produce(Apple apple) {
      return "Apple Pie";
    }
  }

}
