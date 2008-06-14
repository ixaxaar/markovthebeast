package thebeast.util;

import thebeast.pml.PropertyName;

/**
 * @author Sebastian Riedel
 */
public class NullProfiler implements Profiler {
  public void start(String operation) {

  }

  public double getAverageTime(String operation) {
    return 0;
  }

  public long getTotalTime(String operation) {
    return 0;
  }

  public double getCalls(String operations) {
    return 0;
  }

  public Profiler end() {
    return this;
  }


  public String toString() {
    return "No profile available";
  }

  public void setProperty(PropertyName name, Object value) {

  }

  public Object getProperty(PropertyName name) {
    return null;
  }
}
