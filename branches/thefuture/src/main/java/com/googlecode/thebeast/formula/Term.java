package com.googlecode.thebeast.formula;

import com.googlecode.thebeast.world.Type;

/**
 * A Term object represents a First Order Logic term: a placeholder for an
 * object of the domain.
 *
 * <p>Since we assume Herbrand semantics the objects of the domain are the
 * constants of the signature. Hence terms are placeholders for constants.
 *
 * @author Sebastian Riedel
 */
public interface Term {

  /**
   * Method getType returns the type of this Term object.
   *
   * @return the type (type Type) of this Term object.
   */
  Type getType();

}
