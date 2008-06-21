package com.googlecode.thebeast.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A ReflectiveVisitor is a visitor which uses Reflection (and Annotation) to
 * implement double dispatch.
 *
 * <p>Assume you have Fruit class with subclasses such as Apple and Orange. Then
 * the following visitor can visit fruits by calling the visit method. In turn
 * the visitor picks the appropriate version of {@link ReflectiveVisitor#process(Object)}
 * to process properly process the fruit.
 *
 * <p>Each process method that should be called by the visit method needs to be
 * annotated with the tag {@link Dispatchs}.
 * <pre>
 * class Eater extends ReflectiveVisitor&lt;Fruit&gt; {
 *
 *    process(Fruit fruit) {...};
 *
 *    &#064;Dispatchs
 *    process(Apple apple) {...};
 *
 *    &#064;Dispatchs
 *    process(Orange orange) {...};
 * }
 * </pre>
 * <p>Note that there is a performance penalty for using reflection. Clients
 * should not use this class if speed is an issue.
 *
 * @author Sebastian Riedel
 */
public abstract class ReflectiveVisitor<T> {

  /**
   * This method will call the <code>process(R)</code> method where R is the the
   * runtime class of t. If no such method exists or the method is not annotated
   * with {@link Dispatchs} the default {@link ReflectiveVisitor#process(Object)}
   * method is called.
   *
   * @param t the object to visit.
   * @see ReflectiveVisitor#visit(Object)
   */
  public final void visit(T t) {
    Class clazz = t.getClass();
    try {
      Method downPolymorphic = getClass().getDeclaredMethod("process", clazz);
      if (downPolymorphic.isAnnotationPresent(Dispatchs.class)) {
        downPolymorphic.invoke(this, t);
      } else {
        process(t);
      }
    } catch (NoSuchMethodException e) {
      process(t);
    } catch (IllegalAccessException e) {
      throw new ReflectiveVisitorException("Internal exception", this, e);
    } catch (InvocationTargetException e) {
      throw new ReflectiveVisitorException("Internal exception", this, e);
    }

  }

  /**
   * This method processes an object of type t whenever {@link
   * ReflectiveVisitor#visit(Object)} is called. Clients  can also write further
   * process methods with more specific argument types which will be properly
   * dispatched as long the types are subclasses of T (and not interfaces) and
   * the methods are annotated with a {@link Dispatchs} tag.
   *
   * @param t the object to process.
   */
  abstract void process(T t);


  /**
   * Interface Dispatchs marks "process" methods of an ReflectiveVisitor which
   * will be used for dispatching.
   *
   * @author Sebastian Riedel
   * @see ReflectiveVisitor#process(Object)
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Dispatchs {
  }

  /**
   * An exception which is thrown if something goes wrong in the visitor.
   */
  public final static class ReflectiveVisitorException
    extends RuntimeException {
    /**
     * The visitor which threw the exception.
     */
    private final ReflectiveVisitor<?> visitor;

    /**
     * Creates a new exception.
     *
     * @param message the message to display.
     * @param visitor the visitor which threw the exception
     * @param cause   an exception/throwable which causes this exception, may be
     *                null.
     */
    ReflectiveVisitorException(final String message,
                               final ReflectiveVisitor<?> visitor,
                               final Throwable cause) {
      super(message, cause);
      this.visitor = visitor;
    }

    /**
     * Return the visitor that threw this exception.
     *
     * @return the visitor that threw this exception.
     */
    public ReflectiveVisitor<?> getVisitor() {
      return visitor;
    }
  }


}
