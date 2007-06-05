package thebeast.pml.corpora;

import java.util.HashSet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Sebastian Riedel
 */
public class AlchemyFilter {

  public static void main(String[] args) throws IOException {
    String[] preds = args[0].split("[,]");
    HashSet<String> toRemove = new HashSet<String>();
    HashSet<String> existingLines = new HashSet<String>();
    for (String pred : preds) toRemove.add(pred);
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (existingLines.contains(line)) continue;
      if (line.equals("")) continue;
      String pred = line.substring(0,line.indexOf("("));
      if (!toRemove.contains(pred))
        System.out.println(line);
      existingLines.add(line);
    }


  }

}
