package thebeast.util;

import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class HashMultiMapSet<K, V> extends HashMap<K, Set<V>> {

  public void add(K key, V value) {
    Set<V> set = get(key);
    if (set.size() == 0) {
      set = new HashSet<V>();
      put(key, set);
    }
    set.add(value);
  }

  public HashMultiMapSet<K, V> deepcopy() {
    HashMultiMapSet<K, V> result = new HashMultiMapSet<K, V>();
    for (Map.Entry<K, Set<V>> entry : entrySet())
      result.put(entry.getKey(), new HashSet<V>(entry.getValue()));
    return result;
  }


  public Set<V> get(Object o) {
    Set<V> result=  super.get(o);
    return result == null ? new HashSet<V>(0) : result;
  }
}
