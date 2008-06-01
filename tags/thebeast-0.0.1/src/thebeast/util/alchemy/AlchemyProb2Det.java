package thebeast.util.alchemy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Sebastian Riedel
 */
public class AlchemyProb2Det {

  public static void main(String[] args) throws IOException {
    //HashSet<String> existingLines = new HashSet<String>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      //if (existingLines.contains(line)) continue;
      if (line.equals("")) continue;

      //check probability > 0.5
      double prob = Double.parseDouble(line.substring(line.lastIndexOf(")")+1).trim());
      if (prob > 0.5)
        System.out.println(line.substring(0,line.lastIndexOf(")")+1));
      //existingLines.add(line);
    }


  }

}
