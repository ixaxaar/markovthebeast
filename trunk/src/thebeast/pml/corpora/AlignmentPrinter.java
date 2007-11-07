package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.GroundAtom;
import thebeast.pml.Evaluation;
import thebeast.pml.GroundAtomCollection;
import thebeast.util.Pair;
import thebeast.util.Counter;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sebastian Riedel
 */
public class AlignmentPrinter extends DefaultPrinter {
  private static final String CORRECT = "@";
  private static final String FP = "X";
  private static final String FN = "O";
  private static final String NOTHING = ".";

  public void print(GroundAtoms atoms, PrintStream out) {
    out.println(atoms.getGroundAtomsOf("source").toString());
    out.println(atoms.getGroundAtomsOf("target").toString());
    HashSet<Pair<Integer, Integer>> alignments = extractPairs(atoms.getGroundAtomsOf("align"));
    Counter<Integer> srcFert = extractFertilities(atoms.getGroundAtomsOf("srcfert"));
    Counter<Integer> tgtFert = extractFertilities(atoms.getGroundAtomsOf("tgtfert"));
    int rows = atoms.getGroundAtomsOf("target").size();
    int cols = atoms.getGroundAtomsOf("source").size();
    printMatrix(out, rows, cols, srcFert, tgtFert, alignments, CORRECT, null, null, null, null);
  }

  private Counter<Integer> extractFertilities(GroundAtomCollection atoms) {
    Counter<Integer> result = new Counter<Integer>();
    for (GroundAtom atom : atoms){
      result.increment(atom.getArguments().get(0).asInt(), atom.getArguments().get(1).asInt());
    }
    return result;
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
                           Counter<Integer> srcFert, Counter<Integer> tgtFert,
                           Set<Pair<Integer, Integer>> m1, String s1,
                           Set<Pair<Integer, Integer>> m2, String s2,
                           Set<Pair<Integer, Integer>> m3, String s3) {

    out.print("     ");
    for (int col = 0; col < cols; ++col)
      out.print(" " + (col >= 10 ? col / 10 : " "));
    out.println();
    out.print("     ");
    for (int col = 0; col < cols; ++col)
      out.print(" " + col % 10);
    out.println();
    out.println();
    out.print("     ");
    for (int col = 0; col < cols; ++col)
      out.print(" " + srcFert.get(col));
    out.println();
    for (int row = 0; row < rows; ++row) {
      out.printf("%3d %1d ", row, tgtFert.get(row));
      for (int col = 0; col < cols; ++col) {
        if (m1.contains(new Pair<Integer, Integer>(col, row)))
          out.print(s1);
        else if (m2 != null && m2.contains(new Pair<Integer, Integer>(col, row)))
          out.print(s2);
        else if (m3 != null && m3.contains(new Pair<Integer, Integer>(col, row)))
          out.print(s3);
        else
          out.print(NOTHING);
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
    Counter<Integer> srcFert = extractFertilities(evaluation.getGuess().getGroundAtomsOf("srcfert"));
    Counter<Integer> tgtFert = extractFertilities(evaluation.getGuess().getGroundAtomsOf("tgtfert"));
    printMatrix(out, rows, cols, srcFert, tgtFert, fps, FP, fns, FN, gold, CORRECT);

  }
}
