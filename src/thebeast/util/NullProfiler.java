package thebeast.util;

import thebeast.pml.PropertyName;

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

  public void setProperty(PropertyName name, Object value) {

  }

  public Object getProperty(PropertyName name) {
    return null;
  }
}
