package thebeast.pml.formula;

import thebeast.pml.UserPredicate;
import thebeast.pml.predicate.*;
import thebeast.pml.term.Term;
import thebeast.pml.term.TermPrinter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class FormulaPrinter implements BooleanFormulaVisitor {

  private PrintStream out;
  private TermPrinter termPrinter;
  protected ByteArrayOutputStream bos;

  public FormulaPrinter(PrintStream out) {
    this.out = out;
    termPrinter = new TermPrinter(out);
  }

  public FormulaPrinter(BooleanFormula formula) {
    bos = new ByteArrayOutputStream();
    this.out = new PrintStream(bos);
    termPrinter = new TermPrinter(out);
    formula.acceptBooleanFormulaVisitor(this);
  }

  public void visitAtom(final Atom atom) {
    atom.acceptAtomVisitor(new AtomVisitor() {
      public void visitPredicateAtom(final PredicateAtom atom) {
        atom.getPredicate().acceptPredicateVisitor(new PredicateVisitor() {
          public void visitUserPredicate(UserPredicate userPredicate) {
            out.print(atom.getPredicate().getName());
            out.print("(");
            int index = 0;
            for (Term term : atom.getArguments()) {
              if (index++ > 0) out.print(", ");
              term.acceptTermVisitor(termPrinter);
            }
            out.print(")");

          }

          public void visitEquals(Equals equals) {
            atom.getArguments().get(0).acceptTermVisitor(termPrinter);
            out.print(" == ");
            atom.getArguments().get(1).acceptTermVisitor(termPrinter);
          }

          public void visitIntLEQ(IntLEQ intLEQ) {
            atom.getArguments().get(0).acceptTermVisitor(termPrinter);
            out.print(" <= ");
            atom.getArguments().get(1).acceptTermVisitor(termPrinter);
          }

          public void visitNotEquals(NotEquals notEquals) {
            atom.getArguments().get(0).acceptTermVisitor(termPrinter);
            out.print(" != ");
            atom.getArguments().get(1).acceptTermVisitor(termPrinter);
          }

          public void visitIntLT(IntLT intLT) {
            atom.getArguments().get(0).acceptTermVisitor(termPrinter);
            out.print(" < ");
            atom.getArguments().get(1).acceptTermVisitor(termPrinter);
          }

          public void visitIntGT(IntGT intGT) {
            atom.getArguments().get(0).acceptTermVisitor(termPrinter);
            out.print(" > ");
            atom.getArguments().get(1).acceptTermVisitor(termPrinter);

          }

          public void visitIntGEQ(IntGEQ intGEQ) {
            atom.getArguments().get(0).acceptTermVisitor(termPrinter);
            out.print(" >= ");
            atom.getArguments().get(1).acceptTermVisitor(termPrinter);

          }


        });

      }

      public void visitCardinalityConstraint(CardinalityConstraint cardinalityConstraint) {
        Term lb = cardinalityConstraint.getLowerBound();
        Term ub = cardinalityConstraint.getUpperBound();
        if (cardinalityConstraint.isGEQ() || cardinalityConstraint.isLEQ()) {
          out.print("|");
          out.print(cardinalityConstraint.getQuantification());
          cardinalityConstraint.getFormula().acceptBooleanFormulaVisitor(FormulaPrinter.this);
          out.print("|");
          if (cardinalityConstraint.isGEQ()){
            out.print(" >= ");
            lb.acceptTermVisitor(termPrinter);
          } else {
            out.print(" <= ");
            ub.acceptTermVisitor(termPrinter);
          }
        } else {
          lb.acceptTermVisitor(termPrinter);
          out.print(" <= ");
          out.print("|");
          out.print(cardinalityConstraint.getQuantification());
          cardinalityConstraint.getFormula().acceptBooleanFormulaVisitor(FormulaPrinter.this);
          out.print("|");
          out.print(" <= ");
          ub.acceptTermVisitor(termPrinter);
        }
      }


      public void visitExists(Exists exists) {
        out.print("(exists " + exists.getQuantification() + ": " + exists.getFormula() + ")");
      }

      public void visitForall(Forall forall) {
        out.print("(forall " + forall.getQuantification() + ": " + forall.getFormula() + ")");
      }

      public void visitTrue(True aTrue) {
        out.print(true);
      }
    });
  }

  public void visitConjunction(Conjunction conjunction) {
    int index = 0;
    for (BooleanFormula formula : conjunction.getArguments()) {
      if (index++ > 0) out.print(" & ");
      formula.acceptBooleanFormulaVisitor(this);
    }
  }

  public void visitDisjunction(Disjunction disjunction) {
    int index = 0;
    out.print("(");
    for (BooleanFormula formula : disjunction.getArguments()) {
      if (index++ > 0) out.print(" | ");
      formula.acceptBooleanFormulaVisitor(this);
    }
    out.print(")");
  }


  public void visitImplication(Implication implication) {
    implication.getPremise().acceptBooleanFormulaVisitor(this);
    out.print(" => ");
    implication.getConclusion().acceptBooleanFormulaVisitor(this);
  }

  public void visitNot(Not not) {
    out.print("!");
    out.print("(");
    not.getArgument().acceptBooleanFormulaVisitor(this);
    out.print(")");
  }

  public void visitAcyclicityConstraint(AcyclicityConstraint acyclicityConstraint) {
    out.print(acyclicityConstraint.getPredicate().getName() + " is acyclic");
  }

  public String getResult() {
    return bos.toString();
  }
}
