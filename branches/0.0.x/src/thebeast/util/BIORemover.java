package thebeast.util;

import java.util.HashSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public class BIORemover {

  public static void main(String[] args) throws IOException {
    HashSet<String> toRemove = new HashSet<String>();
    int column = Integer.valueOf(args[0]);
    for (int i = 1; i < args.length;++i) toRemove.add(args[i]);
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      if (line.trim().equals("")){
        System.out.println();  
      } else {
        String split[] = line.split("[\t ]");
        for (int col = 0; col < split.length; ++col){
          if (col > 0) System.out.print("\t");
          if (col != column || split[col].equals("O") ||
                  toRemove.contains(split[col].substring(2)))
            System.out.print(split[col]);
          else
            System.out.print("O");
        }
        System.out.println();
      }
    }
  }
}
