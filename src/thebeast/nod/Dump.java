package thebeast.nod;

import thebeast.nod.variable.Variable;

import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public interface Dump {

  void write(Variable variable) throws IOException;
  void read(Variable variable) throws IOException;
  void delete();
  void close() throws IOException;
  void flush() throws IOException;

}
