package thebeast.nodmem;

import thebeast.nod.Dump;
import thebeast.nod.variable.Variable;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemDeserializer;
import thebeast.nodmem.mem.MemSerializer;
import thebeast.nodmem.variable.AbstractMemVariable;
import thebeast.nodmem.variable.MemRelationVariable;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @author Sebastian Riedel
 */
public class MemDump implements Dump {
  private MemSerializer serializer;
  private MemDeserializer deserializer;
  private ByteBuffer writeBuffer, readBuffer;
  private boolean writeMode = true;
  public File file;

  public MemDump(String filename, boolean createNew, int bufferSizeInKb) {
    file = new File(filename);
    if (createNew) file.delete();
    writeBuffer = ByteBuffer.allocateDirect(bufferSizeInKb * 1024);
    readBuffer = ByteBuffer.allocateDirect(bufferSizeInKb * 1024);
    reset();
  }

  private void reset() {
    writeBuffer.position(0);
    try {
      serializer = new MemSerializer(new FileOutputStream(file, true).getChannel(), writeBuffer);
      deserializer = new MemDeserializer(new FileInputStream(file).getChannel(), readBuffer);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void write(Variable variable) throws IOException {
    AbstractMemVariable var = (AbstractMemVariable) variable;
    if (var instanceof MemRelationVariable) {
      MemRelationVariable memRelationVariable = (MemRelationVariable) var;
      memRelationVariable.indexInformation().updateAll();
    }
    MemChunk.serialize(var.getContainerChunk().chunkData[var.getPointer().xChunk], serializer);
  }

  public void read(Variable variable) throws IOException {
    AbstractMemVariable var = (AbstractMemVariable) variable;
    var.getContainerChunk().chunkData[var.getPointer().xChunk] = MemChunk.deserialize(deserializer);
    if (var instanceof MemRelationVariable) {
      MemRelationVariable memRelationVariable = (MemRelationVariable) var;
      memRelationVariable.indexInformation().
              updateIndicesFromChunk(var.getContainerChunk().chunkData[var.getPointer().xChunk]);
    }
  }

  public void delete() {
    file.delete();
  }


  public void close() throws IOException {
    serializer.finish();
  }

  public void flush() throws IOException {
    serializer.flush();
  }
}
