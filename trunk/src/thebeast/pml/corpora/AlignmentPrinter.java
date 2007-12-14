package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.GroundAtom;
import thebeast.pml.Evaluation;
import thebeast.pml.GroundAtomCollection;
import thebeast.util.Pair;
import thebeast.util.Counter;
import thebeast.util.Util;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class AlignmentPrinter extends DefaultPrinter {
  private static final String CORRECT = "@";
  private static final String FP = "X";
  private static final String FN = "O";
  private static final String NOTHING = ".";

  public void print(GroundAtoms atoms, PrintStream out) {
    //out.println(atoms.getGroundAtomsOf("source").toString());
    //out.println(atoms.getGroundAtomsOf("target").toString());
    HashSet<Pair<Integer, Integer>> alignments = extractPairs(atoms.getGroundAtomsOf("align"));
    Counter<Integer> srcFert = extractFertilities(atoms.getGroundAtomsOf("srcfert"));
    Counter<Integer> tgtFert = extractFertilities(atoms.getGroundAtomsOf("tgtfert"));
    int rows = atoms.getGroundAtomsOf("target").size();
    int cols = atoms.getGroundAtomsOf("source").size();
    printMatrix(out, rows, cols, atoms, srcFert, tgtFert, alignments, CORRECT, null, null, null, null);
  }

  private Counter<Integer> extractFertilities(GroundAtomCollection atoms) {
    Counter<Integer> result = new Counter<Integer>();
    for (GroundAtom atom : atoms) {
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
                           GroundAtoms atoms,
                           Counter<Integer> srcFert, Counter<Integer> tgtFert,
                           Set<Pair<Integer, Integer>> m1, String s1,
                           Set<Pair<Integer, Integer>> m2, String s2,
                           Set<Pair<Integer, Integer>> m3, String s3) {

    int maxTargetWordLength = 0;
    Map<Integer,String> targetWords = new HashMap<Integer, String>();
    for (GroundAtom atom: atoms.getGroundAtomsOf("target")){
      int index = atom.getArguments().get(0).asInt();
      String word = Util.unquote(atom.getArguments().get(1).toString());
      if (word.length() > maxTargetWordLength) maxTargetWordLength = word.length();
      targetWords.put(index,word);
    }

    Map<Integer,String> targetTags = new HashMap<Integer, String>();
    for (GroundAtom atom: atoms.getGroundAtomsOf("targetpos")){
      int index = atom.getArguments().get(0).asInt();
      String word = Util.unquote(atom.getArguments().get(1).toString());
      targetTags.put(index,word);
    }

    Map<Integer,Integer> targetHeads = new HashMap<Integer, Integer>();
    for (GroundAtom atom: atoms.getGroundAtomsOf("targethead")){
      int index = atom.getArguments().get(0).asInt();
      Integer head = atom.getArguments().get(1).asInt();
      targetHeads.put(index,head);
    }

    Map<Integer,String> sourceHeads = new HashMap<Integer, String>();
    for (GroundAtom atom: atoms.getGroundAtomsOf("sourcehead")){
      int index = atom.getArguments().get(0).asInt();
      String head = String.valueOf(atom.getArguments().get(1).asInt());
      sourceHeads.put(index,head);
    }

    Map<Integer,String> targetChunks = new HashMap<Integer, String>();
    for (GroundAtom atom: atoms.getGroundAtomsOf("targetchunk")){
      int index = atom.getArguments().get(0).asInt();
      String word = Util.unquote(atom.getArguments().get(1).toString());
      targetChunks.put(index,word);
    }

    int maxSourceWordLength = 0;
    Map<Integer,String> sourceWords = new HashMap<Integer, String>();
    for (GroundAtom atom: atoms.getGroundAtomsOf("source")){
      int index = atom.getArguments().get(0).asInt();
      String word = Util.unquote(atom.getArguments().get(1).toString());
      if (word.length() > maxSourceWordLength) maxSourceWordLength = word.length();
      sourceWords.put(index,word);
    }

    printVertical(out, 2, maxTargetWordLength, cols, sourceHeads);
    out.println();
    printVertical(out, maxSourceWordLength, maxTargetWordLength, cols, sourceWords);
    out.println();
    out.printf("%" + maxTargetWordLength +"s %19s","","");
    for (int col = 0; col < cols; ++col)
      out.print(" " + (col >= 10 ? col / 10 : " "));
    out.println();
    out.printf("%" + maxTargetWordLength +"s %19s","","");
    for (int col = 0; col < cols; ++col)
      out.print(" " + col % 10);
    out.println();
    out.println();
    for (int row = 0; row < rows; ++row) {
      out.printf("%3d %-" + maxTargetWordLength + "s %-4s %-6s %-3d ", row,
              targetWords.get(row),targetTags.get(row), targetChunks.get(row),targetHeads.get(row));
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

  private void printVertical(PrintStream out, int maxSourceWordLength, int offset, int cols, Map<Integer, String> words) {
    for (int srcIndex = 0; srcIndex < maxSourceWordLength; ++srcIndex){
      out.printf("%" + offset +"s %19s","","");
      for (int col = 0; col < cols; ++col){
        String srcWord = words.get(col);
        if (srcIndex < srcWord.length()) out.print(" " + srcWord.charAt(srcIndex));
        else out.print("  ");
      }
      out.println();
    }
  }

  private void printDiagonal(PrintStream out, int maxSourceWordLength, int maxTargetWordLength, int cols, Map<Integer, String> words) {
    for (int row = 0; row < cols; ++row){
      out.printf("%" + maxTargetWordLength +"s %17s","","");
      for (int col = 0; col < row; ++col)
        out.print("  ");
      out.println(words.get(row));
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
    printMatrix(out, rows, cols, evaluation.getGold(), srcFert, tgtFert, fps, FP, fns, FN, gold, CORRECT);

  }
}
