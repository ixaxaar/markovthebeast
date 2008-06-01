package thebeast.util;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;

/**
 * @author Sebastian Riedel
 */
public class TextFileUnion {
  public static void main(String[] args) throws IOException {
    HashSet<String> lines = new HashSet<String>();
    for (String arg : args) {
      BufferedReader reader = new BufferedReader(new FileReader(arg));
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        lines.add(line.trim());
      }
    }
    ArrayList<String> sorted = new ArrayList<String>(lines);
    Collections.sort(sorted);
    for (String line : sorted){
      System.out.println(line);
    }

  }
}
