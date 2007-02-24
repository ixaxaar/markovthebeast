package thebeast.nodmem.mem;

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
      for (int i = 0; i < chunkData.length && chunkData[i]!=null; ++i)
        size += chunkData[i].byteSize();
    }
    return size;
  }
}


