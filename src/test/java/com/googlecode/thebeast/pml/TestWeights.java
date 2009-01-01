package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.SocialNetworkFixture;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestWeights {
  private Signature signature;
  private PMLVector weights;
  private SocialNetworkFixture fixture;
  private ClauseBuilder builder;
  private PMLClause clause;

  @BeforeMethod
  protected void setUp() throws Exception {
    signature = SQLSignature.createSignature();
    weights = new PMLVector();
    fixture = new SocialNetworkFixture(signature);
    builder = new ClauseBuilder(
      QueryFactory.getInstance(), fixture.signature);
    clause = builder.
      atom(fixture.friends, "x", "y").
      atom(fixture.signature.getIntegerType().getEquals(), "i", "0").
      atom(fixture.signature.getDoubleType().getEquals(), "s", "1.0").
      body().
      head(fixture.friends, "y", "x").
      clause(Exists.EXISTS, "i", "s");
  }


  @Test
  public void testSet() {
    weights.setValue(clause, 0, 1.0);
    assertEquals(weights.getValue(clause, 0), 1.0);
  }

  @Test
  public void testDefaultValue() {
    assertEquals(weights.getValue(clause, 0), 0.0);
  }

}
