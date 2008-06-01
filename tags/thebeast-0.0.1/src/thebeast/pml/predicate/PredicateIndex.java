package thebeast.pml.predicate;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Sebastian Riedel
 */
public class PredicateIndex {

  private List<Boolean> markers;


  public PredicateIndex(List<Boolean> markers) {
    this.markers = new ArrayList<Boolean>(markers);
  }


  public List<Boolean> getMarkers() {
    return markers;
  }
}
