package thebeast.nodmem.mem;

import thebeast.util.Int2IntMultiHashtable;

/**
 * @author Sebastian Riedel
 */
public class MemIntColumnIndex {

    private Int2IntMultiHashtable hashtable;
    private int columnPointer;
    private MemChunk chunk;

    public MemIntColumnIndex(MemChunk chunk, int columnPointer) {
        this.chunk = chunk;
        this.columnPointer = columnPointer;
        //rebuild();
    }

//    private void rebuild() {
//        hashtable = new Int2IntMultiHashtable(this.chunk.getSize(),1,1);
//        int size = chunk.getSize();
//        //int cols = chunk.getNumCols();
//        int[] data = chunk.getIntData();
//        for (int row = 0; row < size; ++row){
//            int pointer = row * cols + columnPointer;
//            hashtable.add(data[pointer],row);
//        }
//    }

    public Int2IntMultiHashtable getHashtable() {
        return hashtable;
    }
}
