package thebeast.pml.corpora;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.statement.Interpreter;
import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;
import thebeast.pml.TheBeast;

import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * A DumpedCorpus is backed by a binary dump of the data of the corpus. It can be configured to only ever use a certain
 * amount of memory and to stream in and out bits which are needed/not needed.
 *
 * @author Sebastian Riedel
 */
public class DumpedCorpus extends AbstractCollection<GroundAtoms> implements Corpus {

  private ArrayList<GroundAtoms> active;
  private int size;
  private int byteSize = 0;
  private FileSource fileSource;
  private int activeCount;
  private Signature signature;
  private boolean iterating = false;
  private boolean verbose = false;
  private boolean loadedFromFile = false;
  private File file;

  public DumpedCorpus(Signature signature, File file, int maxByteSize) {
    this.file = file;
    this.signature = signature;
    try {
      fileSource = TheBeast.getInstance().getNodServer().createSource(file, 1024);
      IntVariable sizeVar = TheBeast.getInstance().getNodServer().interpreter().createIntVariable();
      fileSource.read(sizeVar);
      size = sizeVar.value().getInt();
      byteSize = 0;
      active = new ArrayList<GroundAtoms>(10000);
      for (int i = 0; i < size && byteSize < maxByteSize; ++i) {
        GroundAtoms atoms = signature.createGroundAtoms();
        atoms.read(fileSource);
        byteSize += atoms.getMemoryUsage();
        active.add(atoms);
      }
      activeCount = active.size();
      loadedFromFile = true;
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public DumpedCorpus(File file, Corpus corpus, int from, int to, int maxByteSize) throws IOException {
    this.file = file;
    FileSink fileSink = TheBeast.getInstance().getNodServer().createSink(file, 1024);
    active = new ArrayList<GroundAtoms>(to - from);
    this.signature = corpus.getSignature();
    Iterator<GroundAtoms> iter = corpus.iterator();
    for (int i = 0; i < from; ++i) iter.next();
    this.size = to - from;
    ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();
    builder.num(size);
    Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
    fileSink.write(interpreter.createIntVariable(builder.getInt()));

    int numDumps = 0;
    for (int i = 0; i < size; ++i) {
      GroundAtoms atoms = iter.next();
      int memUsage = atoms.getMemoryUsage();
      if (byteSize + memUsage > maxByteSize) {
        activeCount += active.size();
        dump(fileSink);
        ++numDumps;
        byteSize = 0;
      }
      active.add(atoms);
      byteSize += memUsage;
    }
    activeCount = numDumps == 0 ? size : activeCount / numDumps;
    dump(fileSink);
    for (int i = 0; i < activeCount; ++i)
      active.add(signature.createGroundAtoms());
    fileSink.flush();
    fileSource = TheBeast.getInstance().getNodServer().createSource(file, 1024);
  }

  public DumpedCorpus(File file, Corpus corpus, int maxByteSize) throws IOException {
    this.file = file;
    FileSink fileSink = TheBeast.getInstance().getNodServer().createSink(file, 1024);
    active = new ArrayList<GroundAtoms>(10000);
    this.signature = corpus.getSignature();
    this.size = corpus.size();
    ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();
    builder.num(size);
    Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
    fileSink.write(interpreter.createIntVariable(builder.getInt()));
    int numDumps = 0;
    for (GroundAtoms atoms : corpus) {
      int memUsage = atoms.getMemoryUsage();
      if (byteSize + memUsage > maxByteSize) {
        activeCount += active.size();
        dump(fileSink);
        ++numDumps;
        byteSize = 0;
      }
      active.add(atoms);
      byteSize += memUsage;
      //++size;
    }
    activeCount = numDumps == 0 ? size : activeCount / numDumps;
    dump(fileSink);
    for (int i = 0; i < activeCount; ++i)
      active.add(signature.createGroundAtoms());
    fileSink.flush();
    fileSource = TheBeast.getInstance().getNodServer().createSource(file, 1024);
  }


  private void dump(FileSink fileSink) throws IOException {
    for (GroundAtoms atoms : active) {
      atoms.write(fileSink);
    }
    active.clear();
    if (verbose) System.out.print(">");
  }

  public String toString() {
    return "DumpedCorpus size :" + size + " activeCount: " + activeCount;
  }

  public int getActiveCount(){
    return activeCount;
  }

  public synchronized Iterator<GroundAtoms> iterator() {
    if (iterating)
      throw new RuntimeException("Dumped Corpus can only have one active iterator at a time!");
    try {
      iterating = true;
      if (!loadedFromFile) {
        //todo: why does reset not work?
        fileSource = TheBeast.getInstance().getNodServer().createSource(file, 1024);
        fileSource.read(TheBeast.getInstance().getNodServer().interpreter().createIntVariable());
      }
      if (activeCount >= size) {
        if (!loadedFromFile) for (int i = 0; i < size; ++i) {
          GroundAtoms atoms = active.get(i);
          atoms.read(fileSource);
        }
        if (verbose) System.out.print("<");
        iterating = false;
        loadedFromFile = false;        
        return active.subList(0, size).iterator();
      } else {
        if (!loadedFromFile) for (GroundAtoms atoms : active) {
          atoms.read(fileSource);
        }
        loadedFromFile = false;
        return new Iterator<GroundAtoms>() {
          Iterator<GroundAtoms> delegate = active.iterator();
          int current = 0;

          public boolean hasNext() {
            return current < size;
          }

          public GroundAtoms next() {
            //System.out.println(current + " of " + size);
            if (delegate.hasNext()) {
              ++current;
              if (!hasNext()) {
                iterating = false;
              }
              return delegate.next();
            }
            update();
            ++current;
            if (!hasNext()) {
              iterating = false;
            }
            return delegate.next();
          }

          public void update() {
            try {
              if (activeCount >= size - current) {
                for (int i = 0; i < size - current; ++i) {
                  GroundAtoms atoms = active.get(i);
                  atoms.read(fileSource);
                }
                delegate = active.subList(0, size - current).iterator();
              } else {
                for (GroundAtoms atoms : active) {
                  atoms.read(fileSource);
                }
                delegate = active.iterator();
              }
              if (verbose) System.out.print("<");
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
    int usage = 0;
    for (GroundAtoms atoms : active)
      usage += atoms.getMemoryUsage();
    return usage;
  }
}
