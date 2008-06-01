package thebeast.pml.formula;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 29-Jan-2007 Time: 19:07:34
 */
public class DNF {

  private ArrayList<ArrayList<SignedAtom>> conjunctions;

  public List<ArrayList<SignedAtom>> getConjunctions() {
    return conjunctions;
  }

  public DNF(BooleanFormula formula) {
    conjunctions = new ArrayList<ArrayList<SignedAtom>>();
    if (formula instanceof Conjunction) {
      addConjunction((Conjunction) formula);
    } else if (formula instanceof Disjunction) {
      Disjunction disjunction = (Disjunction) formula;
      for (BooleanFormula arg : disjunction.getArguments()) {
        if (arg instanceof Not || arg instanceof Atom)
          arg = new Conjunction(arg);
        addConjunction((Conjunction) arg);
      }
    } else
      addConjunction(new Conjunction(formula));
  }


  private void addConjunction(Conjunction conjunction) {
    ArrayList<SignedAtom> argAtoms = new ArrayList<SignedAtom>(conjunction.getArguments().size());
    conjunctions.add(argAtoms);
    for (BooleanFormula arg : conjunction.getArguments()) {
      if (arg instanceof Not) {
        argAtoms.add(new SignedAtom(false, (Atom) ((Not) arg).getArgument()));
      } else {
        argAtoms.add(new SignedAtom(true, (Atom) arg));
      }
    }
  }


  public List<SignedAtom> getConjunction(int index) {
    return conjunctions.get(index);
  }

  public int getConjunctionCount() {
    return conjunctions.size();
  }


  public String toString() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(os);
    int index = 0;
    for (List<SignedAtom> conjunction : conjunctions) {
      if (index++ > 0) out.print(" | ");
      int atomIndex = 0;
      for (SignedAtom atom : conjunction) {
        if (atomIndex > 0) out.print(" & ");
        if (!atom.isTrue()) out.print("!");
        atom.getAtom().acceptBooleanFormulaVisitor(new FormulaPrinter(out));
        ++atomIndex;
      }
    }
    return os.toString();
  }


  public boolean[] getSigns(int index) {
    List<SignedAtom> conjunction = getConjunction(index);
    boolean[] result = new boolean[conjunction.size()];
    int atomIndex = 0;
    for (SignedAtom atom : conjunction)
      result[atomIndex++] = atom.isTrue();
    return result;
  }

}
