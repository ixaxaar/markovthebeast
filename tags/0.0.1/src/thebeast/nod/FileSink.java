package thebeast.nod;

import thebeast.nod.variable.Variable;

import java.io.IOException;
import java.io.File;

/**
 * @author Sebastian Riedel
 */
public interface FileSink {

  File file();
  void write(Variable variable) throws IOException;
  void write(Variable variable, boolean writeIndices) throws IOException;
  void close() throws IOException;
  void flush() throws IOException;
  void reset();
}
