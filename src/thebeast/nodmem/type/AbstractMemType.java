package thebeast.nodmem.type;

import thebeast.nod.type.Type;
import thebeast.nod.value.Value;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.mem.MemDim;
import thebeast.nodmem.value.AbstractMemValue;

import java.io.StreamTokenizer;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public abstract class AbstractMemType implements Type {

  //private int numIntCols, numDoubleCols, numChunkCols;

  private MemDim dim;

  public void load(StreamTokenizer src, MemChunk dst, MemVector ptr) throws IOException {

  }

  public void load(String src, MemChunk dst, MemVector ptr) throws IOException {

  }


  public enum DataType {
    INT, DOUBLE, CHUNK
  }

  private DataType dataType;

  protected AbstractMemType(DataType dataType, int numIntCols, int numDoubleCols, int numChunkCols) {
    this.dataType = dataType;
    dim = MemDim.create(numIntCols,numDoubleCols,numChunkCols);
  }

  protected AbstractMemType(DataType dataType, MemDim dim) {
    this.dataType = dataType;
    this.dim = dim;
  }


  public DataType getDataType() {
    return dataType;
  }

  protected AbstractMemType(DataType dataType) {
    this.dataType = dataType;
  }

  protected AbstractMemType() {
    dataType = DataType.CHUNK;
  }

  protected void setDim(int numIntCols, int numDoubleCols, int numChunkCols){
    dim = MemDim.create(numIntCols,numDoubleCols, numChunkCols);
  }

  protected void setDim(MemDim dim){
    this.dim = dim;
  }

  public MemDim getDim() {
    return dim;
  }

  

  public abstract Value emptyValue();

  public abstract AbstractMemValue valueFromChunk(MemChunk chunk, MemVector pointer);

}
