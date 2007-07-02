package thebeast.util.alchemy;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
public class AlchemyProb2Det {

  public static void main(String[] args) throws IOException {
    HashSet<String> existingLines = new HashSet<String>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (existingLines.contains(line)) continue;
      if (line.equals("")) continue;
      //check probability > 0.5  
      String pred = line.substring(0, line.indexOf("("));
      System.out.println(line);
      existingLines.add(line);
    }


  }

}
