package thebeast.nodmem;

import thebeast.nod.FileSink;
import thebeast.nod.variable.Variable;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemDeserializer;
import thebeast.nodmem.mem.MemSerializer;
import thebeast.nodmem.variable.AbstractMemVariable;
import thebeast.nodmem.variable.MemRelationVariable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Sebastian Riedel
 */
public class MemFileSink implements FileSink {
  private MemSerializer serializer;
  private MemDeserializer deserializer;
  private ByteBuffer writeBuffer;
  private boolean writeMode = true;
  public File file;

  public MemFileSink(File file, int bufferSizeInKb)  {
    this.file = file;
    writeBuffer = ByteBuffer.allocateDirect(bufferSizeInKb * 1024);
    reset();
  }


  public void reset() {
    writeBuffer.position(0);
    try {
      serializer = new MemSerializer(new FileOutputStream(file, true).getChannel(), writeBuffer);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public File file() {
    return file;
  }

  public void write(Variable variable, boolean writeIndices) throws IOException {
    AbstractMemVariable var = (AbstractMemVariable) variable;
    if (var instanceof MemRelationVariable && writeIndices) {
      MemRelationVariable memRelationVariable = (MemRelationVariable) var;
      memRelationVariable.indexInformation().updateAll();
    }
    MemChunk chunk = var.getContainerChunk().chunkData[var.getPointer().xChunk];
    MemChunk.serialize(chunk, serializer, writeIndices);
  }

  public void write(Variable variable) throws IOException {
    write(variable,true);    
  }

  public void close() throws IOException {
    serializer.finish();
  }

  public void flush() throws IOException {
    serializer.flush();
  }
}
