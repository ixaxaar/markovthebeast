package thebeast.pml.corpora;

import thebeast.pml.GroundAtom;
import thebeast.pml.GroundAtoms;
import thebeast.pml.UserPredicate;
import thebeast.util.HashMultiMapSet;
import thebeast.util.Util;
import thebeast.util.HashMultiMapList;

import java.io.PrintStream;
import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class MTPrinter extends DefaultPrinter {

  private static int BEGIN = 0;
  private static int END = 1;

  public void print(GroundAtoms atoms, PrintStream out) {
    UserPredicate follows = atoms.getSignature().getUserPredicate("follows");
    UserPredicate source = atoms.getSignature().getUserPredicate("source");
    UserPredicate target = atoms.getSignature().getUserPredicate("target");
    UserPredicate activeGroup = atoms.getSignature().getUserPredicate("activeGroup");
    UserPredicate group = atoms.getSignature().getUserPredicate("group");
    UserPredicate groupScore = atoms.getSignature().getUserPredicate("groupScore");

    HashMap<Integer, String> sourceToWord = new HashMap<Integer, String>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(source)){
      sourceToWord.put(atom.getArguments().get(0).asInt(), Util.unquote(atom.getArguments().get(1).toString()));
    }

    HashSet<Integer> active = new HashSet<Integer>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(activeGroup)){
      active.add(atom.getArguments().get(0).asInt());
    }

    final HashMap<Integer,Double> scores = new HashMap<Integer, Double>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(groupScore)){
      scores.put(atom.getArguments().get(0).asInt(),atom.getArguments().get(1).asDouble());
    }

    HashMap<Integer,Integer> groupToSource = new HashMap<Integer, Integer>();
    HashMap<Integer,Integer> sourceToActiveGroup = new HashMap<Integer, Integer>();
    HashMultiMapList<Integer,Integer> sourceToGroups = new HashMultiMapList<Integer, Integer>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(group)){
      groupToSource.put(atom.getArguments().get(1).asInt(), atom.getArguments().get(0).asInt());
      sourceToGroups.add(atom.getArguments().get(0).asInt(), atom.getArguments().get(1).asInt());
      if (active.contains(atom.getArguments().get(1).asInt())){
        sourceToActiveGroup.put(atom.getArguments().get(0).asInt(), atom.getArguments().get(1).asInt());
      }
    }
    for (List<Integer> targets : sourceToGroups.values()){
      Collections.sort(targets, new Comparator<Integer>() {
        public int compare(Integer o1, Integer o2) {
          double score1 = scores.get(o1);
          double score2 = scores.get(o2);
          return score1 > score2 ? -1 : score1 < score2 ? 1 : 0; 
        }
      });
    }



    HashMap<Integer, String> targetToWord = new HashMap<Integer, String>();
    HashMap<Integer, Integer> targetToGroup = new HashMap<Integer, Integer>();
    HashMultiMapSet<Integer,Integer> groupToTargets = new HashMultiMapSet<Integer, Integer>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(target)){
      targetToWord.put(atom.getArguments().get(1).asInt(), Util.unquote(atom.getArguments().get(2).toString()));      
      targetToGroup.put(atom.getArguments().get(1).asInt(), atom.getArguments().get(0).asInt());
      groupToTargets.add(atom.getArguments().get(0).asInt(), atom.getArguments().get(1).asInt());
    }

    HashMap<Integer, Integer> followsMap = new HashMap<Integer, Integer>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(follows)){
      followsMap.put(atom.getArguments().get(0).asInt(), atom.getArguments().get(1).asInt());
    }




    //print source
    for (int i = 0; i < sourceToWord.size();++i){
      if (i > 0) out.print(" ");
      out.print(sourceToWord.get(i));
    }

    out.print("\n\n");

    ArrayList<Integer> targetSentence = new ArrayList<Integer>();
    //print target
    int from = BEGIN;
    do {
      if (from != BEGIN){
        out.print(targetToWord.get(from));
        out.print(" ");
      }
      from = followsMap.get(from);
      if (from!=END) targetSentence.add(from);
    } while(from != END);

    out.print("\n");

    //print source table and which groups where used
    for (int i = 0; i < sourceToWord.size();++i){
      String word = sourceToWord.get(i);
      out.printf("%-3d%-20s",i,word);
      int activeGroupIndex = sourceToActiveGroup.get(i);
      for (int t : groupToTargets.get(activeGroupIndex)){
        out.print(targetToWord.get(t) + " ");        
      }
      out.println();
      for (int g : sourceToGroups.get(i)){
        out.printf("%25s%-5f: ",g==activeGroupIndex ? "->  " : "",scores.get(g));
        int targetIndex = 0;
        for (int t : groupToTargets.get(g)){
          if (targetIndex++ > 0) out.print(",");
          out.print(targetToWord.get(t));
        }
        out.println();
      }
    }

    out.print("\n");

    //print target as a table and show which sources where used
    for (int i = 0; i < targetSentence.size(); ++i){
      int targetIndex = targetSentence.get(i);
      String word = targetToWord.get(targetIndex);
      int src = groupToSource.get(targetToGroup.get(targetIndex));
      String srcWord = sourceToWord.get(src);
      out.printf("%-3d%-20s%-3d%-20s",i,word,src,srcWord);
      out.println();      
    }




  }

 
}
