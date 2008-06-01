package thebeast.nodmem.mem;

import junit.framework.TestCase;

/**
 * @author Sebastian Riedel
 */
public class TestMemChunkIndex extends TestCase {
  protected MemChunk data;
  protected MemChunkIndex index;
  protected MemDim dim;
  protected MemColumnSelector cols;


  protected void setUp() {
    dim = MemDim.create(3, 2, 0);
    data = new MemChunk(3, 3, dim);
    data.intData = new int[]{1, 2, 3, 2, 3, 4, 3, 4, 5};
    data.doubleData = new double[]{1.0, 2.0, 2.0, 3.0, 3.0, 4.0};
    cols = new MemColumnSelector(new int[]{0, 2}, new int[]{0}, new int[0]);
    index = new MemChunkIndex(2, MemDim.create(2, 1, 0));
  }

  public void testPut() {
    assertEquals(-1, index.put(data, new MemVector(0, dim), cols, 10, true));
    assertEquals(-1, index.put(data, new MemVector(1, dim), cols, 20, true));
    assertEquals(-1, index.put(data, new MemVector(2, dim), cols, 30, true));
    assertEquals(30, index.put(data, new MemVector(2, dim), cols, 40, true));

    assertEquals(10, index.get(data, new MemVector(0, dim), cols));
    assertEquals(20, index.get(data, new MemVector(1, dim), cols));
    assertEquals(40, index.get(data, new MemVector(2, dim), cols));

    assertEquals(3, index.getNumKeys());
    assertEquals(2, index.getNumUsedIndices());
  }

  public void testIncreaseCapacity() {
    index.put(data, new MemVector(0, dim), cols, 10, true);
    index.put(data, new MemVector(1, dim), cols, 20, true);
    index.put(data, new MemVector(2, dim), cols, 30, true);

    index.increaseCapacity(10);
    assertEquals(10, index.get(data, new MemVector(0, dim), cols));
    assertEquals(20, index.get(data, new MemVector(1, dim), cols));
    assertEquals(30, index.get(data, new MemVector(2, dim), cols));
    assertEquals(3, index.getNumUsedIndices());
  }

  public void testClear() {
    index.put(data, new MemVector(0, dim), cols, 10, true);
    index.put(data, new MemVector(1, dim), cols, 20, true);
    index.put(data, new MemVector(2, dim), cols, 30, true);

    index.clear();
    assertEquals(-1, index.get(data, new MemVector(0, dim), cols));
    assertEquals(-1, index.get(data, new MemVector(1, dim), cols));
    assertEquals(-1, index.get(data, new MemVector(2, dim), cols));
  }


}
