package thebeast.pml;

import thebeast.nod.variable.RelationVariable;
import thebeast.util.Profiler;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 06-Feb-2007 Time: 22:01:10
 */
public interface ILPSolver extends HasProperties {
  void init();
  void add(RelationVariable variables, RelationVariable constraints);
  RelationVariable solve();
  void setVerbose(boolean verbose);
  void setProfiler(Profiler profiler);
}
