package thebeast.nodmem.mem;

import java.io.IOException;
import java.util.Arrays;

/**
 * This index maps from row data to row numbers.
 *
 * @author Sebastian Riedel
 */
public final class MemShallowIndex {

  private MemChunk chunk;
  private int[][] values;
  private int[][] keys;
  private int[] valueCounts;
  private int capacity;
  private MemDim dim;
  private int numKeys;
  private int numUsedIndices;
  private static int CAPACITY_INCREMENTS = 2;
  public MemVector current = new MemVector();


  /**
   * Creates a new memchunk index for tuples of the given dimension.
   *
   * @param capacity the initial capacity of the index.
   * @param dim      the dimensions of the key-tuples.
   * @param data     the chunk/table this index points to
   */
  public MemShallowIndex(int capacity, MemDim dim, MemChunk data) {
    keys = new int[capacity][];
    values = new int[capacity][];
    valueCounts = new int[capacity];
    this.capacity = capacity;
    this.dim = dim;
    this.chunk = data;
  }

  public int put(int[] ints, double[] doubles, MemChunk[] chunks, int value, boolean overide) {
    return put(new MemHolder(1, 1, ints, doubles, chunks),
            MemVector.ZERO, new MemColumnSelector(ints.length, doubles.length, chunks.length), value, overide);
  }

  /**
   * Adds a mapping from the data in the specified holder at the given pointer and given columns to the specified
   * value.
   *
   * @param data     a MemHolder that stores the tuple to map from
   * @param pointer  a pointer to the beginning of the tuple in data.
   * @param cols     the columns to use of the specified tuple.
   * @param value    the value to put.
   * @param override should we override existing values
   * @return the old value for the given tuple or -1 if there was no old value.
   */
  public int put(MemHolder data, MemVector pointer, MemColumnSelector cols, int value, boolean override) {
    //calculate key
    int key = 17;
    for (int col : cols.intCols)
      key = 37 * key + data.intData[pointer.xInt + col];
    for (int col : cols.doubleCols)
      key = 37 * key + (int) data.doubleData[pointer.xDouble + col];
    for (int col : cols.chunkCols)
      key = 37 * key + data.chunkData[pointer.xChunk + col].hashCode();
    if (key < 0) key = -key;
    int index = key % capacity;
    int[] keysAtIndex = keys[index];
    int[] valuesAtIndex = values[index];
    if (keysAtIndex == null) {
      keysAtIndex = new int[CAPACITY_INCREMENTS];
      valuesAtIndex = new int[CAPACITY_INCREMENTS];
      keys[index] = keysAtIndex;
      values[index] = valuesAtIndex;
    }
    int length = valueCounts[index];
    if (length == 0) ++numUsedIndices;
    current.set(0,0,0);
    for (int item = 0; item < length; ++item) {
      //test key equality
      int old = valuesAtIndex[item];
      check:
      if (key == keysAtIndex[item]) {
        //test tuple equality
        int oldInt = old * dim.xInt;
        for (int i = 0; i < cols.intCols.length; ++i)
          if (chunk.intData[oldInt + cols.intCols[i]] != data.intData[pointer.xInt + cols.intCols[i]])
            break check;
        int oldDouble = old * dim.xDouble;
        for (int i = 0; i < cols.doubleCols.length; ++i)
          if (chunk.doubleData[oldDouble + cols.doubleCols[i]] != data.doubleData[pointer.xDouble + cols.doubleCols[i]])
            break check;
        int oldChunk = old * dim.xChunk;
        for (int i = 0; i < cols.chunkCols.length; ++i)
          if (!data.chunkData[oldChunk + cols.chunkCols[i]].equals(data.chunkData[pointer.xChunk + cols.chunkCols[i]]))
            break check;
        //they are equal, let's just set the new value
        if (override) valuesAtIndex[item] = value;
        return old;
      }
      current.xInt += dim.xInt;
      current.xDouble += dim.xDouble;
      current.xChunk += dim.xChunk;
    }
    //if we have arrived here the key-value pair has not yet been put
    //check if we need to increase the capacity
    if (length == keysAtIndex.length) {
      int[] newValuesAtIndex = new int[length + CAPACITY_INCREMENTS];
      System.arraycopy(valuesAtIndex, 0, newValuesAtIndex, 0, length);
      valuesAtIndex = newValuesAtIndex;
      values[index] = newValuesAtIndex;
      int[] newKeysAtIndex = new int[length + CAPACITY_INCREMENTS];
      System.arraycopy(keysAtIndex, 0, newKeysAtIndex, 0, length);
      keysAtIndex = newKeysAtIndex;
      keys[index] = newKeysAtIndex;
    }
    //insert
    valuesAtIndex[length] = value;
    keysAtIndex[length] = key;
    valueCounts[index]++;
    ++numKeys;
    return -1;
  }

  public int get(int[] ints, double[] doubles, MemChunk[] chunks) {
    return get(new MemHolder(1, 1, ints, doubles, chunks), MemVector.ZERO,
            new MemColumnSelector(ints.length, doubles.length, chunks.length));
  }

  public int get(MemHolder data, MemVector pointer, MemColumnSelector cols) {
    if (capacity == 0) return -1;
    int key = 17;
    for (int col : cols.intCols)
      key = 37 * key + data.intData[pointer.xInt + col];
    for (int col : cols.doubleCols)
      key = 37 * key + (int) data.doubleData[pointer.xDouble + col];
    for (int col : cols.chunkCols)
      key = 37 * key + data.chunkData[pointer.xChunk + col].hashCode();
    if (key < 0) key = -key;
    int index = key % capacity;
    int[] keysAtIndex = keys[index];
    if (keysAtIndex == null) return -1;
    int[] valuesAtIndex = values[index];
    int length = valueCounts[index];
    MemVector p = new MemVector();
    for (int item = 0; item < length; ++item) {
      //test key equality
      int old = valuesAtIndex[item];
      check:
      if (key == keysAtIndex[item]) {
        //test tuple equality
        int oldInt = old * dim.xInt;
        for (int i = 0; i < cols.intCols.length; ++i)
          if (chunk.intData[oldInt + cols.intCols[i]] != data.intData[pointer.xInt + cols.intCols[i]])
            break check;
        int oldDouble = old * dim.xDouble;
        for (int i = 0; i < cols.doubleCols.length; ++i)
          if (chunk.doubleData[oldDouble + cols.doubleCols[i]] != data.doubleData[pointer.xDouble + cols.doubleCols[i]])
            break check;
        int oldChunk = old * dim.xChunk;
        for (int i = 0; i < cols.chunkCols.length; ++i)
          if (!data.chunkData[oldChunk + cols.chunkCols[i]].equals(data.chunkData[pointer.xChunk + cols.chunkCols[i]]))
            break check;
        //they are equal, let's just set the new value
        return old;
      }
      p.xInt += cols.intCols.length;
      p.xDouble += cols.doubleCols.length;
      p.xChunk += cols.chunkCols.length;
    }
    return -1;
  }

  public void clear() {
    numKeys = 0;
    numUsedIndices = 0;
    Arrays.fill(valueCounts,0);
  }

  public void clearMemory() {
    keys = new int[capacity][];
    values = new int[capacity][];
    Arrays.fill(valueCounts,0);
    numKeys = 0;
    numUsedIndices = 0;
  }


  public int getCapacity() {
    return capacity;
  }

  public void increaseCapacity(int howMuch) {
    MemShallowIndex helper = new MemShallowIndex(capacity + howMuch, dim,chunk);
    MemColumnSelector cols = new MemColumnSelector(dim.xInt, dim.xDouble, dim.xChunk);
    for (int index = 0; index < values.length; ++index) {
      if (valueCounts[index] > 0) {
        //int[] keysAtIndex = keys[index];
        int[] valuesAtIndex = values[index];
        //MemVector current = new MemVector();
        int length = valueCounts[index];
        for (int item = 0; item < length; ++item) {
          current.set(valuesAtIndex[item],dim);
          helper.put(chunk, current, cols, valuesAtIndex[item], true);
          current.xInt += dim.xInt;
          current.xDouble += dim.xDouble;
          current.xChunk += dim.xChunk;
        }
      }
    }
    this.capacity = helper.capacity;
    this.valueCounts = helper.valueCounts;
    this.values = helper.values;
    this.keys = helper.keys;
    this.numUsedIndices = helper.numUsedIndices;
  }


  public int getNumKeys() {
    return numKeys;
  }

  public int getNumUsedIndices() {
    return numUsedIndices;
  }

  public double getLoadFactor() {
    return (double) numKeys / (double) numUsedIndices;
  }

  public int byteSize() {
    int size = 3 * MemChunk.ARRAYSIZE + 3 & MemChunk.POINTERSIZE + dim.byteSize() + 3 * MemChunk.INTSIZE;
    size += valueCounts.length * MemChunk.INTSIZE;
    size += keys.length * MemChunk.POINTERSIZE;
    size += values.length * MemChunk.POINTERSIZE;
    for (int i = 0; i < capacity; ++i)
      if (valueCounts[i] > 0) {
        size += 2 * MemChunk.ARRAYSIZE;
        size += keys[i].length * MemChunk.INTSIZE;
        size += values[i].length * MemChunk.INTSIZE;
      }
    return size;
  }

  public static void serialize(MemShallowIndex index, MemSerializer serializer) throws IOException {
    serializer.writeInts(index.capacity, index.dim.xInt, index.dim.xDouble,
            index.dim.xChunk, index.numKeys, index.numUsedIndices);
    for (int i = 0; i < index.capacity; ++i) {
      if (index.valueCounts[i] > 0) {
        serializer.writeInts(0);
      } else {
        int size = index.valueCounts[i];
        serializer.writeInts(size);
        if (size > 0) {
          serializer.writeInts(index.keys[i], size);
          serializer.writeInts(index.values[i], size);
        }
      }
    }
  }

  public static MemShallowIndex deserialize(MemDeserializer deserializer, MemChunk chunk) throws IOException {
    int[] stats = new int[6];
    deserializer.read(stats, 6);
    MemShallowIndex index = new MemShallowIndex(stats[0], MemDim.create(stats[1], stats[2], stats[3]),chunk);
    index.numKeys = stats[4];
    index.numUsedIndices = stats[5];
    int[] size = new int[1];
    for (int i = 0; i < index.capacity; ++i) {
      deserializer.read(size, 1);
      if (size[0] > 0) {
        index.valueCounts[i] = size[0];
        index.keys[i] = new int[size[0]];
        deserializer.read(index.keys[i], size[0]);
        index.values[i] = new int[size[0]];
        deserializer.read(index.values[i], size[0]);
      }
    }
    return index;
  }


  public static void deserializeInPlace(MemDeserializer deserializer, MemShallowIndex index) throws IOException {
    int[] stats = new int[6];
    index.clear();
    deserializer.read(stats, 6);
//    MemChunkIndex index = new MemChunkIndex(stats[0], new MemDim(stats[1], stats[2], stats[3]));
    int savedSize = stats[0];
    if (index.capacity < savedSize)
      index.increaseCapacity(savedSize - index.capacity);
    index.dim.xInt = stats[1];
    index.dim.xDouble = stats[2];
    index.dim.xChunk = stats[3];
    index.numKeys = stats[4];
    index.numUsedIndices = stats[5];
    int[] size = new int[1];
    for (int i = 0; i < savedSize; ++i) {
      deserializer.read(size, 1);
      if (size[0] > 0) {
        index.valueCounts[i] = size[0];
        if (index.keys[i] == null || index.keys[i].length < size[0])
          index.keys[i] = new int[size[0]];
        deserializer.read(index.keys[i], size[0]);
        if (index.values[i] == null || index.values[i].length < size[0])
          index.values[i] = new int[size[0]];
        deserializer.read(index.values[i], size[0]);
      }
    }
  }

}
