package thebeast.nodmem.mem;

import junit.framework.TestCase;

/**
 * @author Sebastian Riedel
 */
public class TestGrouper extends TestCase {

  public void testGroup(){
    MemChunk src = new MemChunk(3, new int[]{
            1,1,1,
            1,2,1,
            2,2,2
    }, new double[0], new MemChunk[0]);

    MemChunk dst = new MemChunk(0,1,1,0,1);
    MemColumnSelector keyCols = new MemColumnSelector(1,0,0);
    MemColumnSelector groupCols = new MemColumnSelector(new int[]{1,2},new int[0], new int[0]);
    MemColumnSelector dstCols = new MemColumnSelector(1,0,0);
    MemGrouper.group(src, keyCols, dstCols, groupCols, 0, dst);
    assertEquals(1, dst.intData[0]);
    assertEquals(2, dst.chunkData[0].size);
    assertEquals(1, dst.chunkData[0].intData[0]);
    assertEquals(1, dst.chunkData[0].intData[1]);
    assertEquals(2, dst.chunkData[0].intData[2]);
    assertEquals(1, dst.chunkData[0].intData[3]);
    assertEquals(2, dst.chunkData[1].intData[0]);
    assertEquals(2, dst.chunkData[1].intData[1]);
    assertEquals(2, dst.intData[1]);

  }
}
