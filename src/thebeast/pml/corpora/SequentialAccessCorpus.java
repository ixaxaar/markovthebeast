package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 08-Feb-2007 Time: 20:57:22
 */
public class SequentialAccessCorpus extends LinkedList<GroundAtoms> implements Corpus {

  private Signature signature;

  public SequentialAccessCorpus(Signature signature, Collection<GroundAtoms> instances) {
    this.signature = signature;
    addAll(instances);
  }

  public SequentialAccessCorpus(Corpus instances) {
    this.signature = instances.getSignature();
    addAll(instances);
  }


  public SequentialAccessCorpus(Signature signature) {
    this.signature = signature;
  }

  public Signature getSignature() {
    return signature;
  }

  public int getUsedMemory() {
    int bytesize = 0;
    for (GroundAtoms atoms : this)
      bytesize += atoms.getUsedMemory();
    return bytesize;
  }

  public GroundAtoms createAndAdd(){
    GroundAtoms result = signature.createGroundAtoms();
    add(result);
    return result;
  }



}
