package thebeast.util;

/**
 * @author Sebastian Riedel
 */
public final class ArrayIndex {

    public int[][] rows;
    public int[] counts;

    public ArrayIndex(int dimension) {
        this.rows = new int[dimension][];
        this.counts = new int[dimension];
    }

    public void compactify(){
        for (int i = 0; i < counts.length;++i){
            int count = counts[i];
            int[] row = rows[i];
            if (row != null && row.length < count){
                int[] newRow = new int[count];
                System.arraycopy(row,0,newRow,0,count);
                rows[i] = newRow;
            }
        }
    }


}
