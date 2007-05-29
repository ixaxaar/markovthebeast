package thebeast.nodmem.mem;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 14-Jan-2007 Time: 16:15:50
 */
public class TestMemSearch extends TestCase {
  protected MemChunk table1;
  protected MemChunk table2;
  protected MemPointer attr1;
  protected MemFunction attr1Select;
  protected MemSearchAction equalSelectAttr1;
  protected MemSearchAction equalPlus1SelectAttr1;
  protected MemFunction equalAttr1Attr2;

  protected void setUp() {
    //let's build two tables
    table1 = new MemChunk(3, new int[]{1, 2, 3}, new double[0], new MemChunk[0]);
    table2 = new MemChunk(3, new int[]{3, 4, 2}, new double[0], new MemChunk[0]);

    //let's also build an index of table2
    MemDim dim = MemDim.INT_DIM;
    MemChunkMultiIndex index1 = new MemChunkMultiIndex(3, dim);
    MemColumnSelector cols = new MemColumnSelector(new int[]{0}, new int[0], new int[0]);
    index1.add(table2, new MemVector(0, dim), cols, 0);
    index1.add(table2, new MemVector(1, dim), cols, 1);
    index1.add(table2, new MemVector(2, dim), cols, 2);

    table2.indices = new MemChunkMultiIndex[]{index1};

    attr1 = new MemPointer(MemChunk.DataType.INT, 0);
    attr1Select = new MemFunction(new MemChunk(1, 1, MemDim.INT_DIM),
            new MemVector[]{new MemVector(0,0,0)},
            new MemFunction(0, attr1));

    equalAttr1Attr2 = new MemFunction(MemFunction.Type.INT_EQUAL,
            new MemFunction(0, attr1),
            new MemFunction(1, attr1));

    equalSelectAttr1 = new MemSearchAction(MemSearchAction.Type.VALIDATE_WRITE, equalAttr1Attr2, attr1Select);

    equalPlus1SelectAttr1 = new MemSearchAction(MemSearchAction.Type.VALIDATE_WRITE,
            new MemFunction(MemFunction.Type.INT_EQUAL,
                    new MemFunction(MemFunction.Type.INT_ADD, new MemFunction(0, attr1), new MemFunction(1)),
                    new MemFunction(1, attr1)),
            attr1Select);

    //MemChunkMultiIndex index = new MemChunkMultiIndex(10,new MemColumnSelector());
  }

  public void testAllAction() {

    MemSearchAction allTable1 = new MemSearchAction(MemSearchAction.Type.ALL);
    MemSearchAction allTable2 = new MemSearchAction(MemSearchAction.Type.ALL);

    MemSearchPlan plan1 = new MemSearchPlan(MemDim.INT_DIM,allTable1, allTable2, equalSelectAttr1);

    MemChunk dst = new MemChunk(4, 4, MemDim.INT_DIM);

    MemSearch.search(plan1, new MemChunk[]{table1, table2}, null, dst, 0);

    assertEquals(2, dst.intData[0]);
    assertEquals(3, dst.intData[1]);
    assertEquals(2, dst.getSize());


    MemSearchPlan plan2 = new MemSearchPlan(MemDim.INT_DIM,allTable1, allTable2, equalPlus1SelectAttr1);

    dst = new MemChunk(4, 4, MemDim.INT_DIM);

    MemSearch.search(plan2, new MemChunk[]{table1, table2}, null, dst, 0);

    assertEquals(1, dst.intData[0]);
    assertEquals(2, dst.intData[1]);
    assertEquals(3, dst.intData[2]);
    assertEquals(3, dst.getSize());

    System.out.println(Arrays.toString(dst.intData));

    MemFunction query = new MemFunction(plan1,
            new MemFunction(new MemVector[]{new MemVector(0,0,0), new MemVector(0,0,1)},
                    new MemChunk(1,1,MemDim.CHUNK2_DIM),
                    new MemFunction(table1), new MemFunction(table2)
                    ), null);

    dst = new MemChunk(4, 4, MemDim.INT_DIM);
    MemChunk dstHolder = new MemChunk(1,1,MemDim.CHUNK_DIM);
    dstHolder.chunkData[0] = dst;

    MemEvaluator.evaluate(query,null,null,dstHolder,new MemVector(0,0,0));
    System.out.println(Arrays.toString(dst.intData));

    assertEquals(2, dst.intData[0]);
    assertEquals(3, dst.intData[1]);
    assertEquals(2, dst.getSize());
    
  }

  public void testIndexAction() {

    MemSearchAction allTable1 = new MemSearchAction(MemSearchAction.Type.ALL);
    MemSearchAction indexTable2 = new MemSearchAction(MemSearchAction.Type.MULTI_INDEX,
            0, new MemColumnSelector(1,0,0), attr1Select);

    MemSearchPlan plan1 = new MemSearchPlan(MemDim.INT_DIM, allTable1, indexTable2,
            new MemSearchAction(MemSearchAction.Type.WRITE, attr1Select));

    MemChunk dst = new MemChunk(4, 4, MemDim.INT_DIM);

    MemSearch.search(plan1, new MemChunk[]{table1, table2}, null, dst, 0);

    System.out.println(Arrays.toString(dst.intData));  

    assertEquals(2, dst.intData[0]);
    assertEquals(3, dst.intData[1]);
    assertEquals(2, dst.getSize());


//    MemSearchPlan plan2 = new MemSearchPlan(allTable1, indexTable2, equalPlus1SelectAttr1);
//
//    MemQueryResolver.resolve(plan2, new MemChunk[]{table1, table2}, dst, 0);
//
//    assertEquals(1, dst.intData[0]);
//    assertEquals(2, dst.intData[1]);
//    assertEquals(3, dst.intData[2]);
//    assertEquals(3, dst.getSize());
//
//

  }


}
