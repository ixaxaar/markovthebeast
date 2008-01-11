package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.UserPredicate;
import thebeast.pml.GroundAtom;
import thebeast.pml.Evaluation;
import thebeast.pml.term.IntConstant;
import thebeast.util.Pair;

import java.io.PrintStream;
import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class CoNLL05SentencePrinter extends DefaultPrinter {

  public void print(GroundAtoms atoms, PrintStream out) {
    UserPredicate word = atoms.getSignature().getUserPredicate("word");
    UserPredicate pos = atoms.getSignature().getUserPredicate("pos");
    UserPredicate arg = atoms.getSignature().getUserPredicate("arg");
    UserPredicate isarg = atoms.getSignature().getUserPredicate("isarg");
    UserPredicate span = atoms.getSignature().getUserPredicate("span");

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

    HashMap<Integer, Pair<Integer, Integer>> spans = new HashMap<Integer, Pair<Integer, Integer>>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(span)) {
      spans.put(atom.getArguments().get(0).asInt(), new Pair<Integer, Integer>(
              atom.getArguments().get(1).asInt(), atom.getArguments().get(2).asInt()));
    }

    if (isarg != null) {
      for (GroundAtom atom : atoms.getGroundAtomsOf(isarg)) {
        Pair<Integer, Integer> beginEnd = spans.get(atom.getArguments().get(0).asInt());
        int begin = beginEnd.arg1;
        int end = beginEnd.arg2;
        if (begin == end) {
          table.add(begin, column, "arg");
        } else {
          table.add(begin, column, "B-arg");
          table.add(end, column, "E-arg");
        }
      }

      ++column;
    }
    
    if (arg != null) {
      for (GroundAtom atom : atoms.getGroundAtomsOf(arg)) {
        Pair<Integer, Integer> beginEnd = spans.get(atom.getArguments().get(0).asInt());
        int begin = beginEnd.arg1;
        int end = beginEnd.arg2;
        String label = atom.getArguments().get(1).toString();
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


  public void printEval(Evaluation evaluation, PrintStream out) {
    GroundAtoms atoms = evaluation.getGuess();
    UserPredicate word = atoms.getSignature().getUserPredicate("word");
    UserPredicate pos = atoms.getSignature().getUserPredicate("pos");
    UserPredicate arg = atoms.getSignature().getUserPredicate("arg");
    UserPredicate isarg = atoms.getSignature().getUserPredicate("isarg");
    UserPredicate span = atoms.getSignature().getUserPredicate("span");
    UserPredicate gold = evaluation.getGold().getSignature().getUserPredicate("arg");

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

    HashMap<Integer, Pair<Integer, Integer>> spans = new HashMap<Integer, Pair<Integer, Integer>>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(span)) {
      spans.put(atom.getArguments().get(0).asInt(), new Pair<Integer, Integer>(
              atom.getArguments().get(1).asInt(), atom.getArguments().get(2).asInt()));
    }

    if (isarg != null) {
      for (GroundAtom atom : atoms.getGroundAtomsOf(isarg)) {
        Pair<Integer, Integer> beginEnd = spans.get(atom.getArguments().get(0).asInt());
        int begin = beginEnd.arg1;
        int end = beginEnd.arg2;
        if (begin == end) {
          table.add(begin, column, "arg");
        } else {
          table.add(begin, column, "B-arg");
          table.add(end, column, "E-arg");
        }
      }

      ++column;
    }
    if (arg != null) {
      for (GroundAtom atom : atoms.getGroundAtomsOf(arg)) {
        Pair<Integer, Integer> beginEnd = spans.get(atom.getArguments().get(0).asInt());
        int begin = beginEnd.arg1;
        int end = beginEnd.arg2;
        String label = atom.getArguments().get(1).toString();
        if (begin == end) {
          table.add(begin, column, label);
        } else {
          table.add(begin, column, "B-" + label);
          table.add(end, column, "E-" + label);
        }
      }

      ++column;
    }

    if (gold != null) {
      for (GroundAtom atom : evaluation.getGold().getGroundAtomsOf(arg)) {
        Pair<Integer, Integer> beginEnd = spans.get(atom.getArguments().get(0).asInt());
        int begin = beginEnd.arg1;
        int end = beginEnd.arg2;
        String label = atom.getArguments().get(1).toString();
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
