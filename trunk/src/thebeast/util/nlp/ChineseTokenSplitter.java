package thebeast.util.nlp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Oct-2007 Time: 20:16:30
 */
public class ChineseTokenSplitter {

  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    for (String line = reader.readLine();line != null; line = reader.readLine()){
      String[] split = line.trim().split("[ ]");
      int index = 0;
      for (String word : split){
        for (char ch : word.toCharArray()){
          if (index++ > 0) System.out.print(" ");
          System.out.print(ch);
        }
      }
      System.out.println();
    }

  }
}
