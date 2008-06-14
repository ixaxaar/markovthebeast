package thebeast.util;

import java.util.Arrays;

/**
 * This hashtable is basically just a storage for a hash table.
 * Ideally, in an inner loop all operations on the hashtable are to be manually executed
 * by the client. This can be achieved by inlining the put and get methods
 * in the clients code.
 * <p/>
 * <p>As of now when putting a new value for an existing key the table
 * will act as if it is a new key, that is it will increase the totalKeyCount
 * and will also need more space. This is to be done.
 *
 * @author Sebastian Riedel
 */
public final class IntTuple2IntHashtable {
    public int[][] keys;
    public int[] keyCounts;
    public int[][] values;
    public int[] currentInsertionPoints;
    public int initialCapacity;
    public int size;
    public int totalKeyCount;
    public int[] keySet;
    public int dimension;
    private int keyPointer;
    public static boolean COLLECTKEYS = false;


    public int getKeyCount() {
        return totalKeyCount;
    }


    public IntTuple2IntHashtable() {
        this(0,0,0);
    }

    public IntTuple2IntHashtable(int dimension, int size, int initialCapacity) {
        keyCounts = new int[size];
        keys = new int[size][];
        values = new int[size][];
        currentInsertionPoints = new int[size];
        if (COLLECTKEYS) keySet = new int[size * dimension];

        init(dimension, size, initialCapacity);
    }


    public void compactify() {
        for (int i = 0; i < size; ++i) {
            int count = keyCounts[i];
            if (count < values[i].length) {
                int[] newValues = new int[count];
                System.arraycopy(values[i], 0, newValues, 0, count);
                int[] newKeys = new int[count * dimension];
                System.arraycopy(keys[i], 0, newKeys, 0, count * dimension);
                values[i] = newValues;
                keys[i] = newKeys;

            }
        }
    }

    public void init(int dimension, int size, int initialCapacity) {
        this.dimension = dimension;
        if (size > keys.length) {
            keyCounts = new int[2 * size];
            keys = new int[2 * size][];
            values = new int[2 * size][];
            currentInsertionPoints = new int[2 * size];
            if (COLLECTKEYS) keySet = new int[2 * size * dimension];
        }
        Arrays.fill(keyCounts, 0);
        Arrays.fill(currentInsertionPoints, 0);

        totalKeyCount = 0;
        this.size = size;
        this.initialCapacity = initialCapacity;
        for (int i = 0; i < size; ++i) {
            keys[i] = new int[initialCapacity * dimension];
            values[i] = new int[initialCapacity];
        }
    }


    public void put(int value, int[] data, int pointer, int... offsets) {
        int key = 17;
        for (int v : offsets)
            key = 37 * key + data[pointer + v];
        if (key < 0) key = -key;
        int index = key % size;
        int insertionPoint = currentInsertionPoints[index]++;
        int[] keyList = keys[index];
        int[] valueList = values[index];

        if (insertionPoint == valueList.length) {
            keys[index] = new int[keyList.length + initialCapacity * dimension];
            System.arraycopy(keyList, 0, keys[index], 0, keyList.length);
            values[index] = new int[valueList.length + initialCapacity];
            System.arraycopy(valueList, 0, values[index], 0, valueList.length);
            keyList = keys[index];
            valueList = values[index];
        }

        int offsetInsertionPoint = insertionPoint * dimension;
        for (int offset : offsets) {
            keyList[offsetInsertionPoint++] = data[pointer + offset];
        }
        //keyList[insertionPoint] = key;
        valueList[insertionPoint] = value;
        if (COLLECTKEYS && keyPointer + dimension >= keySet.length) {
            int[] old = keySet;
            keySet = new int[2 * keyPointer];
            System.arraycopy(old, 0, keySet, 0, old.length);
        }

        ++keyCounts[index];
        ++totalKeyCount;
        if (COLLECTKEYS) for (int i : offsets) {
            assert pointer + i < data.length;
            assert keyPointer < keySet.length :
                    "keySet.length=" + keySet.length +
                            " keyPointer=" + keyPointer + " i=" + i;
            keySet[keyPointer++] = data[pointer + i];
        }
    }

    public void put(int value, int... keyTuple) {
        int key = 17;
        for (int v : keyTuple)
            key = 37 * key + v;
        if (key < 0) key = -key;
        int index = key % size;
        int insertionPoint = currentInsertionPoints[index]++;
        int[] keyList = keys[index];
        int[] valueList = values[index];

        if (insertionPoint == keyList.length) {
            keys[index] = new int[keyList.length + initialCapacity * dimension];
            System.arraycopy(keyList, 0, keys[index], 0, keyList.length);
            values[index] = new int[valueList.length + initialCapacity];
            System.arraycopy(valueList, 0, values[index], 0, valueList.length);
            keyList = keys[index];
            valueList = values[index];
        }

        for (int i = 0; i < dimension; ++i) {
            keyList[insertionPoint + i] = keyTuple[i];
        }
        //keyList[insertionPoint] = key;
        valueList[insertionPoint] = value;
        if (COLLECTKEYS && keyPointer + dimension >= keySet.length) {
            int[] old = keySet;
            keySet = new int[2 * keyPointer];
            System.arraycopy(old, 0, keySet, 0, old.length);
        }

        ++keyCounts[index];
        ++totalKeyCount;
        if (COLLECTKEYS) for (int i = 0; i < dimension; ++i) {
            keySet[keyPointer++] = keyTuple[i];
        }
    }


    public int get(int... keyTuple) {
        int key = 17;
        for (int v : keyTuple)
            key = 37 * key + v;
        if (key < 0) key = -key;
        int index = key % size;
        int[] keyList = keys[index];
        int[] valueList = values[index];
        int count = keyCounts[index];

        for (int i = 0; i < count; ++i) {
            boolean keyMatches = true;
            for (int d = 0; d < dimension; ++d)
                if (keyList[i * dimension + d] != keyTuple[d]) {
                    keyMatches = false;
                    break;
                }
            if (keyMatches) {
                return valueList[i];
            }
        }

        return -1;
    }

    public int get(int[] data, int pointer, int... offsets) {
        int key = 17;
        for (int v : offsets)
            key = 37 * key + data[pointer + v];
        if (key < 0) key = -key;
        int index = key % size;
        int[] keyList = keys[index];
        int[] valueList = values[index];
        int count = keyCounts[index];

        for (int i = 0; i < count; ++i) {
            boolean keyMatches = true;
            int keyListPointer = i * dimension;
            for (int d : offsets)
                if (keyList[keyListPointer++] != data[pointer + d]) {
                    keyMatches = false;
                    break;
                }
            if (keyMatches) {
                return valueList[i];
            }
        }

        return -1;
    }

}
