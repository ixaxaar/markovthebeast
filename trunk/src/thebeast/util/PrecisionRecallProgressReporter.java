package thebeast.util;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 28-Feb-2007 Time: 16:11:41
 */
public interface PrecisionRecallProgressReporter extends ProgressReporter {
  void progressed(int fpCount, int fnCount, int goldCount, int guessCount);  
}
