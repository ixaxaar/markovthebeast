package com.googlecode.thebeast.world;

/**
 * This exception is thrown when the relation for a given predicate is not
 * updatable in the current world.
 *
 * @author Sebastian Riedel
 */
public final class RelationNotUpdatableException extends RuntimeException {

  /**
   * The predicate for which the relation is not updatable.
   */
  private final UserPredicate predicate;

  /**
   * The world in which the relation is not updatable.
   */
  private final World world;

  /**
   * Creates a new RelationNotUpdatableException.
   *
   * @param predicate the predicate for which the relation is not updatable.
   * @param world     the world in which the requested relation is not
   *                  updatable.
   */
  public RelationNotUpdatableException(final UserPredicate predicate,
                                       final World world) {
    super("Predicate " + predicate + " is not updatable in this world");
    this.predicate = predicate;
    this.world = world;
  }

  /**
   * Returns the predicate for which the relation is not updatable.
   *
   * @return the predicate for which the relation is not updatable.
   */
  public UserPredicate getPredicate() {
    return predicate;
  }

  /**
   * Returns the world for which the relation is not updatable.
   *
   * @return the world in which the relation is not updatable.
   */
  public World getWorld() {
    return world;
  }
}
