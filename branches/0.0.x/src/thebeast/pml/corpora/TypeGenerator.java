package thebeast.pml.corpora;

import thebeast.pml.Signature;

import java.io.InputStream;

/**
 * @author Sebastian Riedel
 */
public interface TypeGenerator {
  void generateTypes(InputStream is, Signature signature);
}
