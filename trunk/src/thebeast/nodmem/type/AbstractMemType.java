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

  private int numIntCols, numDoubleCols, numChunkCols;

  public void load(StreamTokenizer src, MemChunk dst, MemVector ptr) throws IOException {

  }

  public enum DataType {
    INT, DOUBLE, CHUNK
  }

  private DataType dataType;

  protected AbstractMemType(DataType dataType, int numIntCols, int numDoubleCols, int numChunkCols) {
    this.dataType = dataType;
    this.numIntCols = numIntCols;
    this.numDoubleCols = numDoubleCols;
    this.numChunkCols = numChunkCols;
  }

  protected AbstractMemType(DataType dataType, MemDim dim) {
    this.dataType = dataType;
    this.numIntCols = dim.xInt;
    this.numDoubleCols = dim.xDouble;
    this.numChunkCols = dim.xChunk;
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

  protected void setNumIntCols(int numIntCols) {
    this.numIntCols = numIntCols;
  }

  protected void setNumDoubleCols(int numDoubleCols) {
    this.numDoubleCols = numDoubleCols;
  }

  protected void setNumChunkCols(int numChunkCols) {
    this.numChunkCols = numChunkCols;
  }

  public MemDim getDim() {
    return new MemDim(numIntCols, numDoubleCols, numChunkCols);
  }

  public int getNumIntCols() {
    return numIntCols;
  }

  public int getNumDoubleCols() {
    return numDoubleCols;
  }

  public int getNumChunkCols() {
    return numChunkCols;
  }

  public abstract Value emptyValue();

  public abstract AbstractMemValue valueFromChunk(MemChunk chunk, MemVector pointer);

}
