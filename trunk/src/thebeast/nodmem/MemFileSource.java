package thebeast.nodmem;

import thebeast.nod.FileSource;
import thebeast.nod.variable.Variable;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemDeserializer;
import thebeast.nodmem.variable.AbstractMemVariable;
import thebeast.nodmem.variable.MemRelationVariable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Sebastian Riedel
 */
public class MemFileSource implements FileSource {
  private MemDeserializer deserializer;
  private ByteBuffer readBuffer;
  public File file;

  public MemFileSource(File file, int bufferSizeInKb) {
    this.file = file;
    readBuffer = ByteBuffer.allocateDirect(bufferSizeInKb * 1024);
    reset();
  }


  public void reset() {
    try {
      deserializer = new MemDeserializer(new FileInputStream(file).getChannel(), readBuffer);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }


  public File file() {
    return file;
  }

  public void read(Variable variable) throws IOException {
    AbstractMemVariable var = (AbstractMemVariable) variable;
    MemChunk chunk = var.getContainerChunk();//.chunkData[var.getPointer().xChunk];
    MemChunk.deserializeInPlace(deserializer, chunk);
    //var.getContainerChunk().chunkData[var.getPointer().xChunk] = MemChunk.deserialize(deserializer);
    if (var instanceof MemRelationVariable) {
      MemRelationVariable memRelationVariable = (MemRelationVariable) var;
      memRelationVariable.indexInformation().updateIndicesFromChunk(chunk.chunkData[var.getPointer().xChunk]);
    }
  }

  public void delete() {
    file.delete();
  }


}
