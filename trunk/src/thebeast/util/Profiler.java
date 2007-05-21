package thebeast.util;

/**
 * @author Sebastian Riedel
 */
public interface Profiler {
  void start(String operation);

  void start(String operation, int order);

  Profiler end();
}
