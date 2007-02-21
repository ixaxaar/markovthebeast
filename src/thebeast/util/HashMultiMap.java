package thebeast.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Sebastian Riedel
 */
public class HashMultiMap<K,V> extends HashMap<K,List<V>> {

    public void add(K key, V value){
        List<V> list = get(key);
        if (list == null){
            list = new LinkedList<V>();
            put(key,list);
        }
        list.add(value);
    }

    public HashMultiMap<K, V> deepcopy() {
        HashMultiMap<K, V> result = new HashMultiMap<K,V>();
        for (Map.Entry<K,List<V>> entry : entrySet())
            result.put(entry.getKey(), new LinkedList<V>(entry.getValue()));
        return result;
    }
}
