package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.UserPredicate;
import thebeast.pml.GroundAtom;
import thebeast.util.Util;
import thebeast.util.HashMultiMapList;
import thebeast.util.HashMultiMapSet;
import thebeast.util.Pair;

import java.io.PrintStream;
import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class MTModel4Printer extends DefaultPrinter {

  private static int BEGIN = 0;
  private static int END = 1;
  private boolean details = true;


  public MTModel4Printer(boolean details) {
    this.details = details;
  }


  public MTModel4Printer() {
  }

  public void print(GroundAtoms atoms, PrintStream out) {
    UserPredicate follows = atoms.getSignature().getUserPredicate("follows");
    UserPredicate source = atoms.getSignature().getUserPredicate("source");
    UserPredicate mapping = atoms.getSignature().getUserPredicate("mapping");
    UserPredicate target = atoms.getSignature().getUserPredicate("target");
    UserPredicate activeTarget = atoms.getSignature().getUserPredicate("activeTarget");
    UserPredicate zeroFerts = atoms.getSignature().getUserPredicate("zeroferts");

    HashMap<Integer, String> sourceToWord = new HashMap<Integer, String>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(source)) {
      sourceToWord.put(atom.getArguments().get(0).asInt(), Util.unquote(atom.getArguments().get(1).toString()));
    }


    HashMap<Integer, String> targetToWord = new HashMap<Integer, String>();
    final HashMap<Integer, Double> targetToProb = new HashMap<Integer, Double>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(target)) {
      targetToWord.put(atom.getArguments().get(0).asInt(), Util.unquote(atom.getArguments().get(1).toString()));
      targetToProb.put(atom.getArguments().get(0).asInt(), atom.getArguments().get(2).asDouble());
    }

    double score = 0;
    HashSet<Integer> active = new HashSet<Integer>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(activeTarget)) {
      int t = atom.getArguments().get(0).asInt();
      active.add(t);
      Double prob = targetToProb.get(t);
      if (prob != null) score += prob;
    }

    HashMap<Integer, Integer> followsMap = new HashMap<Integer, Integer>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(follows)) {
      followsMap.put(atom.getArguments().get(0).asInt(), atom.getArguments().get(1).asInt());
    }

    for (GroundAtom atom : atoms.getGroundAtomsOf("followsScore")) {
      int from = atom.getArguments().get(0).asInt();
      int to = atom.getArguments().get(1).asInt();
      Integer mappedTo = followsMap.get(from);
      if (to == mappedTo)
        score += atom.getArguments().get(2).asDouble();
    }


    HashMap<Pair<Integer, Integer>, String> zeroFertsMap = new HashMap<Pair<Integer, Integer>, String>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(zeroFerts)) {
      zeroFertsMap.put(
              new Pair<Integer, Integer>(atom.getArguments().get(0).asInt(), atom.getArguments().get(1).asInt()),
              Util.unquote(atom.getArguments().get(2).toString()));
    }

    HashMultiMapSet<Integer, Integer> targetToSources = new HashMultiMapSet<Integer, Integer>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(mapping)) {
      targetToSources.add(atom.getArguments().get(1).asInt(), atom.getArguments().get(0).asInt());
    }

    //print source
    for (int i = 0; i < sourceToWord.size(); ++i) {
      if (i > 0) out.print(" ");
      out.print(sourceToWord.get(i));
    }

    out.print("\n");

    out.println(score);

    ArrayList<Integer> targetSentence = new ArrayList<Integer>();
    //print target
    int from = BEGIN;
    int to;
    do {
      to = followsMap.get(from);
      String between = zeroFertsMap.get(new Pair<Integer, Integer>(from, to));
      if (between != null) out.print(between + " ");
      if (to != END) {
        targetSentence.add(to);
        out.print(targetToWord.get(to) + " ");
      }
      from = to;
    } while (from != END);

    out.print("\n\n");

    if (details) {

      for (int i = 0; i < targetSentence.size(); ++i) {
        int targetIndex = targetSentence.get(i);
        String word = targetToWord.get(targetIndex);
        out.printf("%-3d%-4d %-20s\n", i, targetIndex, word);
        for (Integer src : targetToSources.get(targetIndex)) {
          String srcWord = sourceToWord.get(src);
          out.printf("     %-4d %-20s\n", src, srcWord);
        }
      }

      out.println();
      for (int activeTargetIndex : active) {
        if (activeTargetIndex < 0) {
          out.println("Zero fertility targets:");
          for (Integer src : targetToSources.get(activeTargetIndex)) {
            String srcWord = sourceToWord.get(src);
            out.printf("     %-4d %-20s\n", src, srcWord);
          }

        }
      }

      HashMultiMapSet<Set<Integer>, Integer> sourcesToTargets = new HashMultiMapSet<Set<Integer>, Integer>();
      for (Map.Entry<Integer, Set<Integer>> entry : targetToSources.entrySet()) {
        sourcesToTargets.add(entry.getValue(), entry.getKey());
      }

      for (Set<Integer> sourceSet : sourcesToTargets.keySet()) {
        Set<Integer> targets = sourcesToTargets.get(sourceSet);
        List<Integer> sorted = new ArrayList<Integer>(targets);
        Collections.sort(sorted, new Comparator<Integer>() {
          public int compare(Integer o1, Integer o2) {
            double prob1 = targetToProb.get(o1);
            double prob2 = targetToProb.get(o2);
            return prob1 > prob2 ? -1 : prob1 < prob2 ? 1 : 0;
          }
        });
        for (int src : sourceSet) {
          out.print(sourceToWord.get(src) + " ");
        }
        System.out.println();
        for (int k = 0; k < sorted.size() && k < 10; ++k) {
          int tgt = sorted.get(k);
          String word = targetToWord.get(tgt);
          out.printf("   %-20s %-6f\n", word, targetToProb.get(tgt));
        }
      }
    }
  }
}
