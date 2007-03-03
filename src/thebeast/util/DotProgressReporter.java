package thebeast.util;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 19-Feb-2007 Time: 17:34:18
 */
public class DotProgressReporter implements PrecisionRecallProgressReporter{

  private PrintStream out;
  private int dotInterval;
  private int chunkInterval, lineInterval;
  private int count;
  private StopWatch stopWatch = new StopWatch();
  private int goldCount, guessCount, fnCount, fpCount;

  public DotProgressReporter(PrintStream out, int stepsPerDot, int dotsPerChunk, int chunksPerLine) {
    this.out = out;
    dotInterval = stepsPerDot;
    chunkInterval = stepsPerDot * dotsPerChunk;
    lineInterval = stepsPerDot * dotsPerChunk * chunksPerLine;
  }

  public void started() {
    count = 0;
    fnCount = fpCount = goldCount = guessCount = 0;
    stopWatch.start();
  }

  public void progressed() {
    if (count % dotInterval == dotInterval - 1) out.print(".");
    if (count % chunkInterval == chunkInterval - 1) out.print(" ");
    if (count % lineInterval == lineInterval - 1) out.printf("%6d\n", count + 1);
    ++count;

  }

  public void progressed(int fpCount, int fnCount, int goldCount, int guessCount) {
    this.fpCount += fpCount;
    this.fnCount += fnCount;
    this.goldCount += goldCount;
    this.guessCount += guessCount;
    progressed();
  }

  public void finished() {
    long time = stopWatch.stopAndContinue();
    out.println();
    out.printf("%-20s%-6d\n", "Processed:", count);
    out.printf("%-20s%-6.2f\n", "Time(in s):", time/1000.0);
    out.printf("%-20s%-6d\n", "Avg. time(in ms):", time/count);
    if (goldCount > 0){
      out.printf("%-20s%-6d\n", "Gold count: ", goldCount);
      out.printf("%-20s%-6d\n", "Guess count: ", guessCount);
      out.printf("%-20s%-6d\n", "FP count: ", fpCount);
      out.printf("%-20s%-6d\n", "FN count: ", fnCount);
      double recall = (goldCount - fnCount) / (double)goldCount;
      double precision = (guessCount - fpCount) / (double)guessCount;
      out.printf("%-20s%-6.2f\n", "Recall: ", recall);
      out.printf("%-20s%-6.2f\n", "Precision: ", precision);
    }
  }
}
