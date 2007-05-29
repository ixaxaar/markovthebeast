package thebeast.nodmem.mem;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class TestMemEvaluator extends TestCase {
  protected MemFunction attr1;
  protected MemFunction attr2;
  protected MemFunction int1;
  protected MemFunction int2;
  protected MemFunction tuple121;

  protected void setUp() {
    attr1 = new MemFunction(0, new MemPointer(MemChunk.DataType.INT, 0));
    attr2 = new MemFunction(1, new MemPointer(MemChunk.DataType.INT, 1));

    int1 = new MemFunction(1);
    int2 = new MemFunction(2);

    MemChunk argHolder = new MemChunk(1,1,MemDim.create(3,0,0));
    MemVector[] pointers = new MemVector[]{new MemVector(0,0,0), new MemVector(1,0,0), new MemVector(2,0,0)};
    tuple121 = new MemFunction(MemFunction.Type.TUPLE_SELECTOR,argHolder,pointers, int1,int2,int1);

  }

  public void testAdd() {

    MemFunction add = new MemFunction(MemFunction.Type.INT_ADD, attr1, attr2);

    MemChunk chunk1 = new MemChunk(1, 1, MemDim.INT_DIM);
    MemChunk chunk2 = new MemChunk(1, 1,  MemDim.INT2_DIM);

    chunk1.intData[0] = 2;
    chunk2.intData[1] = 3;

    MemChunk dst = new MemChunk(1, 1, MemDim.INT_DIM);

    MemEvaluator.evaluate(add, new MemChunk[]{chunk1, chunk2}, new int[]{0, 0}, dst, new MemVector(0, 0, 0));

    assertEquals(5, dst.intData[0]);

    MemFunction addAgain = new MemFunction(MemFunction.Type.INT_ADD, add, add);

    MemEvaluator.evaluate(addAgain, new MemChunk[]{chunk1, chunk2}, new int[]{0, 0}, dst, new MemVector(0, 0, 0));

    assertEquals(10, dst.intData[0]);
  }

  public void testTupleSelector() {
    MemChunk dst = new MemChunk(1,1,MemDim.CHUNK_DIM);
    MemEvaluator.evaluate(tuple121,null,null,dst,new MemVector(0,0,0));
    System.out.println(Arrays.toString(dst.chunkData[0].intData));
    assertEquals(1,dst.chunkData[0].intData[0]);
    assertEquals(2,dst.chunkData[0].intData[1]);
    assertEquals(1,dst.chunkData[0].intData[2]);
  }

  public void testRelationSelector(){
    MemChunk dst = new MemChunk(1,1,MemDim.CHUNK_DIM);
    MemChunk argHolder = new MemChunk(1,1,MemDim.create(0,0,3));
    MemVector[] argPointers = new MemVector[]{new MemVector(0,0,0), new MemVector(0,0,1), new MemVector(0,0,2)};
    MemFunction relation = new MemFunction(MemFunction.Type.RELATION_SELECTOR,argHolder,argPointers,
            tuple121,tuple121,tuple121);
    MemEvaluator.evaluate(relation,null,null,dst,new MemVector(0,0,0));
    System.out.println(Arrays.toString(dst.chunkData[0].intData));
    assertEquals(1,dst.chunkData[0].intData[0]);
    assertEquals(2,dst.chunkData[0].intData[1]);
    assertEquals(1,dst.chunkData[0].intData[2]);
    assertEquals(1,dst.chunkData[0].intData[3]);
    assertEquals(2,dst.chunkData[0].intData[4]);
    assertEquals(1,dst.chunkData[0].intData[5]);

  }

}
