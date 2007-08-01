package thebeast.pml.training;

import thebeast.pml.GroundAtoms;
import thebeast.pml.HasProperties;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 07-Mar-2007 Time: 21:32:31
 */
public interface LossFunction extends HasProperties {

  double loss(GroundAtoms gold, GroundAtoms guess);

}
