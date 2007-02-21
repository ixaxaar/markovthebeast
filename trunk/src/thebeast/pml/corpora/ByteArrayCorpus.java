package thebeast.pml.corpora;

import thebeast.pml.Signature;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 12-Feb-2007 Time: 17:33:15
 */
public class ByteArrayCorpus extends InputStreamCorpus {

  private byte[] bytes;

  public ByteArrayCorpus(Signature signature, byte[] bytes) {
    super(signature);
    this.bytes = bytes;
  }

  public InputStream createStream() {
    return new ByteArrayInputStream(bytes);
  }
}
