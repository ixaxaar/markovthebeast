package thebeast.pml;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;

import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public interface Dumpable {
  int getMemoryUsage();

  void write(FileSink fileSink) throws IOException;

  void read(FileSource fileSource) throws IOException;
}
