package thebeast.pml.function;

import thebeast.pml.function.IntAdd;
import thebeast.pml.function.WeightFunction;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 18:27:32
 */
public interface FunctionVisitor {
  void visitWeightFunction(WeightFunction weightFunction);

  void visitIntAdd(IntAdd intAdd);

  void visitIntMinus(IntMinus intMinus);

  void visitIntMin(IntMin intMin);

  void visitIntMax(IntMax intMax);
}
