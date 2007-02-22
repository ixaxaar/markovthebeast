package thebeast.pml;

import thebeast.nod.variable.RelationVariable;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 06-Feb-2007 Time: 22:01:10
 */
public interface ILPSolver {
  void init();
  void add(RelationVariable variables, RelationVariable constraints);
  RelationVariable solve();
  void setVerbose(boolean verbose);
  void setProperty(String name, Object value);
}
