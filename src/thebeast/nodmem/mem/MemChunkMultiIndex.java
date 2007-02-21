package thebeast.nodmem.mem;

/**
 * This index maps from row data to row numbers.
 *
 * @author Sebastian Riedel
 */
public final class MemChunkMultiIndex {

  private MemColumnSelector cols;

  //at each index the mcmi stores a list of tuples (one for each key with that index)
  private MemHolder[] tuples;
  //at each index the mcmi stores a list of value lists (one value list for each key)
  private int[][][] lists;
  //the sizes of the value lists
  private int[][] listSizes;
  //the actual keys at each index
  private int[][] keys;
  private int capacity;
  private MemDim dim;
  private int numKeys;
  private int numUsedIndices;
  private static final int CAP_INCREASE_LIST = 1;
  private static final int CAP_INCREASE_KEYS = 1;


  public MemChunkMultiIndex(int capacity, MemDim dim) {
    tuples = new MemHolder[capacity];
    keys = new int[capacity][];
    lists = new int[capacity][][];
    listSizes = new int[capacity][];
    this.capacity = capacity;
    this.dim = dim;
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
    MemHolder tuplesAtIndex = tuples[index];
    if (keysAtIndex == null) {
      keysAtIndex = new int[CAP_INCREASE_LIST];
      tuplesAtIndex = new MemHolder(0, CAP_INCREASE_LIST, dim);
      listsAtIndex = new int[CAP_INCREASE_LIST][CAP_INCREASE_LIST];
      listSizesAtIndex = new int[CAP_INCREASE_LIST];
      lists[index] = listsAtIndex;
      keys[index] = keysAtIndex;
      tuples[index] = tuplesAtIndex;
      listSizes[index] = listSizesAtIndex;
    }
    int length = tuplesAtIndex.size;
    if (length == 0) ++numUsedIndices;
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
      p.xInt += dim.xInt;
      p.xDouble += dim.xDouble;
      p.xChunk += dim.xChunk;
    }
    //if we have arrived here the key-value pair has not yet been put
    //check if we need to increase the capacity
    if (length == tuplesAtIndex.capacity) {
      tuplesAtIndex.increaseCapacity(CAP_INCREASE_KEYS,dim);

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
    for (int i = 0; i < cols.intCols.length; ++i)
      tuplesAtIndex.intData[p.xInt + i] = data.intData[pointer.xInt + cols.intCols[i]];
    for (int i = 0; i < cols.doubleCols.length; ++i)
      tuplesAtIndex.doubleData[p.xDouble + i] = data.doubleData[pointer.xDouble + cols.doubleCols[i]];
    ++tuplesAtIndex.size;

    listsAtIndex[length] = new int[CAP_INCREASE_LIST];
    listsAtIndex[length][listSizesAtIndex[length]++] = row;
    keysAtIndex[length] = key;
    ++numKeys;
    return -CAP_INCREASE_LIST;
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
        //they are equal, let's just set the new value
        listHolder[targetCell] = lists[index][item];
        return listSizes[index][item];
      }
      p.xInt += cols.intCols.length;
      p.xDouble += cols.doubleCols.length;
      p.xChunk += cols.chunkCols.length;
    }
    return 0;
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
    lists = new int[capacity][][];
    listSizes = new int[capacity][];
    numKeys = 0;
    numUsedIndices = 0;
  }

  public void increaseCapacity(int howMuch) {
    //lets move the value lists to new places (and move the keys+tuples)
    MemChunkMultiIndex helper = new MemChunkMultiIndex(capacity + howMuch, dim);
    MemColumnSelector cols = new MemColumnSelector(dim);
    for (int index = 0; index < lists.length; ++index) {
      MemVector p = new MemVector();
      if (lists[index] != null)
        for (int keyIndex = 0; keyIndex < lists[index].length; ++keyIndex) {
          for (int valueIndex = 0; valueIndex < listSizes[index][keyIndex]; ++valueIndex) {
            helper.add(tuples[index], p, cols, lists[index][keyIndex][valueIndex]);
          }
          p.xInt += dim.xInt;
          p.xDouble += dim.xDouble;
          p.xChunk += dim.xChunk;
        }
    }
    this.lists = helper.lists;
    this.listSizes = helper.listSizes;
    this.capacity = helper.capacity;
    this.tuples = helper.tuples;
    this.keys = helper.keys;
    this.numUsedIndices = helper.numUsedIndices;
  }


  public int getNumKeys() {
    return numKeys;
  }

  public int getNumUsedIndices () {
    return numUsedIndices;
  }

  /**
   * Returns the ratio |keys|/|used indices|. High number means there are many keys
   * occupying the same cell (resulting in slower access).
   *
   * @return the ratio |keys|/|used indices|
   */
  public double getLoadFactor () {
    return (double) numKeys / (double) numUsedIndices;
  }

  public int byteSize() {
    int size = 0;
    size += tuples.length * MemHolder.POINTERSIZE;
    size += keys.length * MemHolder.POINTERSIZE;
    size += lists.length * MemHolder.POINTERSIZE;
    size += listSizes.length * MemHolder.POINTERSIZE;
    for (int i = 0; i < capacity; ++i)
      if (tuples[i] != null) {
        size += tuples[i].byteSize();
        size += keys[i].length * MemHolder.POINTERSIZE;
        size += lists[i].length * MemHolder.POINTERSIZE;
        size += keys[i].length * MemHolder.INTSIZE;
        size += listSizes[i].length * MemHolder.INTSIZE;
        for (int[] list : lists[i]){
          if (list!=null) size += list.length * MemHolder.INTSIZE;
        }
      }
    return size;
  }
}
