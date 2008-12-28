package com.googlecode.thebeast.query;

/**
 * A GroundingSet represents a set of nested substitutions returned by a query.
 * This interface serves as holder for query processing results.
 *
 * @author Sebastian Riedel
 * @see Query
 */
public interface NestedSubstitutionSet extends Iterable<NestedSubstitution> {

  /**
   * Gets the query that produced this set of nested substitutions.
   *
   * @return a the query that produced this set of nested substitutions.
   */
  Query getQueryString();

}
