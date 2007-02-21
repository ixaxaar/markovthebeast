package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 08-Feb-2007 Time: 20:57:22
 */
public class RandomAccessCorpus extends ArrayList<GroundAtoms> implements Corpus {

  private Signature signature;

  public RandomAccessCorpus(Signature signature, Collection<GroundAtoms> instances) {
    super(instances);
    this.signature = signature;
  }


  public RandomAccessCorpus(Corpus c) {
    super(c);
    this.signature = c.getSignature();
  }

  public RandomAccessCorpus(Signature signature, int capacity) {
    super(capacity);
    this.signature = signature;
  }

  public Signature getSignature() {
    return signature;
  }

  public GroundAtoms createAndAdd(){
    GroundAtoms result = signature.createGroundAtoms();
    add(result);
    return result;
  }



}
