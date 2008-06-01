package thebeast.util.alchemy;

import thebeast.util.HashMultiMapSet;
import thebeast.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sebastian Riedel
 */
public class AlchemyTransitivityEnforcer {
  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    HashSet<String> bibs = new HashSet<String>();
    HashSet<Pair<String, String>> matches = new HashSet<Pair<String, String>>();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.startsWith("SameBib")) {
        String[] atom = line.split("[(),]");
        bibs.add(atom[1]);
        bibs.add(atom[2]);
        matches.add(new Pair<String, String>(atom[1], atom[2]));
      } else
        System.out.println(line);
    }
    //System.out.println(bibs.size());
    //System.out.println(matches.size());
    //build transitivity matrix
    HashMultiMapSet<String, String> incoming = new HashMultiMapSet<String, String>();
    HashMultiMapSet<String, String> outgoing = new HashMultiMapSet<String, String>();

    for (Pair<String, String> match : matches) {
      Set<String> srcs = new HashSet<String>(incoming.get(match.arg1));
      Set<String> tgts = new HashSet<String>(outgoing.get(match.arg2));
      incoming.add(match.arg2, match.arg1);
      outgoing.add(match.arg1, match.arg2);
      //closure.add(match.arg1, match.arg1);
      //closure.add(match.arg2, match.arg2);
      for (String src : srcs) {
        incoming.add(match.arg2, src);
        outgoing.add(src, match.arg2);
      }
      for (String tgt : tgts) {
        incoming.add(tgt, match.arg1);
        outgoing.add(match.arg1, tgt);
      }
      for (String src : srcs)
        for (String tgt : tgts) {
          incoming.add(tgt, src);
          outgoing.add(src, tgt);
        }
    }
    for (String bib1 : bibs){
      for (String bib2 : outgoing.get(bib1)){
        System.out.println("SameBib(" + bib1 + "," + bib2 + ")");
      }
    }
  }
}
