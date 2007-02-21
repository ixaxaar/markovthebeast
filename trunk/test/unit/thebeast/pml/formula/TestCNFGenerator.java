package thebeast.pml.formula;

import junit.framework.TestCase;
import thebeast.pml.Signature;
import thebeast.pml.TheBeast;
import thebeast.pml.Type;

/**
 * @author Sebastian Riedel
 */
public class TestCNFGenerator extends TestCase {
  private FormulaBuilder builder;
  private Signature signature;
  private CNFGenerator generator;

  protected void setUp(){
    signature = TheBeast.getInstance().createSignature();
    signature.createPredicate("token", Type.INT, Type.INT);
    signature.createPredicate("phrase", Type.INT, Type.INT, Type.INT);

    builder = new FormulaBuilder(signature);
    generator = new CNFGenerator();
  }

  public void testImplies(){
    builder.term(1).term(2).atom("token").term(2).term(3).atom("token").implies();
    BooleanFormula converted = generator.convert(builder.getFormula());
    assertTrue(converted instanceof Disjunction);
    Disjunction disjunction = (Disjunction) converted;
    assertTrue(disjunction.getArguments().get(0) instanceof Not);
    assertTrue(disjunction.getArguments().get(1) instanceof Atom);
    System.out.println(converted);
    System.out.println(new CNF(converted));

  }

  public void testNegated(){
    builder.term(1).term(2).atom("token").term(2).term(3).atom("token").or(2);
    builder.term(2).term(1).atom("token").term(3).term(2).atom("token").or(2);
    builder.and(2).not();

    BooleanFormula booleanFormula = builder.getFormula();
    System.out.println(booleanFormula);

    BooleanFormula converted = generator.convert(booleanFormula);
    System.out.println(converted);

  }

}
