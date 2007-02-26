package thebeast.pml.corpora;

import thebeast.pml.Signature;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 12-Feb-2007 Time: 22:02:25
 */
public interface CorpusFactory {

  Corpus createCorpus(Signature signature, File file);

}
