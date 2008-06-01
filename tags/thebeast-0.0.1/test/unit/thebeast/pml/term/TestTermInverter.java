package thebeast.pml.term;

import junit.framework.TestCase;
import thebeast.pml.formula.FormulaBuilder;
import thebeast.pml.*;
import thebeast.pml.function.IntAdd;
import thebeast.pml.function.IntMinus;

/**
 * @author Sebastian Riedel
 */
public class TestTermInverter extends TestCase {
  private FormulaBuilder builder;
  private Signature signature;
  private TermInverter inverter;

  protected void setUp(){
    signature = TheBeast.getInstance().createSignature();
    signature.createPredicate("token", Type.INT, Type.INT);
    builder = new FormulaBuilder(signature);
    inverter = new TermInverter();
  }

  public void testAdd(){
    Variable x = new Variable(Type.INT, "x");
    Variable y = new Variable(Type.INT, "y");

    Term add = new FunctionApplication(IntAdd.ADD, x, new IntConstant(2));
    Term inverted = inverter.invert(add,y,x);
    Term expected = new FunctionApplication(IntMinus.MINUS, y, new IntConstant(2));
    assertEquals(expected,inverted);

    // y = ((x + 2) + 3)
    Term add2 = new FunctionApplication(IntAdd.ADD,add,new IntConstant(3));
    inverted = inverter.invert(add2,y,x);
    expected = new FunctionApplication(IntMinus.MINUS,
            new FunctionApplication(IntMinus.MINUS, y, new IntConstant(3)),new IntConstant(2));
    assertEquals(expected,inverted);

    System.out.println(inverted);


  }
}
