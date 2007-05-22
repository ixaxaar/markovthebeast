package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;
import thebeast.pml.Model;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 22-May-2007 Time: 17:38:52
 */
public class AuxilaryCorpus extends AbstractCollection<GroundAtoms> implements Corpus{

  private Model model;
  private Corpus delegate;

  public AuxilaryCorpus(Model model, Corpus delegate) {
    this.model = model;
    this.delegate = delegate;
  }

  public Iterator<GroundAtoms> iterator() {
    return null;
  }

  public int size() {
    return 0;
  }

  public Signature getSignature() {
    return model.getSignature();
  }

  public int getUsedMemory() {
    return 0;
  }

  public void append(GroundAtoms atoms) {

  }
}
