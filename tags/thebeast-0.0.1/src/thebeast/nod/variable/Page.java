package thebeast.nod.variable;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface Page {

  void add(Variable variable);
  void remove(Variable variable);

  List<Variable> getContent();

}
