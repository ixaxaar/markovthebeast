package com.googlecode.thebeast.world;

import com.googlecode.thebeast.clause.ClauseFactory;
import com.googlecode.thebeast.clause.GeneralizedClause;
import com.googlecode.thebeast.clause.Grounding;
import com.googlecode.thebeast.clause.GroundingSet;
import com.googlecode.thebeast.world.sql.SQLSignature;
import junit.framework.TestCase;

import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
@SuppressWarnings({"FieldCanBeLocal", "MissingFieldJavaDoc"})
public class TestWorldQueries extends TestCase {

  private Signature signature;
  private UserType person;
  private UserPredicate friends;
  private UserPredicate smokes;
  private UserPredicate cancer;
  private UserConstant peter, anna, sebastian;
  private World world;

  /**
   * Sets up the basic test fixture.
   *
   * @throws Exception if the super method throws an exception.
   */
  protected void setUp() throws Exception {
    signature = SQLSignature.createSignature();
    person = signature.createType("Person", false);
    peter = person.createConstant("Peter");
    anna = person.createConstant("Anna");
    sebastian = person.createConstant("Sebastian");

    friends = signature.createPredicate("friends", person, person);
    smokes = signature.createPredicate("smokes", person);
    cancer = signature.createPredicate("cancer", person);

    world = signature.createWorld();
    world.getRelation(friends).add(new Tuple(sebastian, peter));
    world.getRelation(smokes).add(new Tuple(anna));
    world.getRelation(cancer).add(new Tuple(anna));


    super.setUp();
  }

  /**
   * Tests a simple clause with only one atom in the body and zero in the head.
   */
  public void testOneAtomInBodyClause() {
    world.getRelation(friends).add(new Tuple(peter, anna));
    world.getRelation(friends).add(new Tuple(peter, sebastian));
    GeneralizedClause clause =
      ClauseFactory.build().atom(friends, "x", "y").body().head();
    GroundingSet result = world.query(clause);
    HashSet<Grounding> actual = new HashSet<Grounding>();
    for (Grounding grounding: result){
      actual.add(grounding);  
    }
    

    System.out.println(result);




  }


}
