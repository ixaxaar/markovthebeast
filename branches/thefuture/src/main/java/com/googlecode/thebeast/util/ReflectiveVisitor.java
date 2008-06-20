package com.googlecode.thebeast.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
 *    &#064;Dispatchs
 *    process(Fruit fruit) {...};
 *
 *    &#064;Dispatchs
 *    process(Apple apple) {...};
 * }
 * </pre>
 * <p>Note that there is a performance penalty for using reflection. Clients
 * should not use this class if speed is an issue.
 *
 * @author Sebastian Riedel
 */
public abstract class ReflectiveVisitor<T> {


  /**
   * This method will call the process method of a subclass of this class with
   * the most speficic argument type that matches the class of t.
   *
   * @param t the object to visit.
   * @see ReflectiveVisitor#visit(Object)
   */
  public final void visit(T t) {
    Class clazz = t.getClass();
    Method downPolymorphic = null;
    do {
      try {            
        downPolymorphic =
          getClass().getDeclaredMethod("process", clazz);
      } catch (NoSuchMethodException e) {
        clazz = clazz.getSuperclass();
      }
    } while (downPolymorphic == null && clazz != null);

    if (downPolymorphic == null) {
      throw new ReflectiveVisitorException(
        "Something unexpected happened!",
        this, null);
    } else if (!downPolymorphic.isAnnotationPresent(Dispatchs.class)) {
      throw new ReflectiveVisitorException(
        "The process method with the most specific argument type is "
          + "not marked with the @Dispatch annotation", this, null);

    } else {
      try {
        downPolymorphic.invoke(this, t);
      } catch (Exception e) {
        throw new ReflectiveVisitorException("Internal exception",
          this, e);
      }
    }
  }

  /**
   * This method processes an object of type t. Clients how extend this class
   * need to implement this method. However, they can also write further process
   * methods with more specific argument types which will be properly dispatched
   * when the visitor visits an object via {@link ReflectiveVisitor#visit(Object)}.
   *
   * <p>I order to be dispatched by the visit method each process method needs
   * to be annotated with a {@link Dispatchs} tag.
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
