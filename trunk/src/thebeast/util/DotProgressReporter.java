package thebeast.util;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 19-Feb-2007 Time: 17:34:18
 */
public class DotProgressReporter implements ProgressReporter {

  private PrintStream out;
  private int dotInterval, dotsPerChunk, chunksPerLine;
  private int chunkInterval, lineInterval;
  private int count;

  public DotProgressReporter(PrintStream out, int stepsPerDot, int dotsPerChunk, int chunksPerLine) {
    this.out = out;
    dotInterval = stepsPerDot;
    chunkInterval = stepsPerDot * dotsPerChunk;
    lineInterval = stepsPerDot * dotsPerChunk * chunksPerLine;
  }

  public void started() {
    count = 0;
  }

  public void progressed() {
    if (count % dotInterval == dotInterval - 1) out.print(".");
    if (count % chunkInterval == chunkInterval - 1) out.print(" ");
    if (count % lineInterval == lineInterval - 1) out.printf("%6d\n", count + 1);
    ++count;

  }

  public void finished() {
    out.println(count);
  }
}
