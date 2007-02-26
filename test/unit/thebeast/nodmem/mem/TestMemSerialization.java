package thebeast.nodmem.mem;

import junit.framework.TestCase;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class TestMemSerialization extends TestCase {

  public void testMemSerialization() throws IOException {
    testMemSerialization(8);
  }

  public void testMemSerializationLarge() throws IOException {
    testMemSerialization(1024 * 1024);
  }

  public void testMemSerializationMedium() throws IOException {
    testMemSerialization(12 * 4);
  }

  public void testMemSerialization(int bufferSize) throws IOException {
    File file = new File("delete-me");
    file.delete();
    ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
    MemSerializer serializer = new MemSerializer(new FileOutputStream(file, true).getChannel(), buffer);
    int[] expectedInts = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    double[] expectedDoubles = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
    serializer.writeInts(expectedInts, expectedInts.length);
    serializer.writeDoubles(expectedDoubles, expectedDoubles.length);
    serializer.writeInts(expectedInts, expectedInts.length);
    serializer.finish();
    MemDeserializer deserializer = new MemDeserializer(new FileInputStream(file).getChannel(), buffer);
    int[] ints1 = new int[expectedInts.length];
    int[] ints2 = new int[expectedInts.length];
    double[] doubles = new double[expectedDoubles.length];
    deserializer.read(ints1, ints1.length);
    deserializer.read(doubles, doubles.length);
    deserializer.read(ints2, ints2.length);
    file.delete();
    System.out.println(Arrays.toString(ints1));
    System.out.println(Arrays.toString(doubles));
    System.out.println(Arrays.toString(ints2));
    assertTrue(Arrays.equals(expectedInts, ints1));
    assertTrue(Arrays.equals(expectedDoubles, doubles));
    assertTrue(Arrays.equals(expectedInts, ints2));
  }

  public void testSerializeMemChunkIndex() throws IOException {
    ByteBuffer buffer = ByteBuffer.allocateDirect(10 * 1024);
    File file = new File("delete-me");
    file.delete();
    MemSerializer serializer = new MemSerializer(new FileOutputStream(file, true).getChannel(), buffer);
    MemChunkIndex index = new MemChunkIndex(10, new MemDim(1, 1, 1));
    index.put(new int[]{1}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}, 1, true);
    index.put(new int[]{2}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}, 2, true);
    index.put(new int[]{3}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}, 3, true);
    index.put(new int[]{4}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}, 4, true);

    assertEquals(1, index.get(new int[]{1}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}));

    MemChunkIndex.serialize(index, serializer);
    serializer.finish();
    MemDeserializer deserializer = new MemDeserializer(new FileInputStream(file).getChannel(), buffer);
    MemChunkIndex loaded = MemChunkIndex.deserialize(deserializer);
    assertEquals(index.getCapacity(), loaded.getCapacity());
    assertEquals(index.getNumKeys(), loaded.getNumKeys());
    assertEquals(index.getNumUsedIndices(), loaded.getNumUsedIndices());

    assertEquals(1, loaded.get(new int[]{1}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}));
    assertEquals(2, loaded.get(new int[]{2}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}));
    assertEquals(3, loaded.get(new int[]{3}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}));
    assertEquals(4, loaded.get(new int[]{4}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}));


    file.delete();

  }

  public void testSerializeMemChunkMutliIndex() throws IOException {
    ByteBuffer buffer = ByteBuffer.allocateDirect(10 * 1024);
    File file = new File("delete-me");
    file.delete();
    MemSerializer serializer = new MemSerializer(new FileOutputStream(file, true).getChannel(), buffer);
    MemChunkMultiIndex index = new MemChunkMultiIndex(10, new MemDim(1, 1, 1));
    index.add(new int[]{1}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}, 1);
    index.add(new int[]{1}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}, 2);
    index.add(new int[]{3}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}, 3);
    index.add(new int[]{3}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])}, 4);


    int[][] list = new int[1][10];
    assertEquals(2, index.get(new int[]{1}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])},0,list));
    assertEquals(1, list[0][0]);
    assertEquals(2, list[0][1]);

    MemChunkMultiIndex.serialize(index, serializer);
    serializer.finish();
    MemDeserializer deserializer = new MemDeserializer(new FileInputStream(file).getChannel(), buffer);
    MemChunkMultiIndex loaded = MemChunkMultiIndex.deserialize(deserializer);
    assertEquals(index.getCapacity(), loaded.getCapacity());
    assertEquals(index.getNumKeys(), loaded.getNumKeys());
    assertEquals(index.getNumUsedIndices(), loaded.getNumUsedIndices());
    assertEquals(2, loaded.get(new int[]{1}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])},0,list));
    assertEquals(1, list[0][0]);
    assertEquals(2, list[0][1]);
    assertEquals(2, loaded.get(new int[]{3}, new double[]{1.0},
            new MemChunk[]{new MemChunk(1, new int[]{1}, new double[]{1.0}, new MemChunk[0])},0,list));
    assertEquals(3, list[0][0]);
    assertEquals(4, list[0][1]);


    file.delete();

  }


}
