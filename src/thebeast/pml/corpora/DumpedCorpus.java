package thebeast.pml.corpora;

import thebeast.nod.Dump;
import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * @author Sebastian Riedel
 */
public class DumpedCorpus extends AbstractCollection<GroundAtoms> implements Corpus {

  private ArrayList<GroundAtoms> active;
  private int size;
  private int byteSize = 0;
  private Dump dump;
  private int activeCount;
  private Signature signature;

  public DumpedCorpus(Dump dump, Corpus corpus, int from, int to, int maxByteSize) throws IOException {
    this.dump = dump;
    active = new ArrayList<GroundAtoms>(to - from);
    this.signature = corpus.getSignature();
    Iterator<GroundAtoms> iter = corpus.iterator();
    for (int i = 0; i < from; ++i) iter.next();
    this.size = to - from;
    int numDumps = 0;
    for (int i = 0; i < size; ++i) {
      GroundAtoms atoms = iter.next();
      int memUsage = atoms.getMemoryUsage();
      if (byteSize + memUsage > maxByteSize) {
        activeCount += active.size();
        dump();
        ++numDumps;
        byteSize = 0;
      }
      active.add(atoms);
      byteSize += memUsage;
    }
    activeCount = activeCount / numDumps;
    dump();
    for (int i = 0; i < activeCount; ++i)
      active.add(signature.createGroundAtoms());
  }

  private void dump() throws IOException {
    for (GroundAtoms atoms : active) {
      atoms.write(dump);
    }
    active.clear();
  }

  public Iterator<GroundAtoms> iterator() {
    //fill up initial actives
    try {
      if (activeCount >= size) {
        for (int i = 0; i < size; ++i) {
          GroundAtoms atoms = active.get(i);
          atoms.read(dump);
        }
        return active.subList(0, size).iterator();
      } else {
        for (GroundAtoms atoms : active) {
          atoms.read(dump);
        }
        return new Iterator<GroundAtoms>() {
          Iterator<GroundAtoms> delegate = active.iterator();
          int current = 0;

          public boolean hasNext() {
            return current < size;
          }

          public GroundAtoms next() {
            ++current;
            if (delegate.hasNext()) return delegate.next();
            update();
            return delegate.next();
          }

          public void update() {
            try {
              if (activeCount >= size - current) {
                for (int i = 0; i < size - current; ++i) {
                  GroundAtoms atoms = active.get(i);
                  atoms.read(dump);
                }
                delegate = active.subList(0, size - current).iterator();
              } else {
                for (GroundAtoms atoms : active) {
                  atoms.read(dump);
                }
                delegate = active.iterator();
              }
            } catch (IOException e) {
              e.printStackTrace();
            }

          }

          public void remove() {

          }
        };
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public int size() {
    return size;
  }

  public Signature getSignature() {
    return signature;
  }

  public ListIterator<GroundAtoms> listIterator() {
    return null;
  }

  public int getUsedMemory() {
    return 0;
  }
}
