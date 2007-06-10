package thebeast.util.alchemy;

import thebeast.util.HashMultiMap;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;

/**
 * @author Sebastian Riedel
 */
public class AlchemyConverter {

  public static void main(String[] args) throws IOException {
    if (args.length != 5) {
      System.out.println("usage: AlchemyConverter <mln> <db> <typefile> <predicatefile> <datafile>");
    }

    File mln = new File(args[0]);
    File db = new File(args[1]);
    File types = new File(args[2]);
    File predicates = new File(args[3]);
    File data = new File(args[4]);

    HashMap<String, String[]> predDefinitions = new HashMap<String, String[]>();

    //read mln to get signatures
    BufferedReader reader = new BufferedReader(new FileReader(mln));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.equals("") || line.startsWith("//")) continue;
      String[] split = line.split("[ ]");
      try {
        Double.parseDouble(split[0]);
      } catch (NumberFormatException e) {
        String[] predDefinition = line.split("[(),]");
        predDefinitions.put(predDefinition[0].trim(), predDefinition);
      }
    }

    HashMap<String, HashSet<String>> typeConstants = new HashMap<String, HashSet<String>>();
    HashMultiMap<String, String[]> atoms = new HashMultiMap<String, String[]>();
    reader = new BufferedReader(new FileReader(db));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if ("".equals(line)) continue;
      String[] groundAtomDef = line.split("[(),]");
      String[] predDef = predDefinitions.get(groundAtomDef[0]);
      if (predDef == null){
        throw new RuntimeException("Woh, something's fishy with " + groundAtomDef[0]);

      }
      for (int i = 1; i < groundAtomDef.length; ++i){
        HashSet<String> constants = typeConstants.get(predDef[i].trim());
        if (constants == null){
          constants = new HashSet<String>();
          typeConstants.put(predDef[i].trim(),constants);
        }
        constants.add(groundAtomDef[i]);
      }
      atoms.add(groundAtomDef[0], groundAtomDef);
    }

    //create types;
    PrintStream out = new PrintStream(types);
    for (String type : typeConstants.keySet()){
      out.print("type " + toPMLType(type) + ": ");
      int index = 0;
      for (String constant : typeConstants.get(type)){
        if (index++ > 0) out.print(", ");
        if (index % 10 == 9) out.println();
        out.print(toPMLConstant(constant));
      }
      out.println(";");
    }
    out.flush();

    out = new PrintStream(predicates);
    for (String pred : predDefinitions.keySet()){
      out.print("predicate " + toPMLPredicate(pred) + ": ");
      String[] predDef = predDefinitions.get(pred);
      for (int i = 1; i < predDef.length; ++i){
        if (i > 1) out.print(" x ");
        out.print(toPMLType(predDef[i]));
      }
      out.println(";");
    }
    out.flush();

    out = new PrintStream(data);
    out.println(">>");
    for (String pred : predDefinitions.keySet()){
      out.println(">" + toPMLPredicate(pred));
      for (String[] atomDef : atoms.get(pred)){
        for (int i = 1; i < atomDef.length; ++i){
          if (i > 1) out.print("\t");
          out.print(toPMLConstant(atomDef[i]));
        }
        out.println();
      }
      out.println();
    }
    out.flush();

  }

  private static String toPMLType(String s){
    s = s.trim();
    return s.substring(0,1).toUpperCase() + s.substring(1);
  }

  private static String toPMLConstant(String s){
    s = s.trim();
    return "\"" + s + "\"";
  }

  private static String toPMLPredicate(String s){
    s = s.trim();
    return s.substring(0,1).toLowerCase() + s.substring(1);
  }
}
