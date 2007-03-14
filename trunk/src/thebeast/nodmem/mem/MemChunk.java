package thebeast.nodmem.mem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * @author Sebastian Riedel
 */
public final class MemChunk extends MemHolder {

  //these should go into a general info object to be shared over chunks
  public int numDoubleCols;
  public int numIntCols;
  public int numChunkCols;
  public MemColumnSelector allCols;

  //these should go to a chunk index information (or rather into higher layers, anyway).
  public MemChunkMultiIndex[] indices;
  public MemChunkIndex rowIndex;
  public int rowIndexedSoFar = 0;

  private static final double MAXLOADFACTOR = 3.0;

  //private static final int INCREMENTSCALE = 1;

  public void copyFrom(MemChunk other) {
    if (other.size > capacity) increaseCapacity(other.size - capacity);
    if (intData != null && other.intData != null)
      System.arraycopy(other.intData, 0, intData, 0, other.size * other.numIntCols);
    if (doubleData != null && other.doubleData != null)
      System.arraycopy(other.doubleData, 0, doubleData, 0, other.size * other.numDoubleCols);
    if (chunkData != null && other.chunkData != null)
      for (int i = 0; i < other.size * other.numChunkCols; ++i)
        if (chunkData[i] == null)
          chunkData[i] = other.chunkData[i].copy();
        else
          chunkData[i].copyFrom(other.chunkData[i]);
    size = other.size;
  }

  public void serialize(MemSerializer serializer) {

  }

  public static MemChunk deserialize(MemDeserializer deserializer) throws IOException {
    int[] dims = new int[4];
    deserializer.read(dims, 4);
    MemDim dim = new MemDim(dims[0], dims[1], dims[2]);
    MemHolder holder = MemHolder.deserialize(deserializer, dim);
    MemChunk result = new MemChunk(holder.size, holder.intData, holder.doubleData, holder.chunkData);
    result.rowIndexedSoFar = dims[3];
    int[] buffer = new int[1];
    deserializer.read(buffer, 1);
    if (buffer[0] == 1) {
      result.rowIndex = MemChunkIndex.deserialize(deserializer);
    }
    deserializer.read(buffer, 1);
    if (buffer[0] > 0) {
      result.indices = new MemChunkMultiIndex[buffer[0]];
      for (int i = 0; i < buffer[0]; ++i) {
        result.indices[i] = MemChunkMultiIndex.deserialize(deserializer);
      }
    }
    return result;
  }

  public static void deserializeInPlace(MemDeserializer deserializer, MemChunk dst) throws IOException {
    int[] dims = new int[4];
    deserializer.read(dims, 4);
    MemDim dim = new MemDim(dims[0], dims[1], dims[2]);
    MemHolder.deserializeInPlace(deserializer, dim, dst);
    dst.rowIndexedSoFar = dims[3];
    int[] buffer = new int[1];
    deserializer.read(buffer, 1);
    if (buffer[0] == 1) {
      if (dst.rowIndex == null)
        dst.rowIndex = MemChunkIndex.deserialize(deserializer);
      else
        MemChunkIndex.deserializeInPlace(deserializer, dst.rowIndex);
    }
    deserializer.read(buffer, 1);
    if (buffer[0] > 0) {
      dst.indices = new MemChunkMultiIndex[buffer[0]];
      for (int i = 0; i < buffer[0]; ++i) {
        if (dst.indices[i] == null)
          dst.indices[i] = MemChunkMultiIndex.deserialize(deserializer);
        else
          MemChunkMultiIndex.deserializeInPlace(deserializer, dst.indices[i]);
      }
    } else {
      if (dst.indices != null)
        for (MemChunkMultiIndex index : dst.indices)
          index.clear();
    }
  }


  public static void serialize(MemChunk chunk, MemSerializer serializer, boolean dumpIndices) throws IOException {
    MemChunkIndex rowIndex = dumpIndices ? chunk.rowIndex : null;
    int rowIndexedSoFar = dumpIndices ? chunk.rowIndexedSoFar : 0;
    serializer.writeInts(chunk.numIntCols, chunk.numDoubleCols, chunk.numChunkCols, rowIndexedSoFar);
    MemHolder.serialize(chunk, serializer, new MemDim(chunk.numIntCols, chunk.numDoubleCols, chunk.numChunkCols));
    if (rowIndex != null) {
      serializer.writeInts(1);
      MemChunkIndex.serialize(chunk.rowIndex, serializer);
    } else {
      serializer.writeInts(0);
    }
    if (chunk.indices == null || !dumpIndices) {
      serializer.writeInts(0);
    } else {
      serializer.writeInts(chunk.indices.length);
      for (MemChunkMultiIndex index : chunk.indices) {
        MemChunkMultiIndex.serialize(index, serializer);
      }
    }
  }

  public int getOverhead() {
    return capacity - size;
  }

  public enum DataType {
    INT, DOUBLE, CHUNK
  }

  public MemChunk(int size, int capacity, MemDim dim) {
    this(size, capacity, dim.xInt, dim.xDouble, dim.xChunk);
    rowIndex = new MemChunkIndex(capacity == 0 ? 1 : capacity, dim);
    allCols = new MemColumnSelector(numIntCols, numDoubleCols, numChunkCols);
  }

  public MemChunk(MemDim dim) {
    this(1, 1, dim.xInt, dim.xDouble, dim.xChunk);
    rowIndex = new MemChunkIndex(10, dim);
    allCols = new MemColumnSelector(numIntCols, numDoubleCols, numChunkCols);
  }


  public MemChunk(int size, int[] intData, double[] doubleData, MemChunk[] chunkData) {
    this.size = size;
    this.capacity = size;
    this.numIntCols = intData == null ? 0 : intData.length / size;
    this.numDoubleCols = doubleData == null ? 0 : doubleData.length / size;
    this.numChunkCols = chunkData == null ? 0 : chunkData.length / size;
    this.intData = intData;
    this.doubleData = doubleData;
    this.chunkData = chunkData;
    rowIndex = new MemChunkIndex(10, new MemDim(numIntCols, numDoubleCols, numChunkCols));
    allCols = new MemColumnSelector(numIntCols, numDoubleCols, numChunkCols);
  }

  public MemChunk(int numRows, int capacity, int numIntCols, int numDoubleCols, int numChunkCols) {
    this.size = numRows;
    this.numIntCols = numIntCols;
    this.numDoubleCols = numDoubleCols;
    this.numChunkCols = numChunkCols;
    //numCols = numIntCols + numDoubleCols + numChunkCols;
    this.capacity = capacity;
    intData = new int[capacity * numIntCols];
    doubleData = new double[capacity * numDoubleCols];
    chunkData = new MemChunk[capacity * numChunkCols];
    rowIndex = new MemChunkIndex(capacity, new MemDim(numIntCols, numDoubleCols, numChunkCols));
    allCols = new MemColumnSelector(numIntCols, numDoubleCols, numChunkCols);
  }

  public void increaseCapacity(int howMuch) {
    if (howMuch < 0) return;
    //System.out.print("");    
    capacity += howMuch;
    int[] newIntData = new int[capacity * numIntCols];
    System.arraycopy(intData, 0, newIntData, 0, intData.length);
    intData = newIntData;
    double[] newDoubleData = new double[capacity * numDoubleCols];
    System.arraycopy(doubleData, 0, newDoubleData, 0, doubleData.length);
    doubleData = newDoubleData;
    MemChunk[] newChunkData = new MemChunk[capacity * numChunkCols];
    System.arraycopy(chunkData, 0, newChunkData, 0, chunkData.length);
    chunkData = newChunkData;
    rowIndex.increaseCapacity(howMuch);
    allCols = new MemColumnSelector(numIntCols, numDoubleCols, numChunkCols);
  }

  public void compactify() {
    super.compactify(new MemDim(numIntCols, numDoubleCols, numChunkCols));
  }


  public void buildRowIndex() {
    if (rowIndex == null)
      rowIndex = new MemChunkIndex(size, new MemDim(numIntCols, numDoubleCols, numChunkCols));
    MemVector pointer = new MemVector(rowIndexedSoFar, getDim());
    if (rowIndex.getLoadFactor() > MAXLOADFACTOR) {
      rowIndex.increaseCapacity((size - rowIndex.getCapacity()));
    }
    for (int row = rowIndexedSoFar; row < size; ++row) {
      rowIndex.put(this, pointer, allCols, row, false);
      pointer.xInt += numIntCols;
      pointer.xDouble += numDoubleCols;
      pointer.xChunk += numChunkCols;
    }
    rowIndexedSoFar = size;
  }

  public void unify() {
    MemVector srcPointer = new MemVector(rowIndexedSoFar, numIntCols, numDoubleCols, numChunkCols);
    MemVector dstPointer = new MemVector(rowIndexedSoFar, numIntCols, numDoubleCols, numChunkCols);
    int dstRow = rowIndexedSoFar;
    for (int row = rowIndexedSoFar; row < size; ++row) {
      int old = rowIndex.put(this, srcPointer, allCols, row, false);
      if (old == -1) {
        if (dstRow < row) {
          System.arraycopy(intData, srcPointer.xInt, intData, dstPointer.xInt, numIntCols);
          System.arraycopy(doubleData, srcPointer.xDouble, doubleData, dstPointer.xDouble, numDoubleCols);
          System.arraycopy(chunkData, srcPointer.xChunk, chunkData, dstPointer.xChunk, numChunkCols);
        }
        ++dstRow;
        dstPointer.xInt += numIntCols;
        dstPointer.xDouble += numDoubleCols;
        dstPointer.xChunk += numChunkCols;
      }
      srcPointer.xInt += numIntCols;
      srcPointer.xDouble += numDoubleCols;
      srcPointer.xChunk += numChunkCols;
    }
    size = dstRow;
    rowIndexedSoFar = size;

  }

  public int getSize() {
    return size;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int[] getIntData() {
    return intData;
  }

  public int hashCode() {
    int result = 17;
    int intLength = size * numIntCols;
    for (int i = 0; i < intLength; ++i)
      result = result * 37 + intData[i];
    int doubleLength = size * numDoubleCols;
    for (int i = 0; i < doubleLength; ++i)
      result = result * 37 + (int) doubleData[i];
    int chunkLength = size * numChunkCols;
    for (int i = 0; i < chunkLength; ++i)
      result = result * 37 + chunkData[i].hashCode();
    return result;
  }

  public MemDim getDim() {
    return new MemDim(numIntCols, numDoubleCols, numChunkCols);
  }

  public boolean equals(Object object) {
    MemChunk other = (MemChunk) object;
    if (other.numIntCols != numIntCols) return false;
    if (other.numDoubleCols != numDoubleCols) return false;
    if (other.numChunkCols != numChunkCols) return false;
    int intLength = size * numIntCols;
    for (int i = 0; i < intLength; ++i)
      if (other.intData[i] != intData[i]) return false;
    int doubleLength = size * numDoubleCols;
    for (int i = 0; i < doubleLength; ++i)
      if (other.doubleData[i] != doubleData[i]) return false;
    int chunkLength = size * numChunkCols;
    for (int i = 0; i < chunkLength; ++i)
      if (!other.chunkData[i].equals(chunkData[i])) return false;
    return true;
  }

  public void addMemChunkMultiIndex(MemChunkMultiIndex index) {
    if (indices == null)
      indices = new MemChunkMultiIndex[]{index};
    else {
      MemChunkMultiIndex[] newIndices = new MemChunkMultiIndex[indices.length + 1];
      System.arraycopy(indices, 0, newIndices, 0, indices.length);
      indices = newIndices;
      indices[indices.length - 1] = index;
    }
  }

  public void setMemChunkMultiIndex(int nr, MemChunkMultiIndex index) {
    if (indices == null) {
      indices = new MemChunkMultiIndex[nr + 1];
      indices[nr] = index;
    } else {
      if (nr < indices.length) {
        indices[nr] = index;
      } else {
        MemChunkMultiIndex[] newIndices = new MemChunkMultiIndex[nr + 1];
        System.arraycopy(indices, 0, newIndices, 0, indices.length);
        indices = newIndices;
        indices[nr] = index;
      }
    }
  }

  public void shallowCopy(MemChunk chunk) {
    this.intData = chunk.intData;
    this.doubleData = chunk.doubleData;
    this.chunkData = chunk.chunkData;
    this.size = chunk.size;
    this.capacity = chunk.capacity;
  }

  public MemChunk copy() {
    MemChunk result = new MemChunk(size, size, numIntCols, numDoubleCols, numChunkCols);
    if (intData != null)
      System.arraycopy(intData, 0, result.intData, 0, size * numIntCols);
    if (doubleData != null)
      System.arraycopy(doubleData, 0, result.doubleData, 0, size * numDoubleCols);
    if (chunkData != null)
      MemChunk.copyChunks(chunkData, 0, result.chunkData, 0, size * numChunkCols);
    return result;
  }

  public void own() {
    //System.out.print("!");
    int[] newIntData = new int[capacity * numIntCols];
    System.arraycopy(intData, 0, newIntData, 0, intData.length);
    intData = newIntData;
    double[] newDoubleData = new double[capacity * numDoubleCols];
    System.arraycopy(doubleData, 0, newDoubleData, 0, doubleData.length);
    doubleData = newDoubleData;
    MemChunk[] newChunkData = new MemChunk[capacity * numChunkCols];
    MemChunk.copyChunks(chunkData, 0, newChunkData, 0, chunkData.length);
    chunkData = newChunkData;

    rowIndexedSoFar = 0;
    //buildRowIndex();

  }

  public MemChunk minus(MemChunk other) {
    MemChunk result = new MemChunk(0, size, numIntCols, numDoubleCols, numChunkCols);
    other.buildRowIndex();
    MemVector ptr = new MemVector();
    MemChunkIndex index = other.rowIndex;
    MemColumnSelector cols = allCols;
    MemVector dst = new MemVector();
    for (int row = 0; row < size; ++row) {
      int found = index.get(this, ptr, cols);
      if (found == -1) {
        System.arraycopy(intData, ptr.xInt, result.intData, dst.xInt, numIntCols);
        System.arraycopy(doubleData, ptr.xDouble, result.doubleData, dst.xDouble, numDoubleCols);
        MemChunk.copyChunks(chunkData, ptr.xChunk, result.chunkData, dst.xChunk, numChunkCols);
        dst.add(numIntCols, numDoubleCols, numChunkCols);
        ++result.size;
      }
      ptr.add(numIntCols, numDoubleCols, numChunkCols);
    }
    return result;
  }

  public int findFirst(MemChunk argData, MemVector argPtr, MemColumnSelector argCols, MemColumnSelector cols) {
    MemVector ptr = new MemVector();
    main:
    for (int row = 0; row < size; ++row) {
      for (int i = 0; i < argCols.intCols.length; ++i)
        if (intData[ptr.xInt + cols.intCols[i]] != argData.intData[argPtr.xInt + argCols.intCols[i]]) {
          ptr.xInt += numIntCols;
          ptr.xDouble += numDoubleCols;
          ptr.xChunk += numChunkCols;
          continue main;
        }
      for (int i = 0; i < argCols.doubleCols.length; ++i)
        if (doubleData[ptr.xDouble + cols.doubleCols[i]] != argData.doubleData[argPtr.xDouble + argCols.doubleCols[i]]) {
          ptr.xInt += numIntCols;
          ptr.xDouble += numDoubleCols;
          ptr.xChunk += numChunkCols;
          continue main;
        }
      for (int i = 0; i < argCols.chunkCols.length; ++i)
        if (chunkData[ptr.xChunk + cols.chunkCols[i]] != argData.chunkData[argPtr.xChunk + argCols.chunkCols[i]]) {
          ptr.xInt += numIntCols;
          ptr.xDouble += numDoubleCols;
          ptr.xChunk += numChunkCols;
          continue main;
        }
      return row;

    }

    return -1;
  }

  public int byteSize() {
    int size = 5 * INTSIZE + 2 * POINTERSIZE + ARRAYSIZE;
    size += super.byteSize();
    if (rowIndex != null) {
      size += rowIndex.byteSize();
    }
    if (indices != null) for (MemChunkMultiIndex index : indices)
      size += index.byteSize();
    return size;
  }

  public void sleep(WritableByteChannel channel) throws IOException {
    //save data
    int byteSize = 32;
    int intSize = byteSize / INTSIZE;
    int doubleSize = byteSize / DOUBLESIZE;
    ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
    IntBuffer intBuffer = byteBuffer.asIntBuffer();
    int intLength = size * numIntCols;
    for (int i = 0; i < intLength; i += intSize) {
      intBuffer.put(intData, i, i + intSize > intLength ? intLength - i : intSize);
      channel.write(byteBuffer);
    }
    DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
    int doubleLength = size * numDoubleCols;
    for (int i = 0; i < doubleLength; i += doubleSize) {
      doubleBuffer.put(doubleData, i, i + doubleSize > doubleLength ? doubleLength - i : doubleSize);
      channel.write(byteBuffer);
    }
    int chunkLength = size * numChunkCols;
    for (int i = 0; i < chunkLength; ++i) {
      if (chunkData[i] != null) chunkData[i].serialize(channel, byteBuffer);
    }

    //save indices

    //release data
    intData = null;
    doubleData = null;
    chunkData = null;
  }

  /**
   * Writes this chunk + indices to the given channel and using the given byte buffer for converting ints and doubles to
   * bytes (this allows fast transfer of byte arrays).
   *
   * @param channel    the channel to write to.
   * @param byteBuffer the bytebuffer to use as buffer for converting rows into bytes.
   * @throws IOException if I/O goes wrong.
   */
  public void serialize(WritableByteChannel channel, ByteBuffer byteBuffer) throws IOException {
    int byteSize = byteBuffer.capacity();
    int intSize = byteSize / INTSIZE;
    int doubleSize = byteSize / DOUBLESIZE;
    //ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
    IntBuffer intBuffer = byteBuffer.asIntBuffer();

    //write the size and column sizes
    intBuffer.put(size);
    intBuffer.put(numIntCols);
    intBuffer.put(numDoubleCols);
    intBuffer.put(numChunkCols);
    channel.write(byteBuffer);

    int intLength = size * numIntCols;
    for (int i = 0; i < intLength; i += intSize) {
      intBuffer.put(intData, i, i + intSize > intLength ? intLength - i : intSize);
      channel.write(byteBuffer);
    }
    DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
    int doubleLength = size * numDoubleCols;
    for (int i = 0; i < doubleLength; i += doubleSize) {
      doubleBuffer.put(doubleData, i, i + doubleSize > doubleLength ? doubleLength - i : doubleSize);
      channel.write(byteBuffer);
    }
    int chunkLength = size * numChunkCols;
    for (int i = 0; i < chunkLength; ++i) {
      if (chunkData[i] != null) chunkData[i].serialize(channel, byteBuffer);
    }

    //save indices

  }


  public String toString() {
    return "size: " + size;
  }

  public static void copyChunks(MemChunk[] src, int fromSrc, MemChunk[] dst, int fromDst, int howmany) {
    int srcI = fromSrc;
    int dstI = fromDst;
    int max = fromSrc + howmany;
    for (; srcI < max; ++srcI, ++dstI) {
      if (src[srcI] == null) {
        if (dst[dstI] !=  null)
          dst[dstI].size = 0;
      } else {
        if (dst[dstI] == null) {
          dst[dstI] = src[srcI].copy();
        } else
          dst[dstI].copyFrom(src[srcI]);
      }
    }

  }

}
