package thebeast.nodmem.statement;

import thebeast.nod.type.Attribute;
import thebeast.nod.variable.Index;
import thebeast.nodmem.variable.MemHashIndex;

import java.util.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 18:51:10
 */
public class IndexInformation {

  private TreeMap<String, Index> name2index = new TreeMap<String, Index>();
  private TreeMap<String, Integer> name2nr = new TreeMap<String, Integer>();
  private TreeMap<Index, Integer> index2nr = new TreeMap<Index, Integer>();

  public int getIndexIdForAttributes(List<Attribute> attributes) {
    return -1;
  }

  /**
   * Find the index(name) for which all indexed attributes are in <code>attributes</code>
   * and the number of indexed is maximal.
   *
   * @param attributes the list of attribute names
   * @return the name of the index that doesn't cover any other attributes than in <code>attributes</code>
   *         and has the most attributes.
   */
  public String getMostCovering(List<String> attributes) {
    int maxCount = -1;
    String maxName = null;
    HashSet<String> superSet = new HashSet<String>(attributes);
    for (Map.Entry<String, Index> entry : name2index.entrySet()) {
      Index index = entry.getValue();
      if (superSet.containsAll(index.attributes())) {
        int count = index.attributes().size();
        if (count > maxCount) {
          maxCount = count;
          maxName = entry.getKey();
        }
      }
    }
    return maxName;
  }

  public String getMostCovering(String... attributes) {
    return getMostCovering(Arrays.asList(attributes));
  }

  public Index getIndex(String name) {
    return name2index.get(name);
  }

  public int addIndex(String name, Index index) {
    Index old = name2index.get(name);
    if (old != null) {
      throw new RuntimeException("There can only be one index with name " + name);
    }
    Integer oldNr = index2nr.get(index);
    if (oldNr != null) {
      throw new RuntimeException("There can only be one index of the given type and attributes");
    }
    int nr = name2nr.size();
    name2nr.put(name, nr);
    name2index.put(name,index);
    index2nr.put(index, nr);
    return nr;

  }

  public int getIndexNr(String name) {
    return name2nr.get(name);
  }


  public List<Attribute> getAttributesForIndexID(int indexID) {
    return null;
  }

  public int getIndexCount() {
    return name2index.size();
  }

  public IndexInformation getSubRelationInformation(Attribute attribute) {
    return null;
  }

  public void invalidateIndices() {
    for (Index index : name2index.values()) {
      ((MemHashIndex) index).invalidate();

    }
  }
}
