package thebeast.pml.corpora;

import thebeast.pml.Signature;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 12-Feb-2007 Time: 17:33:15
 */
public class ByteArrayCorpus extends TextFormatCorpus {

  private byte[] bytes;

  public ByteArrayCorpus(Signature signature, byte[] bytes) {
    super(signature);
    this.bytes = bytes;
  }

  public InputStream createInputStream() {
    return new ByteArrayInputStream(bytes);
  }

  public OutputStream getOutputStream() {
    throw new UnsupportedOperationException();
  }

}
