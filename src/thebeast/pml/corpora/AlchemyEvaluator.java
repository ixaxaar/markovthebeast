package thebeast.pml.corpora;

import java.util.HashSet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public class AlchemyEvaluator {

  public static void main(String[] args) throws IOException {
    String[] preds = args[0].split("[,]");
    HashSet<String> toEval = new HashSet<String>();
    for (String pred : preds) toEval.add(pred);
    HashSet<String> gold = new HashSet<String>();
    HashSet<String> guess = new HashSet<String>();
    BufferedReader reader = new BufferedReader(new FileReader(args[1]));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.equals("")) continue;
      String pred = line.substring(0,line.indexOf("("));
      if (toEval.contains(pred)) gold.add(line);
    }
    reader = new BufferedReader(new FileReader(args[2]));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.equals("")) continue;
      String pred = line.substring(0,line.indexOf("("));
      if (toEval.contains(pred)) guess.add(line);
    }

    HashSet<String> inGoldNotInGuess = new HashSet<String>(gold);
    inGoldNotInGuess.removeAll(guess);

    HashSet<String> inGuessNotInGold = new HashSet<String>(guess);
    inGuessNotInGold.removeAll(gold);

    double precision = (guess.size() - inGuessNotInGold.size()) / (double) guess.size();
    double recall = (gold.size() - inGoldNotInGuess.size()) / (double) gold.size();
    double f1 = 2 * recall * precision / (recall + precision);

    System.out.printf("%-10s%-6.4f\n", "Precision:", precision);
    System.out.printf("%-10s%-6.4f\n", "Recall:", recall);
    System.out.printf("%-10s%-6.4f\n", "F1:", f1);

  }
}
