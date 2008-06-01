package thebeast.util;

/**
 * This hashtable is basically just a storage for a hash table.
 * Ideally, in an inner loop all operations on the hashtable are to be manually executed
 * by the client. This can be achieved by inlining the put and get methods
 * in the clients code.
 *
 * @author Sebastian Riedel
 */
public final class Int2IntMultiHashtable {
    public int[][] keys;
    public int[] keyCounts;
    //public int[][] values;
    public int[][][] valueLists;
    public int[][] valueListSizes;
    public int[] currentInsertionPoints;
    public int initialCapacity;
    public final int size;
    public int initialListCapacity;

    public Int2IntMultiHashtable(int size, int initialCapacity, int initialListCapacity) {
        this.size = size;
        this.initialCapacity = initialCapacity;
        keyCounts = new int[size];
        keys = new int[size][];
        valueLists = new int[size][][];
        valueListSizes = new int[size][];
        currentInsertionPoints = new int[size];
        this.initialListCapacity = initialListCapacity;


        for (int i = 0; i < size; ++i) {
            keys[i] = new int[initialCapacity];
            valueLists[i] = new int[initialCapacity][];
            valueListSizes[i] = new int[initialCapacity];
        }
    }

    public void compactify() {
        for (int i = 0; i < size; ++i) {
            int keyCount = keyCounts[i];
            int[] keyList = keys[i];
            if (keyCount < keyList.length) {
                int[] newKeyList = new int[keyCount];
                System.arraycopy(keyList, 0, newKeyList, 0, keyCount);
                keys[i] = newKeyList;
            }
            if (keyCount < valueLists[i].length) {
                int[][] newValueList = new int[keyCount][];
                for (int keyIndex = 0; keyIndex < keyCount; ++keyIndex) {
                    if (valueListSizes[i][keyIndex] < valueLists[i][keyIndex].length) {
                        newValueList[keyIndex] = new int[valueListSizes[i][keyIndex]];
                        System.arraycopy(valueLists[i][keyIndex], 0,
                                newValueList[keyIndex], 0, valueListSizes[i][keyIndex]);
                    } else {
                        newValueList[keyIndex] = valueLists[i][keyIndex];
                    }
                }
                valueLists[i] = newValueList;
            } else {
                for (int keyIndex = 0; keyIndex < keyCount; ++keyIndex) {
                    if (valueListSizes[i][keyIndex] < valueLists[i][keyIndex].length) {
                        int[] newValueList = new int[valueListSizes[i][keyIndex]];
                        System.arraycopy(valueLists[i][keyIndex], 0,
                                newValueList, 0, valueListSizes[i][keyIndex]);
                        valueLists[i][keyIndex] = newValueList;
                    }
                }

            }
        }
    }


    public void add(int key, int value) {
        int index = key % size;
        //first check if we can find the key
        int[] keysForIndex = keys[index];
        int[][] valueListsForIndex = valueLists[index];
        int[] valueListSizesForIndex = valueListSizes[index];
        int keyCount = keyCounts[index];
        //check if we can
        for (int i = 0; i < keyCount; ++i) {
            if (keysForIndex[i] == key) {
                int[] valueListForKey = valueListsForIndex[i];
                int size = valueListSizesForIndex[i];
                //check if there is space left in the list
                if (valueListForKey.length == size) {
                    valueListForKey = new int[valueListForKey.length + initialListCapacity];
                    System.arraycopy(valueListsForIndex[i], 0, valueListForKey, 0, size);
                    valueListsForIndex[i] = valueListForKey;
                }
                //add value to list
                valueListForKey[size] = value;
                ++valueListSizesForIndex[i];
                return;
            }
        }
        //key not inserted yet -> add a new key
        //check if we have enough space for a new key
        if (keyCount == keysForIndex.length) {
            keysForIndex = new int[keysForIndex.length + initialCapacity];
            System.arraycopy(keys[index], 0, keysForIndex, 0, keys[index].length);
            keys[index] = keysForIndex;
            //we also need to copy the value lists
            valueListsForIndex = new int[valueListsForIndex.length + initialCapacity][];
            System.arraycopy(valueLists[index], 0, valueListsForIndex, 0, valueLists[index].length);
            valueLists[index] = valueListsForIndex;
            //and the valueListSizes
            valueListSizesForIndex = new int[valueListSizesForIndex.length + initialCapacity];
            System.arraycopy(valueListSizes[index], 0, valueListSizesForIndex, 0, valueListSizes[index].length);
            valueListSizes[index] = valueListSizesForIndex;
        }
        valueListSizesForIndex[keyCount] = 1;
        valueListsForIndex[keyCount] = new int[initialListCapacity];
        valueListsForIndex[keyCount][0] = value;
        keysForIndex[keyCount] = key;
        ++keyCounts[index];

    }


    public int getList(int key, int dstPointer, int[] dstSize, int[][] dstList) {
        if (size > 0) {
            int index = key % size;
            //first check if we can find the key
            int[] keysForIndex = keys[index];
            int[][] valueListsForIndex = valueLists[index];
            int[] valueListSizesForIndex = valueListSizes[index];
            int keyCount = keyCounts[index];
            //check if we can
            for (int i = 0; i < keyCount; ++i) {
                if (keysForIndex[i] == key) {
                    int[] valueListForKey = valueListsForIndex[i];
                    int size = valueListSizesForIndex[i];
                    dstSize[dstPointer] = size;
                    dstList[dstPointer] = valueListForKey;
                    return size;
                }
            }
        }
        dstSize[dstPointer] = 0;
        dstList[dstPointer] = new int[0];
        return 0;
    }

//
//    public int getByPrimaryKey(int[] data, int pointer, int... primaryKeys) {
//
//        int key = 17;
//        for (int primaryKey : primaryKeys)
//            key = 37 * key + data[pointer + primaryKey];
//        //TODO: inline this eventually
//        return get(key);
//    }
//
//    public int get(int key) {
//        int index = key % size;
//        int[] keyList = keys[index];
//        int[] valueList = values[index];
//        int count = keyCounts[index];
//
//        for (int i = 0; i < count; ++i)
//            if (keyList[i] == key) {
//                return valueList[i];
//            }
//
//        return -1;
//    }
}
