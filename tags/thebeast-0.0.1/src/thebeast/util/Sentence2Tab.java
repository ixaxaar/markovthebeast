package thebeast.util;

import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 02-Mar-2007 Time: 15:33:56
 */
public class Sentence2Tab {

  public static void sentence2tab(InputStream in, OutputStream out) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    PrintStream writer = new PrintStream(out);
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      if (line.trim().equals("")) continue;
      String[] split = line.split("[ ]");
      for (String word : split){
        writer.println(word);
      }
      writer.println();
    }
    writer.close();
  }

  public static void main(String[] args) throws IOException {
    sentence2tab(System.in, System.out);    
  }

}
