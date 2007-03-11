package thebeast.util;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 19-Feb-2007 Time: 17:33:25
 */
public interface ProgressReporter {

  void started();
  void started(String name);
  void progressed();
  void finished();

}
