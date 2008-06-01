package thebeast.pml.formula;

import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 29-Jan-2007 Time: 19:07:34
 */
public class CNF {

  private ArrayList<ArrayList<SignedAtom>> disjunctions;

  public List<ArrayList<SignedAtom>> getDisjunctions() {
    return disjunctions;
  }

  public CNF(BooleanFormula formula) {
    disjunctions = new ArrayList<ArrayList<SignedAtom>>();
    if (formula instanceof Disjunction) {
      addDisjunction((Disjunction) formula);
    } else if (formula instanceof Conjunction) {
      Conjunction conjunction = (Conjunction) formula;
      for (BooleanFormula arg : conjunction.getArguments()) {
        if (arg instanceof Not || arg instanceof Atom)
          arg = new Disjunction(arg);
        addDisjunction((Disjunction) arg);
      }
    } else {
      addDisjunction(new Disjunction(formula));
    }
  }


  private void addDisjunction(Disjunction disjunction) {
    ArrayList<SignedAtom> argAtoms = new ArrayList<SignedAtom>(disjunction.getArguments().size());
    disjunctions.add(argAtoms);
    for (BooleanFormula arg : disjunction.getArguments()) {
      if (arg instanceof Not) {
        argAtoms.add(new SignedAtom(false, (Atom) ((Not) arg).getArgument()));
      } else {
        argAtoms.add(new SignedAtom(true, (Atom) arg));
      }
    }
  }


  public List<SignedAtom> getDisjunction(int index) {
    return disjunctions.get(index);
  }

  public boolean[] getSigns(int index) {
    List<SignedAtom> disjunction = getDisjunction(index);
    boolean[] result = new boolean[disjunction.size()];
    int atomIndex = 0;
    for (SignedAtom atom : disjunction)
      result[atomIndex++] = atom.isTrue();
    return result;
  }

  public int getDisjunctionCount() {
    return disjunctions.size();
  }


  public String toString() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(os);
    int index = 0;
    for (List<SignedAtom> conjunction : disjunctions) {
      if (index++ > 0) out.print(" & ");
      int atomIndex = 0;
      for (SignedAtom atom : conjunction) {
        if (atomIndex > 0) out.print(" | ");
        if (!atom.isTrue()) out.print("!");
        atom.getAtom().acceptBooleanFormulaVisitor(new FormulaPrinter(out));
        ++atomIndex;
      }
    }
    return os.toString();
  }


}
