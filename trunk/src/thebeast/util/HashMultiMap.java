package thebeast.util;

import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class HashMultiMap<K, V> extends HashMap<K, List<V>> {

  public void add(K key, V value) {
    List<V> list = get(key);
    if (list.size() == 0) {
      list = new LinkedList<V>();
      put(key, list);
    }
    list.add(value);
  }

  public HashMultiMap<K, V> deepcopy() {
    HashMultiMap<K, V> result = new HashMultiMap<K, V>();
    for (Map.Entry<K, List<V>> entry : entrySet())
      result.put(entry.getKey(), new LinkedList<V>(entry.getValue()));
    return result;
  }


  public List<V> get(Object o) {
    List<V> result=  super.get(o);
    return result == null ? new ArrayList<V>(0) : result;
  }
}
