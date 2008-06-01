package thebeast.nod;

import thebeast.nod.variable.Variable;

import java.io.IOException;
import java.io.File;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 26-Feb-2007 Time: 14:56:32
 */
public interface FileSource {

  File file();
  void read(Variable variable) throws IOException;
  void reset();

}
