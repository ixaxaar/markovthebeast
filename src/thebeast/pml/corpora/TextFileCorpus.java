package thebeast.pml.corpora;

import thebeast.pml.Signature;
import thebeast.pml.GroundAtoms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ListIterator;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 12-Feb-2007 Time: 16:38:52
 */
public class TextFileCorpus extends InputStreamCorpus {

  private File file;

  public TextFileCorpus(Signature signature, File file) {
    super(signature);
    this.file = file;
  }

  public InputStream createStream() {
    try {
      return new FileInputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }



  public static class Factory implements CorpusFactory {

    public Corpus createCorpus(Signature signature, File file) {
      return new TextFileCorpus(signature, file);
    }
  }

}
