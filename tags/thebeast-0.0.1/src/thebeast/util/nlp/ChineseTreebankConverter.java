package thebeast.util.nlp;

import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 19-Nov-2007 Time: 20:00:30
 */
public class ChineseTreebankConverter {

  public static void main(String[] args) throws IOException {
    for (String fileName : args) {
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      for (String line = reader.readLine(); line != null; line = reader.readLine()){
        if (line.startsWith("<S")){
          String sentence = reader.readLine();
          String[] split = sentence.split("[ ]");
          for (String token : split){
            if (!token.startsWith("_")){
              String[] wordPos = token.split("_");
              if (wordPos.length < 2) continue;
              String word = wordPos[0];
              String pos = wordPos[1];
              System.out.println(word + "\t" + pos);
            }
          }
          System.out.println();
        } 
      }
      System.err.print(".");
    }

  }
}
