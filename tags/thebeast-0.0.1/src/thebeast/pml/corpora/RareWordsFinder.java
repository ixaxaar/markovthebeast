package thebeast.pml.corpora;

import thebeast.util.Counter;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * @author Sebastian Riedel
 */
public class RareWordsFinder {

  public static void main(String[] args) throws FileNotFoundException {
    int column = Integer.valueOf(args[0]);
    int maxCount = Integer.valueOf(args[1]);
    Counter<String> counter = TabTokenCounter.count(System.in, column);
    System.out.println(">rare");
    System.out.println("UNKNOWN");
    for (Map.Entry<String,Integer> entry : counter.entrySet()){
      if (entry.getValue() < maxCount) System.out.println("\"" + entry.getKey() + "\"");
    }
  }



}
