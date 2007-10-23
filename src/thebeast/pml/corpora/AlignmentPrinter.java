package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.GroundAtom;
import thebeast.pml.Evaluation;
import thebeast.pml.GroundAtomCollection;
import thebeast.util.Pair;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sebastian Riedel
 */
public class AlignmentPrinter extends DefaultPrinter {
  public void print(GroundAtoms atoms, PrintStream out) {
    out.println(atoms.getGroundAtomsOf("source").toString());
    out.println(atoms.getGroundAtomsOf("target").toString());
    HashSet<Pair<Integer, Integer>> alignments = extractPairs(atoms.getGroundAtomsOf("align"));
    int rows = atoms.getGroundAtomsOf("target").size();
    int cols = atoms.getGroundAtomsOf("source").size();
    printMatrix(out, rows, cols, alignments, "*", null, null, null, null);
  }

  private HashSet<Pair<Integer, Integer>> extractPairs(GroundAtomCollection atoms) {
    HashSet<Pair<Integer, Integer>> alignments = new HashSet<Pair<Integer, Integer>>();
    for (GroundAtom atom : atoms) {
      alignments.add(new Pair<Integer, Integer>(
              atom.getArguments().get(0).asInt(), atom.getArguments().get(1).asInt()));
    }
    return alignments;
  }

  private void printMatrix(PrintStream out, int rows, int cols,
                           Set<Pair<Integer, Integer>> m1, String s1,
                           Set<Pair<Integer, Integer>> m2, String s2,
                           Set<Pair<Integer, Integer>> m3, String s3) {

    out.print("   ");
    for (int col = 0; col < cols; ++col)
      out.print(" " + (col >= 10 ? col / 10 : " "));
    out.println();
    out.print("   ");
    for (int col = 0; col < cols; ++col)
      out.print(" " + col % 10);
    out.println();
    for (int row = 0; row < rows; ++row) {
      out.printf("%3d ", row);
      for (int col = 0; col < cols; ++col) {
        if (m1.contains(new Pair<Integer, Integer>(row, col)))
          out.print(s1);
        else if (m2 != null && m2.contains(new Pair<Integer, Integer>(row, col)))
          out.print(s2);
        else if (m3 != null && m3.contains(new Pair<Integer, Integer>(row, col)))
          out.print(s3);
        else
          out.print(" ");
        out.print(" ");
      }
      out.println();
    }

  }


  public void printEval(Evaluation evaluation, PrintStream out) {
    super.printEval(evaluation, out);
    HashSet<Pair<Integer, Integer>> gold = extractPairs(evaluation.getGold().getGroundAtomsOf("align"));
    int rows = evaluation.getGold().getGroundAtomsOf("target").size();
    int cols = evaluation.getGold().getGroundAtomsOf("source").size();
    HashSet<Pair<Integer, Integer>> fps = extractPairs(evaluation.getFalsePositives().getGroundAtomsOf("align"));
    HashSet<Pair<Integer, Integer>> fns = extractPairs(evaluation.getFalseNegatives().getGroundAtomsOf("align"));
    printMatrix(out, rows, cols, fps, "X", fns, "O", gold, "*");

  }
}
