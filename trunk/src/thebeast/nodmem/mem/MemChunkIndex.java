package thebeast.nodmem.mem;

import java.io.IOException;

/**
 * This index maps from row data to row numbers.
 *
 * @author Sebastian Riedel
 */
public final class MemChunkIndex {

  private MemHolder[] tuples;
  private int[][] values;
  private int[][] keys;
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
   */
  public MemChunkIndex(int capacity, MemDim dim) {
    tuples = new MemHolder[capacity];
    keys = new int[capacity][];
    values = new int[capacity][];
    this.capacity = capacity;
    this.dim = dim;
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
    MemHolder tuplesAtIndex = tuples[index];
    if (keysAtIndex == null) {
      keysAtIndex = new int[CAPACITY_INCREMENTS];
      valuesAtIndex = new int[CAPACITY_INCREMENTS];
      tuplesAtIndex = new MemHolder(0, CAPACITY_INCREMENTS, dim);
      keys[index] = keysAtIndex;
      values[index] = valuesAtIndex;
      tuples[index] = tuplesAtIndex;
    }
    int length = tuplesAtIndex.size;
    if (length == 0) ++numUsedIndices;
    current.set(0,0,0);
    for (int item = 0; item < length; ++item) {
      //test key equality
      check:
      if (key == keysAtIndex[item]) {
        //test tuple equality
        for (int i = 0; i < cols.intCols.length; ++i)
          if (tuplesAtIndex.intData[current.xInt + i] != data.intData[pointer.xInt + cols.intCols[i]])
            break check;
        for (int i = 0; i < cols.doubleCols.length; ++i)
          if (tuplesAtIndex.doubleData[current.xDouble + i] != data.doubleData[pointer.xDouble + cols.doubleCols[i]])
            break check;
        for (int i = 0; i < cols.chunkCols.length; ++i)
          if (!tuplesAtIndex.chunkData[current.xChunk + i].equals(data.chunkData[pointer.xChunk + cols.chunkCols[i]]))
            break check;
        //they are equal, let's just set the new value
        int old = valuesAtIndex[item];
        if (override) valuesAtIndex[item] = value;
        return old;
      }
      current.xInt += dim.xInt;
      current.xDouble += dim.xDouble;
      current.xChunk += dim.xChunk;
    }
    //if we have arrived here the key-value pair has not yet been put
    //check if we need to increase the capacity
    if (length == tuplesAtIndex.capacity) {
      tuplesAtIndex.increaseCapacity(CAPACITY_INCREMENTS, dim);
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
    for (int i = 0; i < cols.intCols.length; ++i)
      tuplesAtIndex.intData[current.xInt + i] = data.intData[pointer.xInt + cols.intCols[i]];
    for (int i = 0; i < cols.doubleCols.length; ++i)
      tuplesAtIndex.doubleData[current.xDouble + i] = data.doubleData[pointer.xDouble + cols.doubleCols[i]];
    for (int i = 0; i < cols.chunkCols.length; ++i)
      tuplesAtIndex.chunkData[current.xChunk + i] = data.chunkData[pointer.xChunk + cols.chunkCols[i]];
    ++tuplesAtIndex.size;
    valuesAtIndex[length] = value;
    keysAtIndex[length] = key;
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
    MemHolder tuplesAtIndex = tuples[index];
    int length = tuplesAtIndex.size;
    MemVector p = new MemVector();
    for (int item = 0; item < length; ++item) {
      //test key equality
      check:
      if (key == keysAtIndex[item]) {
        //test tuple equality
        for (int i = 0; i < cols.intCols.length; ++i)
          if (tuplesAtIndex.intData[p.xInt + i] != data.intData[pointer.xInt + cols.intCols[i]])
            break check;
        for (int i = 0; i < cols.doubleCols.length; ++i)
          if (tuplesAtIndex.doubleData[p.xDouble + i] != data.doubleData[pointer.xDouble + cols.doubleCols[i]])
            break check;
        for (int i = 0; i < cols.chunkCols.length; ++i)
          if (!tuplesAtIndex.chunkData[p.xChunk + i].equals(data.chunkData[pointer.xChunk + cols.chunkCols[i]]))
            break check;
        //they are equal, let's just set the new value
        return valuesAtIndex[item];
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
    for (MemHolder tuple : tuples) {
      if (tuple != null) tuple.size = 0;
    }
  }

  public void clearMemory() {
    tuples = new MemHolder[capacity];
    keys = new int[capacity][];
    values = new int[capacity][];
    numKeys = 0;
    numUsedIndices = 0;
  }


  public int getCapacity() {
    return capacity;
  }

  public void increaseCapacity(int howMuch) {
    MemChunkIndex helper = new MemChunkIndex(capacity + howMuch, dim);
    MemColumnSelector cols = new MemColumnSelector(dim.xInt, dim.xDouble, dim.xChunk);
    for (int index = 0; index < tuples.length; ++index) {
      MemHolder tuplesAtIndex = tuples[index];
      if (tuplesAtIndex != null) {
        //int[] keysAtIndex = keys[index];
        int[] valuesAtIndex = values[index];
        //MemVector current = new MemVector();
        current.set(0,0,0);
        int length = tuplesAtIndex.size;
        for (int item = 0; item < length; ++item) {
          //todo: use existing keys
          helper.put(tuplesAtIndex, current, cols, valuesAtIndex[item], true);
          current.xInt += dim.xInt;
          current.xDouble += dim.xDouble;
          current.xChunk += dim.xChunk;
        }
      }
    }
    this.capacity = helper.capacity;
    this.tuples = helper.tuples;
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
    size += tuples.length * MemChunk.POINTERSIZE;
    size += keys.length * MemChunk.POINTERSIZE;
    size += values.length * MemChunk.POINTERSIZE;
    for (int i = 0; i < capacity; ++i)
      if (tuples[i] != null) {
        size += tuples[i].byteSize();
        size += 2 * MemChunk.ARRAYSIZE;
        size += keys[i].length * MemChunk.INTSIZE;
        size += values[i].length * MemChunk.INTSIZE;
      }
    return size;
  }

  public static void serialize(MemChunkIndex index, MemSerializer serializer) throws IOException {
    serializer.writeInts(index.capacity, index.dim.xInt, index.dim.xDouble,
            index.dim.xChunk, index.numKeys, index.numUsedIndices);
    for (int i = 0; i < index.capacity; ++i) {
      if (index.tuples[i] == null) {
        serializer.writeInts(0);
      } else {
        int size = index.tuples[i].size;
        serializer.writeInts(size);
        if (size > 0) {
          MemHolder.serialize(index.tuples[i], serializer, index.dim);
          serializer.writeInts(index.keys[i], size);
          serializer.writeInts(index.values[i], size);
        }
      }
    }
  }

  public static MemChunkIndex deserialize(MemDeserializer deserializer) throws IOException {
    int[] stats = new int[6];
    deserializer.read(stats, 6);
    MemChunkIndex index = new MemChunkIndex(stats[0], MemDim.create(stats[1], stats[2], stats[3]));
    index.numKeys = stats[4];
    index.numUsedIndices = stats[5];
    int[] size = new int[1];
    for (int i = 0; i < index.capacity; ++i) {
      deserializer.read(size, 1);
      if (size[0] > 0) {
        index.tuples[i] = MemHolder.deserialize(deserializer, index.dim);
        index.keys[i] = new int[size[0]];
        deserializer.read(index.keys[i], size[0]);
        index.values[i] = new int[size[0]];
        deserializer.read(index.values[i], size[0]);
      }
    }
    return index;
  }


  public static void deserializeInPlace(MemDeserializer deserializer, MemChunkIndex index) throws IOException {
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
        if (index.tuples[i] == null)
          index.tuples[i] = MemHolder.deserialize(deserializer, index.dim);
        else
          MemHolder.deserializeInPlace(deserializer, index.dim, index.tuples[i]);
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
