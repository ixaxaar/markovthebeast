package thebeast.util;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class TestInt2IntMultiHashtable extends TestCase {
    public void testInt2IntMultiHashtable() {
        Int2IntMultiHashtable table = new Int2IntMultiHashtable(10, 2, 2);
        table.add(5, 1);
        table.add(5, 2);
        table.add(5, 20);
        table.add(4, 3);
        table.add(15, 10);
        table.add(25, 100);
        int[][] result = new int[1][];
        int[] resultSize = new int[1];
        table.getList(5, 0, resultSize, result);
        System.out.println(Arrays.toString(result[0]));
        System.out.println(Arrays.toString(resultSize));
        assertEquals(3, resultSize[0]);
        assertEquals(1, result[0][0]);
        assertEquals(2, result[0][1]);
        assertEquals(20, result[0][2]);
        table.getList(15, 0, resultSize, result);
        System.out.println(Arrays.toString(result[0]));
        System.out.println(Arrays.toString(resultSize));
        assertEquals(1, resultSize[0]);
        assertEquals(10, result[0][0]);
        table.getList(25, 0, resultSize, result);
        System.out.println(Arrays.toString(result[0]));
        System.out.println(Arrays.toString(resultSize));
        assertEquals(1, resultSize[0]);
        assertEquals(100, result[0][0]);

    }

}
