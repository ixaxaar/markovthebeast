package com.googlecode.thebeast.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * A ReflectiveProducer is a visitor that produces items at each visit and which
 * uses Reflection (and Annotation) to implement double dispatch. The difference
 * to the {@link ReflectiveVisitor} is that this class allows subclasses to
 * produce something based on what was visited.
 *
 * <p>Assume you have Fruit class with subclasses such as Apple and Orange. Then
 * the following producer can visit fruits by calling the visit method. In turn
 * the producer picks the appropriate version of {@link
 * ReflectiveProducer#produce(Object)} to properly process the fruit and produce
 * an item (such as a Pie).
 *
 * <p>Each process method that should be called by the visit method needs to be
 * annotated with the tag {@link Dispatchs}.
 * <pre>
 * class Eater extends ReflectiveProducer&lt;Fruit,Pie&gt; {
 *
 *    Pie produce(Fruit fruit) {...};
 *
 *    &#064;Dispatchs
 *    Pie produce(Apple apple) {...};
 * }
 * </pre>
 * <p>Note that there is a performance penalty for using reflection. Clients
 * should not use this class if speed is an issue.
 *
 * @author Sebastian Riedel
 */
public abstract class ReflectiveProducer<T, R> {

  /**
   * This method will call the <code>produce(S)</code> method where S is the the
   * runtime class of t. If no such method exists or the method is not annotated
   * with {@link Dispatchs} the default {@link ReflectiveVisitor#process(Object)}
   * method is called.
   *
   * @param t the object to visit.
   * @return what this producer produced after visiting t.
   * @see ReflectiveVisitor#visit(Object)
   */
  @SuppressWarnings({"unchecked"})
  public final R visit(T t) {
    Class clazz = t.getClass();
    try {
      Method downPolymorphic = getClass().getDeclaredMethod("produce", clazz);
      if (downPolymorphic.isAnnotationPresent(Dispatchs.class)) {
        return (R) downPolymorphic.invoke(this, t);
      } else {
        return produce(t);
      }
    } catch (NoSuchMethodException e) {
      return produce(t);
    } catch (IllegalAccessException e) {
      throw new ReflectiveProducerException("Internal exception", this, e);
    } catch (InvocationTargetException e) {
      throw new ReflectiveProducerException("Internal exception", this, e);
    }
  }

  /**
   * This method processes an object of type t whenever {@link
   * ReflectiveVisitor#visit(Object)} is called. Clients  can also write further
   * process methods with more specific argument types which will be properly
   * dispatched as long the types are subclasses of T (and not interfaces) and
   * the methods are annotated with a {@link Dispatchs} tag.
   *
   *
   * @param t the object to process.
   * @return what this producer produced after visiting t.
   */
  abstract R produce(T t);


  /**
   * Interface Dispatchs marks "process" methods of an ReflectiveProducer which
   * will be used for dispatching.
   *
   * @author Sebastian Riedel
   * @see ReflectiveProducer#produce(Object)
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Dispatchs {
  }

  /**
   * An exception which is thrown if something goes wrong in the producer.
   */
  public final static class ReflectiveProducerException
    extends RuntimeException {
    /**
     * The producer which threw the exception.
     */
    private final ReflectiveProducer<?, ?> producer;

    /**
     * Creates a new exception.
     *
     * @param message  the message to display.
     * @param producer the producer which threw the exception
     * @param cause    an exception/throwable which causes this exception, may
     *                 be null.
     */
    ReflectiveProducerException(final String message,
                                final ReflectiveProducer<?, ?> producer,
                                final Throwable cause) {
      super(message, cause);
      this.producer = producer;
    }

    /**
     * Return the producer that threw this exception.
     *
     * @return the producer that threw this exception.
     */
    public ReflectiveProducer<?, ?> getProducer() {
      return producer;
    }
  }


}