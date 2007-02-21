package thebeast.pml.term;

import thebeast.pml.function.FunctionVisitor;
import thebeast.pml.function.IntAdd;
import thebeast.pml.function.IntMinus;
import thebeast.pml.function.WeightFunction;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class TermPrinter implements TermVisitor {

  private PrintStream out;
  private ByteArrayOutputStream bos;


  public TermPrinter(PrintStream out) {
    this.out = out;
  }

  public TermPrinter(Term term){
    bos = new ByteArrayOutputStream();
    out = new PrintStream(bos);
    term.acceptTermVisitor(this);

  }
  
  public String getResult(){
    return bos.toString();
  }

  public void visitVariable(Variable variable) {
    out.print(variable.getName());
  }

  public void visitFunctionApplication(final FunctionApplication functionApplication) {

    functionApplication.getFunction().acceptFunctionVisitor(new FunctionVisitor() {
      public void visitWeightFunction(WeightFunction weightFunction) {
        out.print(functionApplication.getFunction().getName());
        if (functionApplication.getArguments().size() > 0){
          out.print("(");
          int index = 0;
          for (Term term : functionApplication.getArguments()){
            if (index++>0) out.print(", ");
            term.acceptTermVisitor(TermPrinter.this);
          }
          out.print(")");
        }
      }

      public void visitIntAdd(IntAdd intAdd) {
        out.print("(");
        functionApplication.getArguments().get(0).acceptTermVisitor(TermPrinter.this);
        out.print(" + ");
        functionApplication.getArguments().get(1).acceptTermVisitor(TermPrinter.this);
        out.print(")");
      }

      public void visitIntMinus(IntMinus intMinus) {
        out.print("(");
        functionApplication.getArguments().get(0).acceptTermVisitor(TermPrinter.this);
        out.print(" - ");
        functionApplication.getArguments().get(1).acceptTermVisitor(TermPrinter.this);
        out.print(")");

      }
    });
  }

  public void visitIntConstant(IntConstant intConstant) {
    out.print(intConstant.getInteger());
  }

  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {
    out.print("\"" + categoricalConstant.getName() + "\"");
  }

  public void visitDontCare(DontCare dontCare) {
    out.print("_");
  }

  public void visitDoubleConstant(DoubleConstant doubleConstant) {
    out.print(doubleConstant.getValue());
  }

  public void visitBinnedInt(BinnedInt binnedInt) {
    out.print("bin(");
    for (int bin : binnedInt.getBins()){
      out.print(bin);
      out.print(", ");
    }
    binnedInt.getArgument().acceptTermVisitor(this);
    out.print(")");
  }
}
