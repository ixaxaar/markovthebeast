package thebeast.pml.term;

import thebeast.pml.function.FunctionVisitor;
import thebeast.pml.function.IntAdd;
import thebeast.pml.function.IntMinus;
import thebeast.pml.function.*;
import thebeast.util.HashMultiMapList;

/**
 * @author Sebastian Riedel
 */
public class TermInverter implements TermVisitor{

  private Variable argument;
  private Term result, term;
  private HashMultiMapList<Term,Term> children;
  ChildrenCollector collector = new ChildrenCollector();



  public Term invert(Term term, Variable lhs, Variable argument){
    this.argument = argument;
    this.result = lhs;
    this.term = term;
    children = collector.collect(term);
    term.acceptTermVisitor(this);
    return result;
  }


  public void visitVariable(Variable variable) {

  }

  public void visitFunctionApplication(final FunctionApplication functionApplication) {
    functionApplication.getFunction().acceptFunctionVisitor(new FunctionVisitor() {
      public void visitWeightFunction(WeightFunction weightFunction) {
        
      }

      public void visitIntAdd(IntAdd intAdd) {
        Term lhs = functionApplication.getArguments().get(0);
        Term rhs = functionApplication.getArguments().get(1);
        if (children.get(lhs).contains(argument)){
          result = new FunctionApplication(IntMinus.MINUS, result, rhs);
          lhs.acceptTermVisitor(TermInverter.this);
        } else {
          result = new FunctionApplication(IntMinus.MINUS, result, lhs);            
          rhs.acceptTermVisitor(TermInverter.this);
        }
      }


      public void visitIntMinus(IntMinus intMinus) {
        Term lhs = functionApplication.getArguments().get(0);
        Term rhs = functionApplication.getArguments().get(1);
        if (children.get(lhs).contains(argument)){
          result = new FunctionApplication(IntAdd.ADD, result, rhs);  
          lhs.acceptTermVisitor(TermInverter.this);
        } else {
          result = new FunctionApplication(IntMinus.MINUS, lhs, result);
          rhs.acceptTermVisitor(TermInverter.this);
        }
      }

      public void visitDoubleProduct(DoubleProduct doubleProduct) {
        throw new RuntimeException("We can't invert product function " + doubleProduct + " in " + term + " yet!");
      }

      public void visitDoubleCast(DoubleCast doubleCast) {
        throw new RuntimeException("We can't invert cast function " + doubleCast + " in " + term + " yet!");
      }

      public void visitDoubleAbs(DoubleAbs doubleAbs) {
        throw new RuntimeException("We can't invert abs function " + doubleAbs + " in " + term + " yet!");
      }

      public void visitDoubleAdd(DoubleAdd doubleAdd) {
        throw new RuntimeException("We can't invert double add function " + doubleAdd+ " in " + term + " yet!");
      }

      public void visitDoubleMinus(DoubleMinus doubleMinus) {
        throw new RuntimeException("We can't invert double minus function " + doubleMinus+ " in " + term + " yet!");
      }


      public void visitIntMin(IntMin intMin) {
        throw new RuntimeException("We can't invert min function " + intMin + " in " + term);
      }

      public void visitIntMax(IntMax intMax) {
        throw new RuntimeException("We can't invert max function " + intMax + " in " + term);
      }

    });
  }

  public void visitIntConstant(IntConstant intConstant) {

  }

  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {

  }

  public void visitDontCare(DontCare dontCare) {

  }

  public void visitDoubleConstant(DoubleConstant doubleConstant) {

  }

  public void visitBinnedInt(BinnedInt binnedInt) {

  }

  public void visitBoolConstant(BoolConstant boolConstant) {

  }
}
