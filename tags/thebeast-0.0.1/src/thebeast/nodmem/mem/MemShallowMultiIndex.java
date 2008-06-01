package thebeast.nodmem.mem;

import java.io.IOException;
import java.util.Arrays;

/**
 * This index maps from row data to row numbers.
 *
 * @author Sebastian Riedel
 */
public final class MemShallowMultiIndex {

  private MemColumnSelector cols;

  private MemChunk chunk;
  private int[] keyCounts;

  //at each index the mcmi stores a list of tuples (one for each key with that index)
  //private MemHolder[] tuples;
  //at each index the mcmi stores a list of value lists (one value list for each key)
  private int[][][] lists;
  //the sizes of the value lists
  private int[][] listSizes;
  //the actual keys at each index
  private int[][] keys;
  private int capacity;
  private MemDim keyDim,chunkDim;
  private int numKeys;
  private int numUsedIndices;
  public int indexedSoFar;

  private static final int CAP_INCREASE_LIST = 1;
  private static final int CAP_INCREASE_KEYS = 1;
  public MemVector current = new MemVector();


  public MemShallowMultiIndex(MemChunk chunk,MemColumnSelector cols,int capacity, MemDim dim) {
    keys = new int[capacity][];
    lists = new int[capacity][][];
    listSizes = new int[capacity][];
    keyCounts = new int[capacity];    
    this.capacity = capacity;
    this.keyDim = dim;
    this.chunk = chunk;
    this.cols = cols;
    this.chunkDim = chunk.dim;
  }

  public int add(int[] ints, double[] doubles, MemChunk[] chunks, int value) {
    return add(new MemHolder(1, 1, ints, doubles, chunks),
            MemVector.ZERO, new MemColumnSelector(ints.length, doubles.length, chunks.length), value);
  }


  public int add(MemHolder data, MemVector pointer, MemColumnSelector cols, int row) {
    //calculate key
    int key = 17;
    for (int col : cols.intCols)
      key = 37 * key + data.intData[pointer.xInt + col];
    for (int col : cols.doubleCols)
      key = 37 * key + (int) data.doubleData[pointer.xDouble + col];
    //todo: chunks
    if (key < 0) key = -key;
    int index = key % capacity;
    int[] keysAtIndex = keys[index];
    int[][] listsAtIndex = lists[index];
    int[] listSizesAtIndex = listSizes[index];
    if (keysAtIndex == null) {
      keysAtIndex = new int[CAP_INCREASE_LIST];
      listsAtIndex = new int[CAP_INCREASE_LIST][CAP_INCREASE_LIST];
      listSizesAtIndex = new int[CAP_INCREASE_LIST];
      lists[index] = listsAtIndex;
      keys[index] = keysAtIndex;
      listSizes[index] = listSizesAtIndex;
    }
    int length = keyCounts[index];
    if (length == 0) ++numUsedIndices;
    MemVector p = new MemVector();
    for (int item = 0; item < length; ++item) {
      //test key equality
      int old = listsAtIndex[item][0];
      check:
      if (key == keysAtIndex[item]) {
        //test tuple equality
//        for (int i = 0; i < cols.intCols.length; ++i)
//          if (tuplesAtIndex.intData[p.xInt + i] != data.intData[pointer.xInt + cols.intCols[i]])
//            break check;
//        for (int i = 0; i < cols.doubleCols.length; ++i)
//          if (tuplesAtIndex.doubleData[p.xDouble + i] != data.doubleData[pointer.xDouble + cols.doubleCols[i]])
//            break check;
        int oldInt = old * chunkDim.xInt;
        for (int i = 0; i < cols.intCols.length; ++i)
          if (chunk.intData[oldInt + this.cols.intCols[i]] != data.intData[pointer.xInt + cols.intCols[i]])
            break check;
        int oldDouble = old * chunkDim.xDouble;
        for (int i = 0; i < cols.doubleCols.length; ++i)
          if (chunk.doubleData[oldDouble + this.cols.doubleCols[i]] != data.doubleData[pointer.xDouble + cols.doubleCols[i]])
            break check;
        int oldChunk = old * chunkDim.xChunk;
        for (int i = 0; i < cols.chunkCols.length; ++i)
          if (!chunk.chunkData[oldChunk + this.cols.chunkCols[i]].equals(data.chunkData[pointer.xChunk + cols.chunkCols[i]]))
            break check;

        //they are equal, let's just add the new value
        int[] list = listsAtIndex[item];
        if (listSizesAtIndex[item] == list.length) {
          int[] newList = new int[list.length + CAP_INCREASE_LIST];
          System.arraycopy(list, 0, newList, 0, list.length);
          list = newList;
          listsAtIndex[item] = list;
        }
        list[listSizesAtIndex[item]++] = row;
        return row;
      }
      p.xInt += keyDim.xInt;
      p.xDouble += keyDim.xDouble;
      p.xChunk += keyDim.xChunk;
    }
    //if we have arrived here the key-value pair has not yet been put
    //check if we need to increase the capacity
    if (length == keysAtIndex.length) {

      int[][] newListsAtIndex = new int[length + CAP_INCREASE_KEYS][];
      System.arraycopy(listsAtIndex, 0, newListsAtIndex, 0, length);
      listsAtIndex = newListsAtIndex;
      lists[index] = newListsAtIndex;

      int[] newListSizesAtIndex = new int[length + CAP_INCREASE_KEYS];
      System.arraycopy(listSizesAtIndex, 0, newListSizesAtIndex, 0, length);
      listSizesAtIndex = newListSizesAtIndex;
      listSizes[index] = listSizesAtIndex;

      int[] newKeysAtIndex = new int[length + CAP_INCREASE_KEYS];
      System.arraycopy(keysAtIndex, 0, newKeysAtIndex, 0, length);
      keysAtIndex = newKeysAtIndex;
      keys[index] = newKeysAtIndex;
    }
    //insert

    listsAtIndex[length] = new int[CAP_INCREASE_LIST];
    listsAtIndex[length][listSizesAtIndex[length]++] = row;
    keysAtIndex[length] = key;
    keyCounts[index]++;    
    ++numKeys;
//    if (length == 1 && keysAtIndex[0] == keysAtIndex[1]) {
//      System.out.println(Arrays.toString(keysAtIndex));
//      throw new RuntimeException("Huch");
//    }
    return -1;
  }

  public int get(int[] ints, double[] doubles, MemChunk[] chunks, int targetCell, int[][] listHolder) {
    return get(new MemHolder(1, 1, ints, doubles, chunks), MemVector.ZERO,
            new MemColumnSelector(ints.length, doubles.length, chunks.length), targetCell, listHolder);
  }


  public int get(MemHolder data, MemVector pointer, MemColumnSelector cols, int targetCell, int[][] listHolder) {
    if (capacity == 0) return 0;
    int key = 17;
    for (int col : cols.intCols)
      key = 37 * key + data.intData[pointer.xInt + col];
    for (int col : cols.doubleCols)
      key = 37 * key + (int) data.doubleData[pointer.xDouble + col];
    if (key < 0) key = -key;
    //todo: chunks
    int index = key % capacity;
    int[] keysAtIndex = keys[index];
    if (keysAtIndex == null) return 0;
    int length = keyCounts[index];
    current.set(0,0,0);
    for (int item = 0; item < length; ++item) {
      //test key equality
      int old = lists[index][item][0];
      check:
      if (key == keysAtIndex[item]) {
        //test tuple equality
        int oldInt = old * chunkDim.xInt;
        for (int i = 0; i < cols.intCols.length; ++i)
          if (chunk.intData[oldInt + this.cols.intCols[i]] != data.intData[pointer.xInt + cols.intCols[i]])
            break check;
        int oldDouble = old * chunkDim.xDouble;
        for (int i = 0; i < cols.doubleCols.length; ++i)
          if (chunk.doubleData[oldDouble + this.cols.doubleCols[i]] != data.doubleData[pointer.xDouble + cols.doubleCols[i]])
            break check;
        int oldChunk = old * chunkDim.xChunk;
        for (int i = 0; i < cols.chunkCols.length; ++i)
          if (!chunk.chunkData[oldChunk + this.cols.chunkCols[i]].equals(data.chunkData[pointer.xChunk + cols.chunkCols[i]]))
            break check;
        //they are equal, let's just set the new value
//        return old;
//        for (int i = 0; i < cols.intCols.length; ++i)
//          if (tuplesAtIndex.intData[current.xInt + i] != data.intData[pointer.xInt + cols.intCols[i]])
//            break check;
//        for (int i = 0; i < cols.doubleCols.length; ++i)
//          if (tuplesAtIndex.doubleData[current.xDouble + i] != data.doubleData[pointer.xDouble + cols.doubleCols[i]])
//            break check;
        //they are equal, let's just set the new value
        listHolder[targetCell] = lists[index][item];
        return listSizes[index][item];
      }
      //todo: necessary?
      current.xInt += cols.intCols.length;
      current.xDouble += cols.doubleCols.length;
      current.xChunk += cols.chunkCols.length;
    }
    return 0;
  }

  public void clear() {
    numKeys = 0;
    numUsedIndices = 0;
    indexedSoFar = 0;
    int i = 0;
    for (int index = 0; index < keyCounts.length; index++) {
      if (keyCounts[index] > 0) {
        keyCounts[index] = 0;
        for (int j = 0; j < listSizes[i].length; ++j)
          listSizes[i][j] = 0;

      }
      ++i;
    }
  }

  public void clearMemory() {
    Arrays.fill(keyCounts,0);
    keys = new int[capacity][];
    lists = new int[capacity][][];
    listSizes = new int[capacity][];
    numKeys = 0;
    numUsedIndices = 0;
    indexedSoFar = 0;
  }

  public void increaseCapacity(int howMuch) {
    //lets move the value lists to new places (and move the keys+tuples)
    MemShallowMultiIndex helper = new MemShallowMultiIndex(chunk,cols, capacity + howMuch, keyDim);
    MemColumnSelector cols = new MemColumnSelector(keyDim);
    MemVector p = new MemVector();
    for (int index = 0; index < lists.length; ++index) {
      if (lists[index] != null)
        for (int keyIndex = 0; keyIndex < keyCounts[index]; ++keyIndex) {
          p.set(lists[index][keyIndex][0], chunkDim);
          for (int valueIndex = 0; valueIndex < listSizes[index][keyIndex]; ++valueIndex) {
            helper.add(chunk, p, this.cols, lists[index][keyIndex][valueIndex]);
          }
        }
    }
    this.lists = helper.lists;
    this.listSizes = helper.listSizes;
    this.capacity = helper.capacity;
    this.keyCounts = helper.keyCounts;
    this.keys = helper.keys;
    this.numUsedIndices = helper.numUsedIndices;
    this.numKeys = helper.numKeys;
    
  }


  public int getNumKeys() {
    return numKeys;
  }

  public int getNumUsedIndices() {
    return numUsedIndices;
  }

  /**
   * Returns the ratio |keys|/|used indices|. High number means there are many keys occupying the same cell (resulting
   * in slower access).
   *
   * @return the ratio |keys|/|used indices|
   */
  public double getLoadFactor() {
    return (double) numKeys / (double) numUsedIndices;
  }

  public int byteSize() {
    int size = 0;
    //int size = 3 * MemChunk.ARRAYSIZE + 3 & MemChunk.POINTERSIZE + MemDim.byteSize() + 3 * MemChunk.INTSIZE;
    size += keys.length * MemHolder.POINTERSIZE;
    size += lists.length * MemHolder.POINTERSIZE;
    size += listSizes.length * MemHolder.POINTERSIZE;
    for (int i = 0; i < capacity; ++i)
      if (keyCounts[i] > 0) {
        size += keys[i].length * MemHolder.POINTERSIZE;
        size += lists[i].length * MemHolder.POINTERSIZE;
        size += keys[i].length * MemHolder.INTSIZE;
        size += listSizes[i].length * MemHolder.INTSIZE;
        for (int[] list : lists[i]) {
          if (list != null) size += list.length * MemHolder.INTSIZE;
        }
      }
    return size;
  }

  public static void serialize(MemShallowMultiIndex index, MemSerializer serializer) throws IOException {
    serializer.writeInts(index.capacity, index.keyDim.xInt, index.keyDim.xDouble,
            index.keyDim.xChunk, index.numKeys, index.numUsedIndices, index.indexedSoFar);
    index.cols.serialize(serializer);
    for (int i = 0; i < index.capacity; ++i) {
      if (index.keyCounts[i] == 0) {
        serializer.writeInts(0);
      } else {
        int size = index.keyCounts[i];
        serializer.writeInts(size);
        //MemHolder.serialize(index.tuples[i], serializer, index.dim);
        serializer.writeInts(index.keys[i], size);
        serializer.writeInts(index.listSizes[i], size);
        for (int j = 0; j < size; ++j) {
          serializer.writeInts(index.lists[i][j], index.listSizes[i][j]);
        }
      }
    }
  }

//    public static void deserializeInPlace(MemDeserializer deserializer, MemChunkIndex index) throws IOException {
//    int[] stats = new int[6];
//    index.clear();
//    deserializer.read(stats, 6);
////    MemChunkIndex index = new MemChunkIndex(stats[0], new MemDim(stats[1], stats[2], stats[3]));
//    int savedSize = stats[0];
//    if (index.capacity < savedSize)
//      index.increaseCapacity(savedSize - index.capacity);
//    index.dim.xInt = stats[1];
//    index.dim.xDouble = stats[2];
//    index.dim.xChunk = stats[3];
//    index.numKeys = stats[4];
//    index.numUsedIndices = stats[5];
//    int[] size = new int[1];
//    for (int i = 0; i < savedSize; ++i) {
//      deserializer.read(size, 1);
//      if (size[0] > 0) {
//        if (index.tuples[i] == null)
//          index.tuples[i] = MemHolder.deserialize(deserializer, index.dim);
//        else
//          MemHolder.deserializeInPlace(deserializer, index.dim, index.tuples[i]);
//        if (index.keys[i] == null || index.keys[i].length < size[0])
//          index.keys[i] = new int[size[0]];
//        deserializer.read(index.keys[i], size[0]);
//        if (index.values[i] == null || index.values[i].length < size[0])
//          index.values[i] = new int[size[0]];
//        deserializer.read(index.values[i], size[0]);
//      }
//    }

  //  }

  public static MemShallowMultiIndex deserializeInPlace(MemDeserializer deserializer, MemShallowMultiIndex index) throws IOException {
    int[] stats = new int[7];
    deserializer.read(stats, 7);
    index.cols = MemColumnSelector.deserialize(deserializer);
    //MemChunkMultiIndex index = new MemChunkMultiIndex(stats[0], new MemDim(stats[1], stats[2], stats[3]));
    index.numKeys = stats[4];
    index.numUsedIndices = stats[5];
    index.indexedSoFar = stats[6];
    int savedSize = stats[0];
    if (index.capacity < savedSize)
      index.increaseCapacity(savedSize - index.capacity);

    int[] sizeBuffer = new int[1];
    for (int i = 0; i < index.capacity; ++i) {
      deserializer.read(sizeBuffer, 1);
      int size = sizeBuffer[0];
      index.keyCounts[i] = size;
      if (size > 0) {
        if (index.keys[i] == null || index.keys[i].length < size)
          index.keys[i] = new int[size];
        deserializer.read(index.keys[i], size);
        if (index.listSizes[i] == null || index.listSizes[i].length < size)
          index.listSizes[i] = new int[size];
        deserializer.read(index.listSizes[i], size);
        if (index.lists[i] == null || index.lists[i].length < size)
          index.lists[i] = new int[size][];
        for (int j = 0; j < size; ++j) {
          if (index.lists[i][j] == null || index.lists[i][j].length < index.listSizes[i][j])
            index.lists[i][j] = new int[index.listSizes[i][j]];
          deserializer.read(index.lists[i][j], index.listSizes[i][j]);
        }
      }
    }
    return index;
  }


  public static MemShallowMultiIndex deserialize(MemDeserializer deserializer, MemChunk chunk) throws IOException {
    int[] stats = new int[7];
    deserializer.read(stats, 7);
    MemColumnSelector cols = MemColumnSelector.deserialize(deserializer);    
    MemShallowMultiIndex index = new MemShallowMultiIndex(chunk,cols,stats[0], MemDim.create(stats[1], stats[2], stats[3]));
    index.numKeys = stats[4];
    index.numUsedIndices = stats[5];
    index.indexedSoFar = stats[6];
    int[] sizeBuffer = new int[1];
    for (int i = 0; i < index.capacity; ++i) {
      deserializer.read(sizeBuffer, 1);
      int size = sizeBuffer[0];
      index.keyCounts[i] = size;
      if (size > 0) {
        index.keys[i] = new int[size];
        deserializer.read(index.keys[i], size);
        index.listSizes[i] = new int[size];
        deserializer.read(index.listSizes[i], size);
        index.lists[i] = new int[size][];
        for (int j = 0; j < size; ++j) {
          index.lists[i][j] = new int[index.listSizes[i][j]];
          deserializer.read(index.lists[i][j], index.listSizes[i][j]);
        }
      }
    }
    return index;
  }


  public int getCapacity() {
    return capacity;
  }
}
