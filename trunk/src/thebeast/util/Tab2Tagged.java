package thebeast.util;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 02-Mar-2007 Time: 17:53:19
 */
public class Tab2Tagged {

   public static void tab2sentence(InputStream in, OutputStream out, int wordColumn, int posColumn) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    LinkedList<String> sentence = new LinkedList<String>();
    PrintStream writer = new PrintStream(out);
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      if (line.trim().equals("")){
        for (String token : sentence) writer.print(token + " ");
        writer.println();
        sentence.clear();
      } else {
        String[] split = line.split("[\t ]");
        sentence.add(split[wordColumn] + "_" + split[posColumn]);
      }
    }
    writer.close();
  }

  public static void main(String[] args) throws IOException {
    int word = Integer.valueOf(args[0]);
    int pos = Integer.valueOf(args[1]);
    tab2sentence(System.in, System.out,word,pos);

  }

}
