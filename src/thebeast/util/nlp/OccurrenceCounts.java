package thebeast.util.nlp;

import thebeast.util.HashMultiMapSet;

import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.Set;
import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 24-Sep-2007 Time: 19:58:18
 */
public class OccurrenceCounts {

  private HashMultiMapSet<String,Integer> occurrences = new HashMultiMapSet<String, Integer>();

  public int getCount(String string){
    return occurrences.get(string).size();
  }

  public int getCoOccurrenceCount(String s1, String s2){
    HashSet<Integer> intersect = new HashSet<Integer>(occurrences.get(s1));
    intersect.retainAll(occurrences.get(s2));
    return intersect.size();
  }

  public double getDiceCoefficient(String s1, String s2){
    return 2.0 * getCoOccurrenceCount(s1,s2) / (double) (getCount(s1) + getCount(s2));
  }

  public void load(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      StringTokenizer tokenizer = new StringTokenizer(line, "[\t ]", false);
      String word = tokenizer.nextToken();
      while (tokenizer.hasMoreTokens()){
        occurrences.add(word, Integer.parseInt(tokenizer.nextToken()));
      }
    }
  }

  public void save(PrintStream out) {
    for (Map.Entry<String, Set<Integer>> entry : occurrences.entrySet()){
      out.print(entry.getKey());
      for (int i : entry.getValue()){
        out.print(" " + i);
      }
      out.println();
    }
  }

}
