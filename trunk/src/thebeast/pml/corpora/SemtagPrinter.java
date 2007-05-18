package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.UserPredicate;
import thebeast.pml.GroundAtom;
import thebeast.pml.term.IntConstant;

import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class SemtagPrinter implements GroundAtomsPrinter {

  public void print(GroundAtoms atoms, PrintStream out) {
    UserPredicate word = atoms.getSignature().getUserPredicate("word");
    UserPredicate pos = atoms.getSignature().getUserPredicate("pos");
    UserPredicate slot = atoms.getSignature().getUserPredicate("slot");
    UserPredicate goal = atoms.getSignature().getUserPredicate("goal");

    out.print(atoms.getGroundAtomsOf(goal).toString());

    out.println();

    ColumnTable table = new ColumnTable();
    int column = 0;
    for (GroundAtom atom : atoms.getGroundAtomsOf(word))
      table.add(((IntConstant) atom.getArguments().get(0)).getInteger(), column,
              atom.getArguments().get(1).toString());
    ++column;
    if (pos != null) {
      for (GroundAtom atom : atoms.getGroundAtomsOf(pos))
        table.add(((IntConstant) atom.getArguments().get(0)).getInteger(), column,
                atom.getArguments().get(1).toString());
      ++column;
    }
    if (slot != null) {
      for (GroundAtom atom : atoms.getGroundAtomsOf(slot))
        table.add(((IntConstant) atom.getArguments().get(0)).getInteger(), column,
                atom.getArguments().get(1).toString());
      ++column;
    }


    table.write(out, 0, true);

  }
}
