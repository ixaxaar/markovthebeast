package thebeast.pml.training;

import thebeast.pml.GroundAtoms;
import thebeast.pml.LocalFeatures;
import thebeast.pml.SparseVector;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Feb-2007 Time: 12:28:48
 */
public class TrainingInstances extends LinkedList<TrainingInstance> {

  public void add(GroundAtoms data, LocalFeatures features, SparseVector gold){
    add(new TrainingInstance(data, features, gold));
  }

}
