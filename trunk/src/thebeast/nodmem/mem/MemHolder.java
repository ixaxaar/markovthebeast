package thebeast.nodmem.mem;

import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public class MemHolder {
  public int[] intData;
  public double[] doubleData;
  public MemChunk[] chunkData;
  public int size, capacity;

  public static final int POINTERSIZE = 4;
  public static final int DOUBLESIZE = 8;
  public static final int INTSIZE = 4;
  public static final int ARRAYSIZE = 16;


  MemHolder(int size, int capacity, MemDim dim) {
    this.size = size;
    this.capacity = capacity;
    if (dim.xInt > 0) intData = new int[capacity * dim.xInt];
    if (dim.xDouble > 0) doubleData = new double[capacity * dim.xDouble];
    if (dim.xChunk > 0) chunkData = new MemChunk[capacity * dim.xChunk];
  }


  public MemHolder() {
  }


  public MemHolder(int size, int capacity, int[] intData, double[] doubleData, MemChunk[] chunkData) {
    this.size = size;
    this.intData = intData;
    this.doubleData = doubleData;
    this.chunkData = chunkData;
    this.capacity = capacity;
  }

  void increaseCapacity(int howmuch, MemDim dim) {
    if (intData != null) {
      int[] oldInts = intData;
      intData = new int[(capacity + howmuch) * dim.xInt];
      System.arraycopy(oldInts, 0, intData, 0, capacity * dim.xInt);
    }
    if (doubleData != null) {
      double[] oldDoubles = doubleData;
      doubleData = new double[(capacity + howmuch) * dim.xDouble];
      System.arraycopy(oldDoubles, 0, doubleData, 0, capacity * dim.xDouble);
    }
    if (chunkData != null) {
      MemChunk[] oldChunks = chunkData;
      chunkData = new MemChunk[(capacity + howmuch) * dim.xChunk];
      System.arraycopy(oldChunks, 0, chunkData, 0, capacity * dim.xChunk);
    }
    capacity += howmuch;
  }

  public int byteSize() {
    int size = 2 * INTSIZE + 3 * POINTERSIZE;
    if (intData != null)
      size += intData.length * INTSIZE + ARRAYSIZE;
    if (doubleData != null)
      size += doubleData.length * DOUBLESIZE + ARRAYSIZE;
    if (chunkData != null) {
      size += chunkData.length * POINTERSIZE + ARRAYSIZE;
      for (int i = 0; i < chunkData.length && chunkData[i] != null; ++i)
        size += chunkData[i].byteSize();
    }
    return size;
  }

  public static void serialize(MemHolder holder, MemSerializer serializer, MemDim dim) throws IOException {
    serializer.writeInts(holder.size);
    if (holder.intData != null) {
      serializer.writeInts(holder.intData, holder.size * dim.xInt);
    }
    if (holder.doubleData != null) {
      serializer.writeDoubles(holder.doubleData, holder.size * dim.xDouble);
    }
    if (holder.chunkData != null) {
      for (int i = 0; i < holder.size * dim.xChunk; ++i)
        MemChunk.serialize(holder.chunkData[i], serializer);
    }
  }

  public static MemHolder deserialize(MemDeserializer deserializer, MemDim dim) throws IOException {
    int[] dummy = new int[1];
    deserializer.read(dummy, 1);
    int size = dummy[0];
    MemHolder result = new MemHolder(size, size, dim);
    if (dim.xInt > 0)
      deserializer.read(result.intData, size * dim.xInt);
    if (dim.xDouble > 0)
      deserializer.read(result.doubleData, size * dim.xDouble);
    if (dim.xChunk > 0) {
      for (int i = 0; i < result.chunkData.length; ++i)
        result.chunkData[i] = MemChunk.deserialize(deserializer);
    }
    return result;
  }


}


