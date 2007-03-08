package thebeast.pml.corpora;

import thebeast.util.Counter;

import java.io.*;

/**
 * @author Sebastian Riedel
 */
public class TabTokenCounter {

  public static void main(String[] args) throws FileNotFoundException {
    int column = Integer.valueOf(args[0]);
    Counter<String> counter = count(System.in,column);
    counter.save(System.out);
  }

  public static Counter<String> count(InputStream inputStream, int column)  {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      Counter<String> counter = new Counter<String>();
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (!line.trim().equals("")){
          String[] split = line.split("[ \t]");
          counter.increment(split[column],1);
        }
      }
      return counter;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
