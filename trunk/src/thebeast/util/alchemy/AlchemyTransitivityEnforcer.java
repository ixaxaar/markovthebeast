package thebeast.util.alchemy;

import thebeast.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * @author Sebastian Riedel
 */
public class AlchemyTransitivityEnforcer {
  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    HashSet<String> bibs = new HashSet<String>();
    HashSet<Pair<String,String>> matches = new HashSet<Pair<String, String>>();
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      line = line.trim();
      if (line.startsWith("SameBib")){
        String[] atom = line.split("[(),]");
        bibs.add(atom[1]);
        bibs.add(atom[2]);
        matches.add(new Pair<String, String>(atom[1],atom[2]));
      }
      System.out.println(line);
    }
    System.out.println(bibs.size());
    System.out.println(matches.size());
    for (String bib1 : bibs)
      for (String bib2: bibs)
        for (String bib3: bibs){
          if (matches.contains(new Pair<String,String>(bib1,bib2)) &&
                  matches.contains(new Pair<String,String>(bib2,bib3)) &&
                  !matches.contains(new Pair<String,String>(bib1,bib3)))
            System.out.println("SameBib(" + bib1 + "," + bib3 +")");

        }
  }
}
