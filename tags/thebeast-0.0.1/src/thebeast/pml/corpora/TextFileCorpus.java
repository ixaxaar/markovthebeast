package thebeast.pml.corpora;

import thebeast.pml.Signature;

import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 12-Feb-2007 Time: 16:38:52
 */
public class TextFileCorpus extends TextFormatCorpus {

  private File file;

  public TextFileCorpus(Signature signature, File file) {
    super(signature);
    this.file = file;
  }

  public InputStream createInputStream() {
    try {
      return new FileInputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public OutputStream getOutputStream() {
    try {
      return new FileOutputStream(file, true);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public int getUsedMemory() {
    return 0;
  }


  public static class Factory implements CorpusFactory {

    public Corpus createCorpus(Signature signature, File file) {
      return new TextFileCorpus(signature, file);
    }


  }

}
