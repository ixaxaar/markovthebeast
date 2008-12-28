package com.googlecode.thebeast.world;

import com.googlecode.thebeast.world.sql.SQLSignature;

/**
 * A SocialNetworkFixture consists of a signature with a Person type and
 * friends, smokes and cancer predicates, as well as a few named constants.
 *
 * @author Sebastian Riedel
 */
public class SocialNetworkFixture {

  /**
   * The signature we use in these tests.
   */
  public final Signature signature;

  /**
   * The type that contains person constants.
   */
  public final UserType person;

  /**
   * The predicate representing the friends relation.
   */
  public final UserPredicate friends;

  /**
   * The predicate representing the (unary) smokes relation.
   */
  public final UserPredicate smokes;

  /**
   * The predicate representing the (unary) cancer relation.
   */
  public final UserPredicate cancer;

  /**
   * A constant representing peter.
   */
  public final UserConstant peter;
  /**
   * A constant representing anna.
   */
  public final UserConstant anna;
  /**
   * A constant representing Sebastian.
   */
  public final UserConstant sebastian;

  /**
   * Sets up the basic social network fixture.
   */
  public SocialNetworkFixture() {
    signature = SQLSignature.createSignature();
    person = signature.createType("Person", false);
    peter = person.createConstant("Peter");
    anna = person.createConstant("Anna");
    sebastian = person.createConstant("Sebastian");

    friends = signature.createPredicate("friends", person, person);
    smokes = signature.createPredicate("smokes", person);
    cancer = signature.createPredicate("cancer", person);
  }
}
