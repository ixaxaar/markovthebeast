package thebeast.util;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 19-Feb-2007 Time: 17:34:18
 */
public class DotProgressReporter implements PerformanceProgressReporter {

  private PrintStream out;
  private int dotInterval;
  private int chunkInterval, lineInterval;
  private int count;
  private StopWatch stopWatch = new StopWatch();
  private double loss;
  private boolean performanceAvailable;
  private int candidates;

  public DotProgressReporter(PrintStream out, int stepsPerDot, int dotsPerChunk, int chunksPerLine) {
    this.out = out;
    dotInterval = stepsPerDot;
    chunkInterval = stepsPerDot * dotsPerChunk;
    lineInterval = stepsPerDot * dotsPerChunk * chunksPerLine;
  }

  public void started() {
    count = 0;
    loss =  0;
    candidates = 0;
    stopWatch.start();
  }

  public void started(String name) {
    started();
    out.println("Started " + name + ":");
  }

  public void progressed() {
    if (count % dotInterval == dotInterval - 1) out.print(".");
    if (count % chunkInterval == chunkInterval - 1) out.print(" ");
    if (count % lineInterval == lineInterval - 1) {
      out.printf("%6d", count + 1);
      if (performanceAvailable){
        out.printf(" %3.2f %3.2f\n", loss / count, (double)candidates / count);
      } else {
        out.println();
      }
    }
    ++count;

  }

  public void progressed(double loss, int candidateCount) {
    performanceAvailable = true;
    this.loss += loss;
    this.candidates += candidateCount;
    progressed();
  }

  public void finished() {
    if (count == 0){
      out.println("No instances processed.");
      return;
    }
    long time = stopWatch.stopAndContinue();
    out.println();
    out.printf("%-20s%-6d\n", "Processed:", count);
    out.printf("%-20s%-6.2f\n", "Time(in s):", time/1000.0);
    out.printf("%-20s%-6d\n", "Avg. time(in ms):", time/count);
    if (performanceAvailable){
      out.printf("%-20s%-6.2f\n", "Loss: ", loss / count);
      out.printf("%-20s%-6.2f\n", "Candidates: ", (double) candidates / count);
    }
  }
}
