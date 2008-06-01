package thebeast.util;

import junit.framework.TestCase;

/**
 * @author Sebastian Riedel
 */
public class TestIntTuple2IntHashtable extends TestCase {

    public void testPutGet() {
        IntTuple2IntHashtable table = new IntTuple2IntHashtable(2, 10, 10);
        table.put(20, 1, 1);
        table.put(10, 1, 0);
        table.put(30, 2, 2);
        assertEquals(3, table.getKeyCount());
        assertEquals(20, table.get(1, 1));
        assertEquals(10, table.get(1, 0));
        assertEquals(30, table.get(2, 2));

    }

    public void testPutGetWithOffset() {
        IntTuple2IntHashtable table = new IntTuple2IntHashtable(2, 10, 10);
        int[] data = new int[]{
                1,5,1,
                1,5,0,
                2,5,2
        };
        table.put(20, data, 0, 0, 2);
        table.put(10, data, 3, 0, 2);
        table.put(30, data, 6, 0, 2);
        assertEquals(3, table.getKeyCount());
        assertEquals(20, table.get(1, 1));
        assertEquals(10, table.get(1, 0));
        assertEquals(30, table.get(2, 2));
        assertEquals(20, table.get(data, 0, 0, 2));
        assertEquals(10, table.get(data, 3, 0, 2));
        assertEquals(30, table.get(data, 6, 0, 2));

    }

}
