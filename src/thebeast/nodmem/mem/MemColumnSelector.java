package thebeast.nodmem.mem;

import java.util.LinkedList;
import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class MemColumnSelector {

  public int[] intCols;
  public int[] doubleCols;
  public int[] chunkCols;


  public MemColumnSelector(int[] intCols, int[] doubleCols, int[] chunkCols) {
    this.intCols = intCols;
    this.doubleCols = doubleCols;
    this.chunkCols = chunkCols;
  }

  public MemColumnSelector(int numIntCols, int numDoubleCols, int numChunkCols) {
    intCols = new int[numIntCols];
    for (int i = 0; i < intCols.length; ++i) intCols[i] = i;
    doubleCols = new int[numDoubleCols];
    for (int i = 0; i < doubleCols.length; ++i) doubleCols[i] = i;
    chunkCols = new int[numChunkCols];
    for (int i = 0; i < chunkCols.length; ++i) chunkCols[i] = i;
  }

  public MemColumnSelector(MemDim dim){
    this(dim.xInt,dim.xDouble, dim.xChunk);
  }

  public MemColumnSelector(MemColumnSelector cols) {
    this(cols.intCols.length, cols.doubleCols.length, cols.chunkCols.length);
  }

  public MemColumnSelector(MemPointer ... pointers){
    ArrayList<Integer> intCols = new ArrayList<Integer>();
    ArrayList<Integer> doubleCols = new ArrayList<Integer>();
    ArrayList<Integer> chunkCols = new ArrayList<Integer>();
    for (MemPointer pointer : pointers){
      switch(pointer.type){
        case INT: intCols.add(pointer.pointer); break;
        case DOUBLE: doubleCols.add(pointer.pointer); break;
        case CHUNK: chunkCols.add(pointer.pointer); break;
      }
    }
    this.intCols = new int[intCols.size()];
    this.doubleCols = new int[doubleCols.size()];
    this.chunkCols = new int[chunkCols.size()];
    for (int i = 0; i < this.intCols.length;++i)
      this.intCols[i] = intCols.get(i);
    for (int i = 0; i < this.doubleCols.length;++i)
      this.doubleCols[i] = doubleCols.get(i);
    for (int i = 0; i < this.chunkCols.length;++i)
      this.doubleCols[i] = doubleCols.get(i);

  }

  public MemDim getDim(){
    return new MemDim(intCols.length,doubleCols.length,chunkCols.length);
  }
}
