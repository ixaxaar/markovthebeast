package thebeast.util.alchemy;

import thebeast.pml.Model;
import thebeast.pml.TheBeast;
import thebeast.pml.Weights;
import thebeast.pml.function.WeightFunction;
import thebeast.nod.FileSource;

import java.io.*;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * @author Sebastian Riedel
 */
public class AlchemyWeights2TheBeast {
  public static void main(String[] args) throws Exception {
    if (args.length == 0){
      System.out.println("usage: <model> <orderfile>");
      System.exit(0);
    }
    Model model = TheBeast.getInstance().loadModel(new FileInputStream(args[0]));
    Weights weights = model.getSignature().createWeights();

    //read order file
    BufferedReader reader = new BufferedReader(new FileReader(args[1]));
    LinkedList<Boolean> signList = new LinkedList<Boolean>();
    LinkedList<String> functionList = new LinkedList<String>();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.startsWith("//")) continue;
      if (line.startsWith("-")){
        line = line.substring(1);
        functionList.add(line);
        signList.add(false);
      } else {
        signList.add(true);
        functionList.add(line);
      }

    }
    Iterator<Boolean> signIterator = signList.iterator();
    Iterator<String> functionIteration = functionList.iterator();
    reader = new BufferedReader(new InputStreamReader(System.in));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      int whitespace = line.indexOf(" ");
      if (line.equals("") || whitespace == -1) {
        continue;
      }
      String prefix = line.substring(0, whitespace);
      try {
        double newWeight = Double.parseDouble(prefix);
        boolean newSign = signIterator.next();
        newWeight *=  newSign ? 1.0 : -1.0;
        String newFunction = functionIteration.next();
        weights.addWeight(newFunction, newWeight);
      } catch (Exception e) {
        //not a double
      }
    }
    if (functionIteration.hasNext())
      throw new RuntimeException("More weights then formulas!");

    weights.save(System.out);

  }
}
