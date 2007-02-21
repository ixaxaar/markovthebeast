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


  MemHolder(int size, int capacity, MemDim dim) {
    this.size = size;
    this.capacity = capacity;
    intData = new int[capacity * dim.xInt];
    doubleData = new double[capacity * dim.xDouble];
    chunkData = new MemChunk[capacity * dim.xChunk];
  }


  public MemHolder() {
  }

  void increaseCapacity(int howmuch, MemDim dim) {
    int[] oldInts = intData;
    intData = new int[(capacity + howmuch) * dim.xInt];
    System.arraycopy(oldInts, 0, intData, 0, capacity * dim.xInt);
    double[] oldDoubles = doubleData;
    doubleData = new double[(capacity + howmuch) * dim.xDouble];
    System.arraycopy(oldDoubles, 0, doubleData, 0, capacity * dim.xDouble);
    MemChunk[] oldChunks = chunkData;
    chunkData = new MemChunk[(capacity + howmuch) * dim.xChunk];
    System.arraycopy(oldChunks, 0, chunkData, 0, capacity * dim.xChunk);
    capacity += howmuch;
  }

  public int byteSize(){
    int size = 0;
    size += intData.length * INTSIZE;
    size += doubleData.length * DOUBLESIZE;
    size += chunkData.length * POINTERSIZE;
    return size;
  }
}


