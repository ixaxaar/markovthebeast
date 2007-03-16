package thebeast.util;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 28-Feb-2007 Time: 16:11:41
 */
public interface PerformanceProgressReporter extends ProgressReporter {
  void progressed(double loss, int candidateCount);
}
