package thebeast.pml.corpora;

import thebeast.pml.GroundAtom;
import thebeast.pml.GroundAtoms;
import thebeast.pml.UserPredicate;
import thebeast.pml.Evaluation;
import thebeast.pml.term.IntConstant;

import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class CoNLL00SentencePrinter extends DefaultPrinter {

  public void print(GroundAtoms atoms, PrintStream out) {
    UserPredicate word = atoms.getSignature().getUserPredicate("word");
    UserPredicate pos = atoms.getSignature().getUserPredicate("pos");
    UserPredicate chunk = atoms.getSignature().getUserPredicate("chunk");

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

    if (chunk != null) {
      for (GroundAtom atom : atoms.getGroundAtomsOf(chunk)) {
        int begin = ((IntConstant) atom.getArguments().get(0)).getInteger();
        int end = ((IntConstant) atom.getArguments().get(1)).getInteger();
        String label = atom.getArguments().get(2).toString();
        if (begin == end) {
          table.add(begin, column, label);
        } else {
          table.add(begin, column, "B-" + label);
          table.add(end, column, "E-" + label);
        }
      }

      ++column;
    }

    table.write(out, 0, true);

  }

}
