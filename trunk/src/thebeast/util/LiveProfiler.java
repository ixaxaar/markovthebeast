package thebeast.util;

import thebeast.pml.PropertyName;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 03-Oct-2007 Time: 17:36:19
 */
public class LiveProfiler extends TreeProfiler {

  protected int level = 0;
  protected PrintStream out;

  public LiveProfiler(PrintStream out) {
    this.out = out;
  }

  @Override
  public void start(String operation) {
    for (int i = 0; i < level; ++i) out.print("  ");
    out.println("START " + operation);
    super.start(operation);
    ++level;
  }


  @Override
  public Profiler end() {
    --level;
    for (int i = 0; i < level; ++i) out.print("  ");
    out.println("END " + current.name);    
    return super.end();
  }
}
