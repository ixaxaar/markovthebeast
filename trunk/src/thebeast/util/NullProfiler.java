package thebeast.util;

/**
 * @author Sebastian Riedel
 */
public class NullProfiler implements Profiler {
  public void start(String operation) {

  }

  public void start(String operation, int order) {

  }

  public Profiler end() {
    return this;
  }


  public String toString() {
    return "No profile available";
  }
}
