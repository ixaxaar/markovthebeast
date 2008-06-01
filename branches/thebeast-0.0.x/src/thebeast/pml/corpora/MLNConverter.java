package thebeast.pml.corpora;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class MLNConverter {

  public static void main(String[] args) throws IOException {
    String type = args[0];
    HashSet<String> hidden = new HashSet<String>();
    for (String s : args[1].split("[,]")) hidden.add(s.trim());

    int count = 0;
    if (type.equals("web")) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        line = line.trim();
        if (line.startsWith("Exists")) continue;
        if (line.contains("=>")){
          String[] imp = line.split("[=]");
          String premise = imp[0].trim();
          String conclusion = imp[1].substring(1).trim();
          LinkedList<Boolean> signs = new LinkedList<Boolean>();
          LinkedList<String> atoms = new LinkedList<String>();
          String[] premiseAtoms = premise.split("[\\^]");
          for (String atom : premiseAtoms){
            atom = atom.trim();
            if (atom.startsWith("!")) {
              atoms.add(atom.substring(1));
              signs.add(true);
            }
            else {
              atoms.add(atom);
              signs.add(false);
            }
          }
          int oldIndex = 0;
          for (int index = conclusion.indexOf(" or ",0); index != -1; index = conclusion.indexOf(" or ", index + 1)){
            String atom = conclusion.substring(oldIndex, index);
            if (atom.startsWith("!")) {
              atoms.add(atom.substring(1));
              signs.add(false);
            } else {
              atoms.add(atom);
              signs.add(true);              
            }
            oldIndex = index;
          }
          String atom = conclusion.substring(oldIndex);
          if (atom.startsWith("!")) {
            atoms.add(atom.substring(1));
            signs.add(false);
          } else {
            atoms.add(atom);
            signs.add(true);
          }
          LinkedList<String> conditionAtoms = new LinkedList<String>();
          LinkedList<Boolean> conditionSigns = new LinkedList<Boolean>();
          LinkedList<String> prem = new LinkedList<String>();
          LinkedList<Boolean> premSigns = new LinkedList<Boolean>();
          LinkedList<String> conc = new LinkedList<String>();
          LinkedList<Boolean> concSigns = new LinkedList<Boolean>();
          for (int i = 0; i < atoms.size(); ++i){
            if (!hidden.contains(atoms.get(i).substring(0,atoms.get(i).indexOf("(")))){
              conditionAtoms.add(atoms.get(i));
              conditionSigns.add(signs.get(i));
            } else if (signs.get(i)){
              conc.add(atoms.get(i));
              concSigns.add(true);
            } else {
              prem.add(atoms.get(i));
              premSigns.add(true);
            }
          }
          boolean conjunction = conc.isEmpty();
          int size = conc.size() + prem.size();
          if (size == 0) continue;
          boolean local = size == 1;

          System.out.println("weight w_" + count +": Double" + (local ? "" : conjunction ? "-":"+")+";");
          HashSet<String> variables = new HashSet<String>();
          HashMap<String,String> mln2pml = new HashMap<String, String>();
          for (String atomText : atoms){
            String[] split = atomText.split("[(),]");
            StringBuffer replacement = new StringBuffer(toPMLPredicate(split[0]) + "(");
            for (int i = 1; i < split.length;++i ){
              String arg = split[i].trim();
              if (i > 1) replacement.append(",");
              if (arg.contains(":")) arg = arg.substring(0, arg.indexOf(":"));
              if (!Character.isLowerCase(arg.charAt(0))){
                String var = arg.substring(0, 1).toLowerCase();
                replacement.append(var);
                variables.add(var);
              }else {
                replacement.append("\"").append(arg).append("\"");
              }
            }
            replacement.append(")");
            mln2pml.put(atomText, replacement.toString());
          }
          System.out.print("for ");
          int index = 0;
          for (String var : variables) {
            if (index++>0) System.out.print(",");
            System.out.print(var);
          }
          if (conditionAtoms.size() > 0){
            System.out.print(" if ");
            for (int i = 0; i < conditionAtoms.size(); ++i){
              if (i > 0) System.out.print(" & ");
              System.out.print(conditionSigns.get(i) ? "" : "!");
              System.out.print(mln2pml.get(conditionAtoms.get(i)));
            }
          }
          System.out.print(" add [ ");
          for (int i = 0; i < prem.size(); ++i){
            if (i > 0) System.out.print(" & ");
            System.out.print(premSigns.get(i) ? "" : "!");
            System.out.print(mln2pml.get(prem.get(i)));
          }
          if (!conjunction) System.out.print(" => ");
          for (int i = 0; i < conc.size(); ++i){
            if (i > 0) System.out.print(" | ");
            System.out.print(concSigns.get(i) ? "" : "!");
            System.out.print(mln2pml.get(conc.get(i)));
          }
          System.out.println(" ] * w_" + count +";");

          ++count;
        }

      }
    }
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
