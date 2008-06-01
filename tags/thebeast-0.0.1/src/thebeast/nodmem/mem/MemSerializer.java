package thebeast.nodmem.mem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * @author Sebastian Riedel
 */
public class MemSerializer {

  private WritableByteChannel channel;
  private ByteBuffer byteBuffer;
  private IntBuffer intBuffer;
  private DoubleBuffer doubleBuffer;

  public MemSerializer(WritableByteChannel channel, ByteBuffer byteBuffer) {
    if (byteBuffer.capacity() % MemHolder.DOUBLESIZE != 0)
      throw new IllegalArgumentException("buffer capacity must be dividable by 8");
    this.channel = channel;
    this.byteBuffer = byteBuffer;
    this.intBuffer = byteBuffer.asIntBuffer();
    this.doubleBuffer = byteBuffer.asDoubleBuffer();
  }


  public void writeInts(int... data) throws IOException {
    writeInts(data, data.length);
  }

  public void finish() throws IOException {
    flush();
    channel.close();
  }

  public void flush() throws IOException {
    byteBuffer.position(0);
    channel.write(byteBuffer);
    clearBuffers();
  }

  public void clearBuffers() {
    byteBuffer.clear();
    intBuffer.clear();
    doubleBuffer.clear();
  }

  public void writeInts(int[] data, int howmany) throws IOException {
    //fill up the buffer
    intBuffer.position(byteBuffer.position() / MemHolder.INTSIZE);
    int bufferSize = intBuffer.limit();
    int spaceLeft = intBuffer.remaining();
    intBuffer.put(data, 0, spaceLeft >= howmany ? howmany : spaceLeft);
    if (spaceLeft < howmany) {
      byteBuffer.position(0);
      channel.write(byteBuffer);
      intBuffer.position(0);
      byteBuffer.position(0);
      int position = spaceLeft;
      while (position < howmany - bufferSize) {
        intBuffer.put(data, position, bufferSize);
        byteBuffer.position(0);        
        channel.write(byteBuffer);
        intBuffer.position(0);
        byteBuffer.position(0);
        position += bufferSize;
      }
      intBuffer.put(data, position, howmany - position);
    }
    if (intBuffer.position() % 2 == 1) {
      intBuffer.put(0);
    }
    byteBuffer.position(intBuffer.position() * MemHolder.INTSIZE);
  }

  public void writeDoubles(double ... data) throws IOException {
    writeDoubles(data, data.length);
  }

  public void writeDoubles(double[] data, int howmany) throws IOException {
    //fill up the buffer
    doubleBuffer.position(byteBuffer.position() / MemHolder.DOUBLESIZE);
    int bufferSize = doubleBuffer.limit();
    int spaceLeft = doubleBuffer.remaining();
    doubleBuffer.put(data, 0, spaceLeft >= howmany ? howmany : spaceLeft);
    if (spaceLeft < howmany) {
      byteBuffer.position(0);
      channel.write(byteBuffer);
      doubleBuffer.position(0);
      byteBuffer.position(0);
      int position = spaceLeft;
      while (position < howmany - bufferSize) {
        doubleBuffer.put(data, position, bufferSize);
        byteBuffer.position(0);
        channel.write(byteBuffer);
        doubleBuffer.position(0);
        byteBuffer.position(0);
        position += bufferSize;
      }
      doubleBuffer.put(data, position, howmany - position);
      //channel.write(byteBuffer);
    }
    byteBuffer.position(doubleBuffer.position() * MemHolder.DOUBLESIZE);
  }




}
