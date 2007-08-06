package thebeast.nodmem.mem;

import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class MemDim extends MemVector {

  private static HashMap<MemDim, MemDim> cache = new HashMap<MemDim, MemDim>();

  public final MemColumnSelector allCols;

  public static final MemDim EMPTY = new MemDim(0, 0, 0);
  public static final MemDim INT_DIM = new MemDim(1, 0, 0);
  public static final MemDim INT2_DIM = new MemDim(2, 0, 0);
  public static final MemDim INT_CHUNK_DIM = new MemDim(1, 0, 1);
  public static final MemDim DOUBLE_DIM = new MemDim(0, 1, 0);
  public static final MemDim DOUBLE2_DIM = new MemDim(0, 2, 0);
  public static final MemDim CHUNK_DIM = new MemDim(0, 0, 1);
  public static final MemDim CHUNK2_DIM = new MemDim(0, 0, 2);
  public static final MemDim DOUBLE_CHUNK_DIM = new MemDim(0, 1, 1);
  public static final MemDim DOUBLE_CHUNK2_DIM = new MemDim(0, 1, 2);


  private MemDim(int xInt, int xDouble, int xChunk) {
    super(xInt, xDouble, xChunk);
    allCols = new MemColumnSelector(this);
  }

  public static MemDim create(int xInt, int xDouble, int xChunk) {
    MemDim dim = new MemDim(xInt, xDouble, xChunk);
    MemDim old = cache.get(dim);
    if (old != null) return old;
    cache.put(dim, dim);
    return dim;
  }

  public static MemDim create(MemVector vector) {
    return create(vector.xInt, vector.xDouble, vector.xChunk);
  }



}
