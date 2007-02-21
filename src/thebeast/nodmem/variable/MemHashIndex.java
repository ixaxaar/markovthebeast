package thebeast.nodmem.variable;

import thebeast.nod.variable.Index;
import thebeast.nod.variable.RelationVariable;
import thebeast.nodmem.mem.*;
import thebeast.nodmem.type.MemHeading;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 19-Jan-2007 Time: 16:02:44
 */
public class MemHashIndex implements Index {

  private MemRelationVariable variable;
  private ArrayList<String> attributes;
  private Index.Type indexType;
  private MemChunkMultiIndex memIndex;
  private int indexedSoFar;
  private MemColumnSelector cols;
  private MemDim dim;
  private MemChunk chunk;
  private double maxLoadFactor = 3;

  public MemHashIndex(MemRelationVariable variable, Type indexType, List<String> attributes) {
    this.variable = variable;
    this.indexType = indexType;
    this.attributes = new ArrayList<String>(attributes);
    Collections.sort(attributes);
    //Collections.reverse(attributes);
    this.memIndex = null;
    MemHeading heading = (MemHeading) variable.type().heading();
    MemPointer[] pointers = new MemPointer[attributes.size()];
    int index = 0;
    for (String name : attributes) pointers[index++] = heading.pointerForAttribute(name);
    cols = new MemColumnSelector(pointers);
    dim = cols.getDim();
    chunk = variable.getContainerChunk().chunkData[variable.getPointer().xChunk];
    memIndex = new MemChunkMultiIndex(chunk.size, dim);
    indexedSoFar = 0;
  }


  public MemColumnSelector getCols() {
    return cols;
  }

  public RelationVariable variable() {
    return variable;
  }

  public void invalidate(){
    indexedSoFar = 0;
    memIndex.clearMemory();
  }

  public List<String> attributes() {
    return attributes;
  }

  public Type indexType() {
    return indexType;
  }

  public MemChunkMultiIndex memIndex() {
    return memIndex;
  }

  public void update() {
    if (indexedSoFar == chunk.size) return;

    int numUsedIndices = memIndex.getNumUsedIndices();
    int numUsedKeys = memIndex.getNumKeys();
    int numNewRows = chunk.size - indexedSoFar;
    double expectedLoadFactor = numUsedIndices == 0 ? Double.POSITIVE_INFINITY  :
            numUsedKeys + numNewRows / (double) numUsedIndices;
    if (expectedLoadFactor > maxLoadFactor){
      int increase = (int) Math.ceil((numUsedKeys + numNewRows)/maxLoadFactor - numUsedIndices);
      memIndex.increaseCapacity(increase);
    }

    MemDim chunkDim = chunk.getDim();
    MemVector pointer = new MemVector(indexedSoFar,chunk.getDim());
    for (int row = indexedSoFar; row < chunk.size;++row){
      memIndex.add(chunk,pointer, cols, row);
      pointer.xInt += chunkDim.xInt;
      pointer.xDouble += chunkDim.xDouble;
      pointer.xChunk += chunkDim.xChunk;
    }
    indexedSoFar = chunk.size;

  }


  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemHashIndex that = (MemHashIndex) o;

    if (!attributes.equals(that.attributes)) return false;
    if (indexType != that.indexType) return false;
    if (!variable.equals(that.variable)) return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = variable.hashCode();
    result = 31 * result + attributes.hashCode();
    result = 31 * result + indexType.hashCode();
    return result;
  }
}