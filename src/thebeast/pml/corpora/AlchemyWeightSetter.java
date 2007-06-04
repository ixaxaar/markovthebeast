package thebeast.pml.corpora;

import thebeast.pml.Weights;
import thebeast.pml.Model;
import thebeast.pml.TheBeast;
import thebeast.pml.function.WeightFunction;
import thebeast.nod.FileSource;

import java.io.*;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * @author Sebastian Riedel
 */
public class AlchemyWeightSetter {
  public static void main(String[] args) throws Exception {
    String weightFileType = args[0];
    Model model = TheBeast.getInstance().loadModel(new FileInputStream(args[1]));
    Weights weights = model.getSignature().createWeights();

    PrintStream oldOut = System.out;
    if (weightFileType.equals("dump")) {
      FileSource source = TheBeast.getInstance().getNodServer().createSource(new File(args[2]), 1024);
      weights.read(source);
    } else if (weightFileType.equals("text")) {
      weights.load(new FileInputStream(args[2]));
    }
    //read order file
    BufferedReader reader = new BufferedReader(new FileReader(args[3]));
    LinkedList<Double> weightList = new LinkedList<Double>();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.startsWith("//")) continue;
      WeightFunction w = model.getSignature().getWeightFunction(line);
      weightList.add(weights.getWeight(w));
    }
    Iterator<Double> weightIterator = weightList.iterator();
    reader = new BufferedReader(new InputStreamReader(System.in));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      int whitespace = line.indexOf(" ");
      if (line.equals("") || whitespace == -1) {
        System.out.println(line);
        continue;
      }
      String prefix = line.substring(0, whitespace);
      try {
        Double.parseDouble(prefix);
        double newWeight = weightIterator.next();
        System.out.println(newWeight + line.substring(whitespace));
      } catch (Exception e) {
        System.out.println(line);
      }
    }

  }
}
