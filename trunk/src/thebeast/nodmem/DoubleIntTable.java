package thebeast.nodmem;

import thebeast.util.ArrayIndex;
import thebeast.util.Int2IntHashtable;
import thebeast.util.Int2IntMultiHashtable;
import thebeast.util.IntTuple2IntHashtable;
import thebeast.forml.processing.RowUniquifier;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * A table is a list of rows which contain integers. Use this class with great care as it does not make
 * any checks at any time at all.
 * <p/>
 * <p>Its implementation is based on a contigous array. Clients are encouraged to access this contigous
 * array directly, using the {@link DoubleIntTable#getIntData()} method. The contents
 * of the table can be access using <code>table.getData()[row * table.getColumnCount() + column]</code>.
 * <p/>
 * <p>A table can also have a weight column of type <code>double</code>
 * which can be used for numerous purposes (storing feature weights, storing
 * row scores,...).
 * <p/>
 * <p>There are several options of adding rows to a table. Either add rows using <code>addRow</code>
 * which is safe (you need to call {@link DoubleIntTable#close()} when done)
 * or adding data to the table data directly, which is fast. Make sure to set the {@link DoubleIntTable#setSize(int)}
 * correctly afterwards (or set the datapointer to after the end of the inserted data and call
 * {@link DoubleIntTable#close()}.
 * <p/>
 * <p>Also note that the initial Table as created by the constructors will never come with any indices
 * already created. Build them according to your own needs.
 *
 * @author Sebastian Riedel
 */
public final class DoubleIntTable {

    public static final int NO_SEQUENTIAL_COLUMN = -1;

    private final int columnCount;
    private int[] dimensions;
    private int capacityIncrements;
    private int[] intData;
    private ArrayIndex[] indices;
    private boolean hasDoubles;
    private boolean hasInts;
    private double[] doubleData;
    private int[] domainSizes;
    private int[][] domains;
    private int sequentialColumn = -1;
    private boolean hasSequentialColumn = false;
    private int[] primaryKeys;
    private Int2IntHashtable primaryKey2Row;
    private int[] multiIndexColumns;
    private Int2IntMultiHashtable multiColumnIndex;
    private Int2IntMultiHashtable[] lightIndices;

    private int currentIntData = 0;
    private int size = 0;
    private static final int DEFAULT_INCREMENTS = 100;
    private IntTuple2IntHashtable noClashPrimaryKey2Row;

    public DoubleIntTable(int initialCapacity, int... dimensions) {
        this.columnCount = dimensions.length;
        this.dimensions = dimensions;
        intData = new int[columnCount * initialCapacity];
        //TODO: this is a very adhoc way of coming up with good capacity increments
        this.capacityIncrements = initialCapacity == 0 ? DEFAULT_INCREMENTS : initialCapacity;
        this.indices = new ArrayIndex[dimensions.length];
        this.lightIndices = new Int2IntMultiHashtable[dimensions.length];
        hasDoubles = false;
        primaryKeys = new int[columnCount];
        for (int i = 0; i < columnCount; ++i) primaryKeys[i] = i;
    }

    public DoubleIntTable(int initialCapacity, int sequentialColumn, boolean hasWeights, int... dimensions) {
        this.sequentialColumn = sequentialColumn;
        hasSequentialColumn = !(sequentialColumn == NO_SEQUENTIAL_COLUMN);
        this.columnCount = dimensions.length;
        this.dimensions = dimensions;
        intData = new int[columnCount * initialCapacity];
        this.capacityIncrements = initialCapacity == 0 ? DEFAULT_INCREMENTS : initialCapacity;
        this.hasDoubles = hasWeights;
        this.indices = new ArrayIndex[dimensions.length];
        this.lightIndices = new Int2IntMultiHashtable[dimensions.length];
        if (hasWeights) {
            doubleData = new double[initialCapacity];
        }
        //initialise primary keys to use all columns
        primaryKeys = new int[columnCount];
        for (int i = 0; i < columnCount; ++i) primaryKeys[i] = i;
    }

    public void setPrimaryKeys(int... primaryKeys) {
        this.primaryKeys = primaryKeys;
    }


    public int[] getMultiIndexColumns() {
        return multiIndexColumns;
    }

    public Int2IntMultiHashtable getMultiColumnIndex() {
        return multiColumnIndex;
    }

    public void setMultiIndexColumns(int... multiIndexColumns) {
        this.multiIndexColumns = multiIndexColumns;
    }

    public int getSequentialColumn() {
        return sequentialColumn;
    }

    public boolean hasSequentialColumn() {
        return hasSequentialColumn;
    }

    public int getCurrentPointer() {
        return currentIntData;
    }

    public boolean hasWeights() {
        return hasDoubles;
    }

    public int getData(int row, int column) {
        return intData[row * columnCount + column];
    }

    public int getData(int column) {
        return intData[currentIntData + column];
    }

    public void setSize(int size) {
        this.size = size;
        primaryKey2Row = null;
        multiColumnIndex = null;
        this.indices = new ArrayIndex[dimensions.length];
    }

    public double getWeight(int row) {
        return doubleData[row];
    }

    public double getWeight() {
        return doubleData[currentIntData / dimensions.length];
    }

    public double[] getDoubleData() {
        return doubleData;
    }

    public void advance(int rows) {
        currentIntData += rows * columnCount;
    }

    public void setData(int row, int column, int value) {
        intData[row * columnCount + column] = value;
    }

    public void scale(double scale){
        for (int row = 0; row < size;++row) doubleData[row] *= scale;
    }

    public void addRow(int... row) {
        if (currentIntData + row.length >= intData.length) increaseCapacity();
        for (int i = 0; i < row.length; ++i) intData[currentIntData++] = row[i];
    }

    public void addWeightedRow(double weight, int... row) {
        if (currentIntData + row.length >= intData.length) increaseCapacity();
        doubleData[currentIntData / dimensions.length] = weight;
        for (int i = 0; i < row.length; ++i) intData[currentIntData++] = row[i];
        //TODO: have a weight pointer as well, maybe
    }

    public void close() {
        size = (currentIntData / dimensions.length);
        primaryKey2Row = null;
        multiColumnIndex = null;
        this.indices = new ArrayIndex[dimensions.length];

    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return intData.length / columnCount;
    }

    public int[] getIntData() {
        return intData;
    }

    public void seek(int row) {
        currentIntData = row * columnCount;
    }

    public void seekEnd() {
        currentIntData = size * columnCount;
    }


    public boolean atEnd() {
        return currentIntData == intData.length;
    }

    public ArrayIndex[] getIndices() {
        return indices;
    }

    public void increaseCapacity() {
        int[] old = intData;
        intData = new int[intData.length + capacityIncrements];
        System.arraycopy(old, 0, intData, 0, old.length);
        if (hasDoubles) {
            double[] oldWeights = doubleData;
            doubleData = new double[doubleData.length + capacityIncrements];
            System.arraycopy(oldWeights, 0, doubleData, 0, oldWeights.length);
        }
    }


    public void increaseCapacityAtLeast(int atleast) {
        int[] old = intData;
        int capacityIncrements = this.capacityIncrements > atleast ? this.capacityIncrements : atleast;
        intData = new int[intData.length + capacityIncrements];
        System.arraycopy(old, 0, intData, 0, old.length);
        if (hasDoubles) {
            double[] oldWeights = doubleData;
            doubleData = new double[doubleData.length + capacityIncrements];
            System.arraycopy(oldWeights, 0, doubleData, 0, oldWeights.length);
        }
    }


    public void increaseCapacity(int capacity) {
        int[] old = intData;
        intData = new int[intData.length + capacity];
        System.arraycopy(old, 0, intData, 0, old.length);
        if (hasDoubles) {
            double[] oldWeights = doubleData;
            doubleData = new double[doubleData.length + capacity];
            System.arraycopy(oldWeights, 0, doubleData, 0, oldWeights.length);
        }
    }


    public void buildDomains() {
        domainSizes = new int[dimensions.length];
        domains = new int[dimensions.length][];
        for (int column = 0; column < dimensions.length; ++column) {
            boolean[] contains = new boolean[dimensions[column]];
            domains[column] = new int[dimensions.length];
            for (int row = 0; row < size; ++row) {
                int value = intData[row * dimensions.length + column];
                if (!contains[value]) {
                    domains[column][domainSizes[column]++] = value;
                    contains[value] = true;
                }
            }
        }
    }

    public int[] getDomainSizes() {
        return domainSizes;
    }

    public int[][] getDomains() {
        return domains;
    }


    public Int2IntMultiHashtable[] getLightIndices() {
        return lightIndices;
    }

//    public void buildIndex(int column, int initialCapacity) {
//
//        indices[column] = new ArrayIndex(dimensions[column]);
//
//        //iterate over data
//        for (int row = 0; row < size; ++row) {
//            //go over each column
//            int value = data[row * dimensions.length + column];
//            if (indices[column].rows[value] == null) {
//                indices[column].rows[value] = new int[initialCapacity];
//                indices[column].rows[value][0] = row;
//                indices[column].counts[value] = 1;
//            } else {
//                int[] rowsForValue = indices[column].rows[value];
//                if (rowsForValue.length == indices[column].counts[value]) {
//                    int[] newRows = new int[rowsForValue.length + initialCapacity];
//                    System.arraycopy(rowsForValue, 0, newRows, 0, rowsForValue.length);
//                    newRows[indices[column].counts[value]++] = row;
//                    indices[column].rows[value] = newRows;
//                } else {
//                    rowsForValue[indices[column].counts[value]++] = row;
//                }
//            }
//        }
//
//    }

    public boolean containsRow(int... row) {
        if (primaryKey2Row == null) {
            buildPrimaryKeyIndex();
        }
        return primaryKey2Row.getByPrimaryKey(row, 0, primaryKeys) != -1;
    }

    public void buildLightIndices() {
        lightIndices = new Int2IntMultiHashtable[columnCount];
        int pointer = 0;
        for (int col = 0; col < columnCount; ++col) {
            lightIndices[col] = new Int2IntMultiHashtable(size, 2, 2);
        }
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                lightIndices[col].add(intData[pointer++], row);
            }
        }
    }


    public void buildIndices(int... initialCapacities) {

        indices = new ArrayIndex[dimensions.length];

        for (int column = 0; column < dimensions.length; ++column)
            indices[column] = new ArrayIndex(dimensions[column]);

        //iterate over data
        for (int row = 0; row < size; ++row) {
            //go over each column
            for (int column = 0; column < dimensions.length; ++column) {
                int value = intData[row * dimensions.length + column];
                if (indices[column].rows[value] == null) {
                    indices[column].rows[value] = new int[initialCapacities[column]];
                    indices[column].rows[value][0] = row;
                    indices[column].counts[value] = 1;
                } else {
                    int[] rowsForValue = indices[column].rows[value];
                    if (rowsForValue.length == indices[column].counts[value]) {
                        int[] newRows = new int[rowsForValue.length + initialCapacities[column]];
                        System.arraycopy(rowsForValue, 0, newRows, 0, rowsForValue.length);
                        newRows[indices[column].counts[value]++] = row;
                        indices[column].rows[value] = newRows;
                    } else {
                        rowsForValue[indices[column].counts[value]++] = row;
                    }
                }
            }
        }

    }

    public int getColumnCount() {
        return dimensions.length;
    }

    public void printTo(PrintStream out) {
        StringBuilder formatBuilder = new StringBuilder();
        for (int i = 0; i < dimensions.length; ++i)
            formatBuilder.append("%7d");
        if (hasDoubles) formatBuilder.append("%7.2f");
        formatBuilder.append("\n");
        String format = formatBuilder.toString();
        for (int rowNumber = 0; rowNumber < size; ++rowNumber) {
            if (hasDoubles) {
                Object[] row = new Object[dimensions.length + 1];
                for (int column = 0; column < row.length; ++column) {
                    row[column] = intData[rowNumber * dimensions.length + column];
                }
                row[dimensions.length] = doubleData[rowNumber];
                out.printf(format, (Object[]) row);

            } else {
                Integer[] row = new Integer[dimensions.length];
                for (int column = 0; column < row.length; ++column) {
                    row[column] = intData[rowNumber * dimensions.length + column];
                }
                out.printf(format, (Object[]) row);
            }
        }
    }

    public void setCurrentPointer(int pointer) {
        currentIntData = pointer;
    }

    public double sumOfWeights() {
        double result = 0;
        for (int row = 0; row < size; ++row)
            result += doubleData[row];
        return result;
    }

    public int[] getPrimaryKeys() {
        return primaryKeys;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public void fillWeights(double value) {
        Arrays.fill(doubleData, 0, size, value);
    }

    public static DoubleIntTable createByPrototype(int initialCapacity, DoubleIntTable table) {
        return new DoubleIntTable(initialCapacity, table.getSequentialColumn(), table.hasDoubles, table.getDimensions());
    }

    public void clear() {
        currentIntData = 0;
        size = 0;
        primaryKey2Row = null;

    }



    public void buildPrimaryKeyIndex() {
        primaryKey2Row = new Int2IntHashtable(size, 4);
        int pointer = 0;
        for (int row = 0; row < size; ++row, pointer += columnCount) {
            int key = 17;
            for (int i : primaryKeys) {
                key = 37 * key + intData[pointer + i];
            }
            key = key > 0 ? key : -key;
            primaryKey2Row.put(key, row);
        }
    }

    public void buildNoClashPrimaryKeyIndex() {
        noClashPrimaryKey2Row = new IntTuple2IntHashtable(primaryKeys.length,size, 2);
        int pointer = 0;
        for (int row = 0; row < size; ++row, pointer += columnCount) {
            noClashPrimaryKey2Row.put(row, intData, pointer, primaryKeys);
        }
    }


    public IntTuple2IntHashtable getNoClashPrimaryKeyIndex() {
        return noClashPrimaryKey2Row;
    }

    public Int2IntHashtable getPrimaryKeyIndex() {
        return primaryKey2Row;
    }


    public void fillWith(DoubleIntTable table) {
        int diff = intData.length - table.intData.length;
        if (diff < 0) {
            intData = new int[table.intData.length];
            if (table.hasDoubles) {
                doubleData = new double[table.doubleData.length];
            }
        }
        System.arraycopy(table.intData, 0, intData, 0, table.intData.length);
        System.arraycopy(table.doubleData, 0, doubleData, 0, table.doubleData.length);
        size = table.size;
    }

    public double getWeightByRowData(int... row) {
        if (primaryKey2Row == null)
            buildPrimaryKeyIndex();
        int rowIndex = primaryKey2Row.getByPrimaryKey(row, 0, primaryKeys);
        return doubleData[rowIndex];
    }


    public int numberCorrectIn(DoubleIntTable other) {
        if (size == 0) return 0;
        if (primaryKey2Row == null)
            buildPrimaryKeyIndex();
        int[] otherData = other.getIntData();
        int otherSize = other.getSize();
        int correct = 0;
        for (int row = 0; row < otherSize; ++row) {
            if (primaryKey2Row.getByPrimaryKey(otherData, row * columnCount, primaryKeys) != -1)
                ++correct;
        }
        return correct;
    }

    public double precision(DoubleIntTable guess) {
        if (guess.getSize() == 0) return 0;
        return (double) numberCorrectIn(guess) / (double) guess.getSize();
    }

    public double recall(DoubleIntTable guess) {
        if (size == 0) return 0;
        return (double) guess.numberCorrectIn(this) / (double) size;
    }

    public void buildMultiColumnIndex() {
        if (multiIndexColumns != null) {
            multiColumnIndex = new Int2IntMultiHashtable(size, 2, 2);
            int dataPointer = 0;
            //TODO: there is a potential for hashcode clashes, if happens, cancel this multicolumn index
            //and let search handle it with other means (eg, light indices)
            for (int row = 0; row < size; ++row) {
                int key = 17;
                for (int i : multiIndexColumns) {
                    key = 37 * key + intData[dataPointer + i];
                    multiColumnIndex.add(key, row);
                }
                dataPointer += columnCount;
            }

        }
    }


    public void compactify() {
        if (size * columnCount < intData.length) {
            int[] newData = new int[size * columnCount];
            System.arraycopy(intData, 0, newData, 0, newData.length);
            intData = newData;
            if (hasDoubles) {
                double[] newWeights = new double[size];
                System.arraycopy(doubleData, 0, newWeights, 0, newWeights.length);
                doubleData = newWeights;
            }

        }
    }

    public void compactifyIndices() {
        if (indices != null)
            for (int i = 0; i < columnCount; ++i) {
                if (indices[i] != null) indices[i].compactify();
                if (lightIndices[i] != null) lightIndices[i].compactify();

            }
        if (primaryKey2Row != null)
            primaryKey2Row.compactify();

    }

    public int[] getRow(int row) {
        int[] result = new int[columnCount];
        System.arraycopy(intData, row * columnCount, result, 0, columnCount);
        return result;
    }

    public void add(DoubleIntTable table, double scale) {
        boolean wasEmpty = size == 0;
        append(table, scale);
        if (!wasEmpty) {
            RowUniquifier uniquifier = new RowUniquifier();
            setCurrentPointer(0);
            //uniquifier.uniquify(this, this);
            close();
        }
    }

    public void addByOrder(DoubleIntTable table, double scale){
        double[] otherWeights = table.getDoubleData();
        for (int row = 0; row < size; ++row)
            doubleData[row] += scale * otherWeights[row];
    }

    public void append(DoubleIntTable table, double scale) {
        int[] otherData = table.getIntData();
        int otherSize = table.getSize();
        assert table.getColumnCount() == columnCount;
        int pointer = size * columnCount;
        int otherLength = otherSize * columnCount;
        if ((otherSize + size) * columnCount < intData.length) {
            System.arraycopy(otherData, 0, intData, pointer, otherLength);
            if (hasDoubles) {
                if (scale == 1.0) System.arraycopy(table.getDoubleData(), 0, doubleData, size, otherSize);
                else {
                    double[] otherWeights = table.getDoubleData();
                    for (int i = 0; i < otherSize; ++i) doubleData[size + i] = scale * otherWeights[i];
                }
            }
        } else {
            int[] newdata = new int[(otherSize + size) * columnCount];
            System.arraycopy(intData, 0, newdata, 0, size * columnCount);
            System.arraycopy(otherData, 0, newdata, pointer, otherLength);
            intData = newdata;
            if (hasDoubles) {
                double[] newWeights = new double[otherSize + size];
                System.arraycopy(doubleData, 0, newWeights, 0, size);
                if (scale == 1.0) System.arraycopy(table.getDoubleData(), 0, newWeights, size, otherSize);
                else {
                    double[] otherWeights = table.getDoubleData();
                    for (int i = 0; i < otherSize; ++i) newWeights[size + i] = scale * otherWeights[i];
                }
                doubleData = newWeights;
            }
        }
        invalidateIndices();
        size += otherSize;

    }

    private void invalidateIndices() {
        primaryKey2Row = null;
        lightIndices = null;
        multiColumnIndex = null;
    }


}
