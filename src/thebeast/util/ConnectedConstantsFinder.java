package thebeast.util;

import thebeast.pml.term.Constant;
import thebeast.pml.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: s0349492
 * Date: 19-Sep-2008
 * Time: 21:32:09
 * To change this template use File | Settings | File Templates.
 */
public class ConnectedConstantsFinder {

  /**
   * Uses binary hidden predicates to find sets of connected constants.
   * @param atoms the input atoms
   * @param model the model which specifies which predicates are hidden.
   * @return a set of sets of connected constants.
   */
  public static Set<Set<Constant>> findConnectedConstants(GroundAtoms atoms, Model model) {
    HashMap<Constant,Set<Constant>>
            constant2ConnectedConstants = new HashMap<Constant, Set<Constant>>();

    for (UserPredicate pred : model.getInstancePredicates()) {
      if (pred.getArity() == 2) {
        for (GroundAtom atom : atoms.getGroundAtomsOf(pred)) {
          Constant c1 = atom.getArguments().get(0);
          Constant c2 = atom.getArguments().get(1);
          Set<Constant> connected1 = constant2ConnectedConstants.get(c1);
          Set<Constant> connected2 = constant2ConnectedConstants.get(c2);
          Set<Constant> newConnected;
          if (connected1 == null && connected2 == null){
            newConnected = new HashSet<Constant>();
            newConnected.add(c1);
            newConnected.add(c2);
          } else if (connected1 == null){
            newConnected = connected2;
            newConnected.add(c1);
          } else if (connected2 == null){
            newConnected = connected1;
            newConnected.add(c2);
          } else {
            newConnected = connected1;
            connected1.addAll(connected2);
          }
          constant2ConnectedConstants.put(c1,newConnected);
          constant2ConnectedConstants.put(c2,newConnected);
        }
      }
    }
    return new HashSet<Set<Constant>>(constant2ConnectedConstants.values());
  }

  public static void main(String[] args) {
    Signature erSig = TheBeast.getInstance().createSignature();
    erSig.createType("Bib", false, "Bib1", "Bib2", "Bib3", "Bib4", "Bib5", "Bib6");
    Model model = erSig.createModel();
    erSig.createPredicate("sameBib", "Bib", "Bib");
    model.addHiddenPredicate(erSig.getUserPredicate("sameBib"));

    GroundAtoms atoms = erSig.createGroundAtoms();
    atoms.getGroundAtomsOf("sameBib").addGroundAtom("Bib1", "Bib2");
    atoms.getGroundAtomsOf("sameBib").addGroundAtom("Bib2", "Bib3");
    atoms.getGroundAtomsOf("sameBib").addGroundAtom("Bib3", "Bib4");
    atoms.getGroundAtomsOf("sameBib").addGroundAtom("Bib5", "Bib6");

    System.out.println(findConnectedConstants(atoms, model));



  }


}
