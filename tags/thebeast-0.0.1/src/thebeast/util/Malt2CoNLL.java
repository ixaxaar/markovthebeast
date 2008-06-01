package thebeast.util;

import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 02-Mar-2007 Time: 17:53:19
 */
public class Malt2CoNLL {

   public static void malt2conll(InputStream in, OutputStream out) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    PrintStream writer = new PrintStream(out);
    int lineNr = 1;
     for (String line = reader.readLine(); line != null; line = reader.readLine()){                                  
      if (!line.trim().equals("")){
        String[] split = line.split("[\t ]");
        writer.print(lineNr + "\t");
        writer.print(split[0] + "\t");
        writer.print("_\t");
        writer.print(split[1].length() > 2 ? split[1].substring(0,2) : split[1]);
        writer.print("\t");
        writer.print(split[1] + "\t");
        writer.print("_\t");
        writer.print(split[2] + "\t");
        writer.print(split[3]);
        writer.print("\t_\t_");
        writer.println();
        ++lineNr;
      } else {
        writer.println();
        lineNr = 1;
      }
    }
    writer.close();
  }

  public static void main(String[] args) throws IOException {
    malt2conll(System.in, System.out);

  }

}
