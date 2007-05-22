package thebeast.util;

import thebeast.pml.HasProperties;

/**
 * @author Sebastian Riedel
 */
public interface Profiler extends HasProperties {
  void start(String operation);

  void start(String operation, int order);

  Profiler end();
}
