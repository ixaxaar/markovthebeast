package thebeast.nodmem.mem;

import junit.framework.TestCase;

/**
 * @author Sebastian Riedel
 */
public class TestMemChunkMultiIndex extends TestCase {
  protected MemChunk data;
  protected MemChunkMultiIndex index;
  protected MemDim dim;
  protected MemColumnSelector cols;


  protected void setUp() {
    dim = new MemDim(3, 2, 0);
    data = new MemChunk(3, 3, dim);
    data.intData = new int[]{1, 2, 3, 2, 3, 4, 3, 4, 5};
    data.doubleData = new double[]{1.0, 2.0, 2.0, 3.0, 3.0, 4.0};
    cols = new MemColumnSelector(new int[]{0, 2}, new int[]{0}, new int[0]);
    index = new MemChunkMultiIndex(2, new MemDim(2,1,0));

  }

  public void testAdd() {
    assertEquals(-1, index.add(data, new MemVector(0, dim),cols, 10));
    assertEquals(-1, index.add(data, new MemVector(1, dim),cols, 20));
    assertEquals(-1, index.add(data, new MemVector(2, dim),cols, 30));
    assertEquals(40, index.add(data, new MemVector(2, dim),cols, 40));
    assertEquals(50, index.add(data, new MemVector(2, dim),cols, 50));
    assertEquals(60, index.add(data, new MemVector(2, dim),cols, 60));

    int[][] holder = new int[10][];
    assertEquals(1, index.get(data, new MemVector(0, dim),cols,0,holder));
    assertEquals(10, holder[0][0]);
    assertEquals(1, index.get(data, new MemVector(1, dim),cols,0,holder));
    assertEquals(20, holder[0][0]);
    assertEquals(4, index.get(data, new MemVector(2, dim),cols,0,holder));
    assertEquals(30, holder[0][0]);
    assertEquals(40, holder[0][1]);
    assertEquals(50, holder[0][2]);
    assertEquals(60, holder[0][3]);

    assertEquals(3, index.getNumKeys());
    assertEquals(2, index.getNumUsedIndices());
  }

  public void testIncreaseCapacity(){
    assertEquals(-1, index.add(data, new MemVector(0, dim),cols, 10));
    assertEquals(-1, index.add(data, new MemVector(1, dim),cols, 20));
    assertEquals(-1, index.add(data, new MemVector(2, dim),cols, 30));
    assertEquals(40, index.add(data, new MemVector(2, dim),cols, 40));
    assertEquals(50, index.add(data, new MemVector(2, dim),cols, 50));
    assertEquals(60, index.add(data, new MemVector(2, dim),cols, 60));

    index.increaseCapacity(10);

    int[][] holder = new int[10][];
    assertEquals(1, index.get(data, new MemVector(0, dim),cols,0,holder));
    assertEquals(10, holder[0][0]);
    assertEquals(1, index.get(data, new MemVector(1, dim),cols,0,holder));
    assertEquals(20, holder[0][0]);
    assertEquals(4, index.get(data, new MemVector(2, dim),cols,0,holder));
    assertEquals(30, holder[0][0]);
    assertEquals(40, holder[0][1]);
    assertEquals(50, holder[0][2]);
    assertEquals(60, holder[0][3]);

    assertEquals(3, index.getNumKeys());
    assertEquals(3, index.getNumUsedIndices());    
  }



}
