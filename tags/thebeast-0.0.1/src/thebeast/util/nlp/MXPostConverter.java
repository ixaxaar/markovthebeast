package thebeast.util.nlp;

import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 29-Nov-2007 Time: 19:41:22
 */
public class MXPostConverter {

  private static void mx2conll(InputStream is, PrintStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      int tokenIndex = 1;
      String[] tokens = line.split(" ");
      for (String token: tokens){
        String[] split = token.split("[_]");
        String word = split[0];
        String pos = split[1];
        os.print(tokenIndex++ + "\t");
        os.print(word + "\t");
        os.print("_\t");
        //os.print(pos + "\t");
        os.print((pos.length() > 2 ? pos.substring(0,2) : pos) + "\t");
        os.print(pos + "\t");
        os.print("_\t");
        os.print("0\t");
        os.print("_");
        os.println();
      }
      os.println();
    }
  }

  private static void mx2s(InputStream is, PrintStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      String[] tokens = line.split(" ");
      for (String token: tokens){
        String[] split = token.split("[_]");
        String word = split[0];
        os.print(word + " ");
      }
      os.println();
    }
  }

  private static void conll2mx(InputStream is, PrintStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      line = line.trim();
      if (line.equals("")) os.println();
      else {
        String[] split = line.split("[ \t]");
        String word = split[1];
        word = word.replaceAll("[_]","-US-");
        String pos = split[4];
        os.print(word + "_" + pos + " ");
      }
    }
  }

  public static void main(String[] args) throws IOException {
    String operation = args[0];
    if (operation.equals("mx2conll"))
      mx2conll(System.in, System.out);
    else if (operation.equals("conll2mx"))
      conll2mx(System.in, System.out);
    else if (operation.equals("mx2s"))
      mx2s(System.in, System.out);
    else {
      System.out.println("usage MXPostConverter mx2conll <<infile> > <outfile>");
    }
  }

}
