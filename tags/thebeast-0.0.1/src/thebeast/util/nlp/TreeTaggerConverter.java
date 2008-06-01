package thebeast.util.nlp;

import java.io.*;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 29-Nov-2007 Time: 17:01:08
 */
public class TreeTaggerConverter {

  private static void sentence2treetag(InputStream is, PrintStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      os.println(line);
      os.println("<END>");
    }
  }

  private static void treetag2conll(InputStream is, PrintStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    int token = 0;
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      if (line.startsWith("<END>")) {
        os.println();
        token = 0;
      }
      else {
        String[] split = line.split("[\t]");
        //System.out.println(Arrays.toString(split));
        os.print(token++ + "\t");
        os.print(split[0] + "\t");
        os.print("_\t");
        os.print(split[1] + "\t");
        os.print(split[1] + "\t");
        os.print("_\t");
        os.println();
      }
    }
  }

  public static void main(String[] args) throws IOException {
    String operation = args[0];
    if (operation.equals("s2tt"))
      sentence2treetag(System.in, System.out);
    else if (operation.equals("tt2conll"))
      treetag2conll(System.in, System.out);
    else {
      System.out.println("usage TreeTaggerConverter s2tt|tt2conll <<infile> > <outfile>");
    }
  }

}
