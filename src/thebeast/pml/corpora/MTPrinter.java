package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.UserPredicate;
import thebeast.pml.GroundAtom;
import thebeast.pml.term.IntConstant;
import thebeast.util.Util;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class MTPrinter implements GroundAtomsPrinter {

  private static int BEGIN = 0;
  private static int END = 1;

  public void print(GroundAtoms atoms, PrintStream out) {
    UserPredicate follows = atoms.getSignature().getUserPredicate("follows");
    UserPredicate source = atoms.getSignature().getUserPredicate("source");
    UserPredicate target = atoms.getSignature().getUserPredicate("target");

    HashMap<Integer, String> sourceToWord = new HashMap<Integer, String>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(source)){
      sourceToWord.put(atom.getArguments().get(0).asInt(), Util.unquote(atom.getArguments().get(1).toString()));
    }

    HashMap<Integer, String> targetToWord = new HashMap<Integer, String>();
    for (GroundAtom atom : atoms.getGroundAtomsOf(target)){
      targetToWord.put(atom.getArguments().get(1).asInt(), Util.unquote(atom.getArguments().get(2).toString()));      
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

    //print target
    int from = BEGIN;
    do {
      if (from != BEGIN){
        out.print(targetToWord.get(from));
        out.print(" ");
      }
      from = followsMap.get(from);
    } while(from != END);

    out.print("\n");

  }
}
