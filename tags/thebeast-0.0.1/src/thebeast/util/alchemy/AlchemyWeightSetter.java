package thebeast.util.alchemy;

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
    if (args.length == 0){
      System.out.println("usage: text|dump <model> <weights> <orderfile>");
      System.exit(0);
    }
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
    LinkedList<Boolean> signList = new LinkedList<Boolean>();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.startsWith("//")) continue;
      if (line.startsWith("-")){
        line = line.substring(1);
        signList.add(false);
      } else
        signList.add(true);
      WeightFunction w = model.getSignature().getWeightFunction(line);
      weightList.add(weights.getWeight(w));

    }
    Iterator<Double> weightIterator = weightList.iterator();
    Iterator<Boolean> signIterator = signList.iterator();
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
        boolean newSign = signIterator.next();
        double newWeight = weightIterator.next() * (newSign ? 1.0 : -1.0);
        String weightString = Double.toString(newWeight).replace("E-","e-");
        System.out.println(weightString + line.substring(whitespace));
      } catch (Exception e) {
        System.out.println(line);
      }
    }
    if (weightIterator.hasNext())
      throw new RuntimeException("More weights then formulas!");


  }
}
