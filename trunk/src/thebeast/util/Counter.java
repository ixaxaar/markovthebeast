package thebeast.util;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

/**
 * @author Sebastian Riedel
 */
public class Counter<T> extends HashMap<T, Integer> {

  public Integer get(Object o) {
    Integer original = super.get(o);
    return original == null ? 0 : original;
  }

  public void increment(T value, int howmuch) {
    Integer old = super.get(value);
    put(value, old == null ? howmuch : old + howmuch);
  }

  public static Counter<String> loadFromFile(File file) throws IOException {
    Counter<String> result = new Counter<String>();
    BufferedReader reader = new BufferedReader(new FileReader(file));
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      if (!line.trim().equals("")){
        String[] split = line.split("[ \t]");
        result.increment(split[0],Integer.valueOf(split[1]));
      }
    }
    return result;
  }

  public void save(OutputStream outputStream) throws FileNotFoundException {
    PrintStream out = new PrintStream(outputStream);
    for (Map.Entry<T,Integer> entry : entrySet()){
      out.println(entry.getKey() + "\t" + entry.getValue());
    }
  }

}
