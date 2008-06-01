package thebeast.util;

import java.util.Arrays;


/**
 * This hashtable is basically just a storage for a hash table.
 * Ideally, in an inner loop all operations on the hashtable are to be manually executed
 * by the client. This can be achieved by inlining the put and get methods
 * in the clients code.
 *
 * <p>As of now when putting a new value for an existing key the table
 * will act as if it is a new key, that is it will increase the totalKeyCount
 * and will also need more space. This is to be done.
 *
 * @author Sebastian Riedel
 */
public final class Int2IntHashtable {
    public int[][] keys;
    public int[] keyCounts;
    public int[][] values;
    public int[] currentInsertionPoints;
    public int initialCapacity;
    public int size;
    public int totalKeyCount;
    public int[] keySet;

    public Int2IntHashtable(int size, int initialCapacity) {
        keyCounts = new int[size];
        keys = new int[size][];
        values = new int[size][];
        currentInsertionPoints = new int[size];
        keySet = new int[size];

        init(size, initialCapacity);
    }


    public Int2IntHashtable() {
        this(0,0);
    }

    public void compactify(){
        for (int i = 0; i < size; ++i){
            int count = keyCounts[i];
            if (count < values[i].length){
                int[] newValues = new int[count];
                System.arraycopy(values[i],0,newValues, 0, count);
                values[i] = newValues;
            }
        }
    }

    public void init(int size, int initialCapacity) {
        if (size > keys.length){
            keyCounts = new int[2 * size];
            keys = new int[2 * size][];
            values = new int[2 * size][];
            currentInsertionPoints = new int[2 * size];
            keySet = new int[2 * size];
        }
        Arrays.fill(keyCounts,0);
        Arrays.fill(currentInsertionPoints,0);

        totalKeyCount = 0;
        this.size = size;
        this.initialCapacity = initialCapacity;
        for (int i = 0; i < size; ++i) {
            keys[i] = new int[initialCapacity];
            values[i] = new int[initialCapacity];
        }
    }


    public int calculateKey(int[] row, int from, int to){
        int key = 17;
        for (int i = from; i < to; ++i)
            key = 37 * key + row[i];
        return key > 0 ? key : -key;
    }

    public int calculateKey(int[] data, int pointer, int[] primaryKeys){
        int key = 17;
        for (int primaryKey : primaryKeys)
            key = 37 * key + data[pointer + primaryKey];
        return key > 0 ? key : -key;
    }

    public void put(int key, int value) {
        int index = key % size;
        int insertionPoint = currentInsertionPoints[index]++;
        int[] keyList = keys[index];
        int[] valueList = values[index];

        if (insertionPoint == keyList.length) {
            keys[index] = new int[keyList.length + initialCapacity];
            System.arraycopy(keyList, 0, keys[index], 0, keyList.length);
            values[index] = new int[valueList.length + initialCapacity];
            System.arraycopy(valueList, 0, values[index], 0, valueList.length);
            keyList = keys[index];
            valueList = values[index];
        }

        keyList[insertionPoint] = key;
        valueList[insertionPoint] = value;
        if (totalKeyCount == keySet.length){
            int[] old = keySet;
            keySet = new int[2 * keySet.length];
            System.arraycopy(old, 0, keySet, 0, old.length);
        }
        
        ++keyCounts[index];
        keySet[totalKeyCount++] = key;
    }

    public int getByPrimaryKey(int[] data, int pointer, int... primaryKeys) {

        if (size == 0) return -1;
        int key = 17;
        for (int primaryKey : primaryKeys)
            key = 37 * key + data[pointer + primaryKey];
        //TODO: inline this eventually
        key = key > 0 ? key : -key;
        //inlined
        int index = key % size;
        int[] keyList = keys[index];
        int[] valueList = values[index];
        int count = keyCounts[index];

        for (int i = 0; i < count; ++i)
            if (keyList[i] == key) {
                return valueList[i];
            }

        return -1;
        //return get(key);
    }

    public int get(int key) {
        int index = key % size;
        int[] keyList = keys[index];
        int[] valueList = values[index];
        int count = keyCounts[index];

        for (int i = 0; i < count; ++i)
            if (keyList[i] == key) {
                return valueList[i];
            }

        return -1;
    }
}
