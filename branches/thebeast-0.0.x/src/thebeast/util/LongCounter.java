package thebeast.util;

import java.util.*;
import java.io.*;

/**
 * @author Sebastian Riedel
 */
public class LongCounter<T> extends HashMap<T, Long> {

  public Long get(Object o) {
    Long original = super.get(o);
    return original == null ? 0 : original;
  }

  public void increment(T value, long howmuch) {
    Long old = super.get(value);
    put(value, old == null ? howmuch : old + howmuch);
  }

  public static LongCounter<String> loadFromFile(File file) throws IOException {
    LongCounter<String> result = new LongCounter<String>();
    BufferedReader reader = new BufferedReader(new FileReader(file));
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      if (!line.trim().equals("")){
        String[] split = line.split("[ \t]");
        result.increment(split[0],Long.valueOf(split[1]));
      }
    }
    return result;
  }

  public List<Map.Entry<T,Long>> sorted(final boolean descending){
    ArrayList<Map.Entry<T,Long>> sorted = new ArrayList<Map.Entry<T, Long>>(entrySet());
    Collections.sort(sorted, new Comparator<Map.Entry<T,Long>>(){
      public int compare(Map.Entry<T, Long> o1, Map.Entry<T, Long> o2) {
        return (int) ((descending ? 1 : -1) *( o2.getValue() - o1.getValue()));
      }
    });
    return sorted;
  }

  public void save(OutputStream outputStream)  {
    PrintStream out = new PrintStream(outputStream);
    for (Map.Entry<T,Long> entry : sorted(true)){
      out.println(entry.getKey() + "\t" + entry.getValue());
    }
  }

  public long getMaximum(){
    long max = 0;
    for (Long value : values())
      if (value > max) max = value;
    return max;
  }

}