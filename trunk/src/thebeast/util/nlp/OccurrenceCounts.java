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

  public void loadSentences(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    int lineNr = 0;
    for (String line = reader.readLine();line != null; line = reader.readLine()){
      String[] split = line.trim().split("[ ]");
      for (String word : split){
        occurrences.add(word, lineNr);
      }
      ++lineNr;
      if (lineNr % 1000 == 0) System.err.print(".");
    }
  }

  public static void main(String[] args) throws IOException {
    String src = args[0];
    String tgt = args[1];
    String dst = args[2];
    OccurrenceCounts counts = new OccurrenceCounts();
    System.err.println();
    System.err.println(src);
    counts.loadSentences(new FileInputStream(src));
    System.err.println();
    System.err.println(tgt);
    counts.loadSentences(new FileInputStream(tgt));
    PrintStream stream = new PrintStream(dst);
    System.err.println();
    System.err.println(dst);
    counts.save(stream);
    stream.close();
  }

}
