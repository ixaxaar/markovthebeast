package thebeast.util;

import thebeast.pml.HasProperties;

/**
 * @author Sebastian Riedel
 */
public interface Profiler extends HasProperties {
  void start(String operation);

  double getAverageTime(String operation);

  long getTotalTime(String operation);

  double getCalls(String operation);

  Profiler end();
}
