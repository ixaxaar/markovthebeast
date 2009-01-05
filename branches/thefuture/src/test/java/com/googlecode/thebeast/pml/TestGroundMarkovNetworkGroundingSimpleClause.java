package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.NestedSubstitution;
import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.world.IntegerType;
import com.googlecode.thebeast.world.SocialNetworkSignatureFixture;
import com.googlecode.thebeast.world.Tuple;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class TestGroundMarkovNetworkGroundingSimpleClause {
  private GroundMarkovNetwork gmn;
  private SocialNetworkSignatureFixture signatureFixture;
  private PMLClause clause;
  private List<NestedSubstitution> substitutions;


  @BeforeMethod
  public void setUp() {

    gmn = new GroundMarkovNetwork();
    signatureFixture = new SocialNetworkSignatureFixture(SQLSignature.createSignature());
    ClauseBuilder builder = new ClauseBuilder(
      QueryFactory.getInstance(), signatureFixture.signature);
    clause = builder.
      atom(signatureFixture.friends, "x", "y").
      atom(signatureFixture.signature.getIntegerType().getEquals(), "i", "0").
      atom(signatureFixture.signature.getDoubleType().getEquals(), "s", "1.0").
      body().
      head(signatureFixture.friends, "y", "x").
      clause(Exists.EXISTS, "i", "s");
    substitutions = NestedSubstitution.createNestedSubstitutions(signatureFixture.signature,
      "x/Peter y/Anna i/0 s/1.0",
      "x/Peter y/Anna i/1 s/1.0");
  }

  @Test
  public void testGroundCreatesFactorWithOriginalClause() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);
    assertEquals(clause, factor.getClause());
  }

  @Test
  public void testGroundCreatesBodyWithRightNumberOfAtoms() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);
    assertEquals(3, factor.getBody().size());
  }

  @Test
  public void testGroundCreatesBodyAtomWithRightPredicate() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);
    assertEquals(signatureFixture.friends, factor.getBody().get(0).getPredicate());
  }

  @Test
  public void testGroundCreatesBodyWithCorrectAtomArgument() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);
    assertEquals(signatureFixture.anna, factor.getBody().get(0).getArguments().get(1));
  }

  @Test
  public void testGroundCreatesBodyWithRightStaticPredicate() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);

    GroundFactor factor = factors.get(0);
    assertEquals(signatureFixture.signature.getIntegerType().getEquals(),
      factor.getBody().get(1).getPredicate());
  }

  @Test
  public void testGroundCreatesRightNumberOfFactors() {
    List<GroundFactor> factors = gmn.ground(clause,
      substitutions);
    assertEquals(2, factors.size());
  }

  @Test
  public void testGroundCreatesNodeThatCanBeAccessedByAnAtomSpec() {
    gmn.ground(clause, substitutions);
    IntegerType intType = signatureFixture.signature.getIntegerType();
    assertNotNull(gmn.getNode(intType.getEquals(),
      new Tuple(intType.getEquals(), 1, 1)));
  }




}
