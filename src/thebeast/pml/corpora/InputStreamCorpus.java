package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;

import java.io.*;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 12-Feb-2007 Time: 16:38:52
 */
public abstract class InputStreamCorpus extends AbstractCollection<GroundAtoms> implements Corpus {

  private Signature signature;
  private int size;
  private boolean sizeKnown = false;


  public InputStreamCorpus(Signature signature) {
    this.signature = signature;
  }

  public abstract InputStream createStream();

  public Iterator<GroundAtoms> iterator() {
    return new InputStreamCorpus.GroundAtomsIterator(
            new BufferedReader(new InputStreamReader(createStream())));
  }

  public ListIterator<GroundAtoms> listIterator() {
    return null;
  }

  public int size() {
    if (!sizeKnown) determineSize();
    return size;
  }

  private void determineSize() {
    size = 0;
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(createStream()));
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (line.startsWith(">>")) ++size;
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    sizeKnown = true;
  }

  public Signature getSignature() {
    return signature;
  }

  private class GroundAtomsIterator implements Iterator<GroundAtoms> {

    private BufferedReader reader;
    private boolean hasNext = false;
    private GroundAtoms next;

    public GroundAtomsIterator(BufferedReader reader) {
      if (!sizeKnown) size = 0;
      this.reader = reader;
      //look for the first ">>";
      try {
        String line;
        for (line = reader.readLine(); line != null; line = reader.readLine()) {
          if (line.startsWith(">>")) break;
        }
        if (line == null) {
          size = 0;
          hasNext = false;
          sizeKnown = true;
        } else {
          hasNext = true;
        }

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    private void update() {
      StringBuffer buffer = new StringBuffer();
      try {
        String line;
        for (line = reader.readLine(); line != null; line = reader.readLine()) {
          if (line.startsWith(">>")) break;
          buffer.append(line).append("\n");
        }
        next = signature.createGroundAtoms();
        next.load(buffer.toString());
        if (line == null) {
          hasNext = false;
          sizeKnown = true;
        } else {
          hasNext = true;
          if (!sizeKnown) ++size;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public boolean hasNext() {
      return hasNext;
    }

    public GroundAtoms next() {
      update();
      return next;
    }

    public void remove() {

    }
  }


}
