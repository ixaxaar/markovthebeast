package thebeast.nodmem.mem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.ByteChannel;

/**
 * @author Sebastian Riedel
 */
public class MemDeserializer {

  private ByteChannel channel;
  private IntBuffer intBuffer;
  private DoubleBuffer doubleBuffer;
  private ByteBuffer byteBuffer;
  private boolean initialized = false;
  private int remaining = 0;
  private int positionInBytes = 0;

  public MemDeserializer(ByteChannel channel, ByteBuffer byteBuffer) {
    if (byteBuffer.capacity() % MemHolder.DOUBLESIZE != 0)
      throw new IllegalArgumentException("buffer capacity must be dividable by 8");
    this.channel = channel;
    this.byteBuffer = byteBuffer;
    this.intBuffer = byteBuffer.asIntBuffer();
    this.doubleBuffer = byteBuffer.asDoubleBuffer();
  }

  public void read(int[] data, int howmany) throws IOException {
    if (!initialized) {
      init();
    }
    byteBuffer.position(positionInBytes);    
    intBuffer.position(positionInBytes / MemHolder.INTSIZE);
    //fill up
    int dstPosition = 0;
    if (intBuffer.remaining() >= howmany) {
      intBuffer.get(data, 0, howmany);
    } else {
      //fill from remainder
      int remaining = intBuffer.remaining();
      intBuffer.get(data, 0, remaining);
      dstPosition += remaining;
      while (dstPosition < howmany - intBuffer.capacity()) {
        byteBuffer.position(0);
        channel.read(byteBuffer);
        intBuffer.position(0);
        intBuffer.get(data, dstPosition, intBuffer.capacity());
        dstPosition += intBuffer.capacity();
      }
      byteBuffer.position(0);
      channel.read(byteBuffer);
      intBuffer.position(0);
      intBuffer.get(data, dstPosition, howmany - dstPosition);
    }
    if (intBuffer.position() % 2 == 1) {
      intBuffer.get();
    }
    positionInBytes = intBuffer.position() * MemHolder.INTSIZE;

  }

  private void init() throws IOException {
    channel.read(byteBuffer);
    intBuffer.position(0);
    doubleBuffer.position(0);
    byteBuffer.position(0);
    positionInBytes = 0;
    initialized = true;
  }

  public void read(double[] data, int howmany) throws IOException {
    if (!initialized) {
      init();
    }
    byteBuffer.position(positionInBytes);
    doubleBuffer.position(positionInBytes / MemHolder.DOUBLESIZE);
    //fill up
    int dstPosition = 0;
    if (doubleBuffer.remaining() >= howmany) {
      doubleBuffer.get(data, 0, howmany);
    } else {
      //fill from remainder
      int remaining = doubleBuffer.remaining();
      doubleBuffer.get(data, 0, remaining);
      dstPosition += remaining;
      while (dstPosition < howmany - doubleBuffer.capacity()) {
        byteBuffer.position(0);
        channel.read(byteBuffer);
        doubleBuffer.position(0);
        doubleBuffer.get(data, dstPosition, doubleBuffer.capacity());
        dstPosition += doubleBuffer.capacity();
      }
      byteBuffer.position(0);
      channel.read(byteBuffer);
      doubleBuffer.position(0);
      doubleBuffer.get(data, dstPosition, howmany - dstPosition);
    }
    positionInBytes = doubleBuffer.position() * MemHolder.DOUBLESIZE;    
  }

  public int readInt() throws IOException {
    int[] buffer = new int[1];
    read(buffer,1);
    return buffer[0];
  }
}
