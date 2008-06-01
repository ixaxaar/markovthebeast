package thebeast.pml.corpora;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public class BrillDictionaryExtractor {

  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    System.out.println(">brill");
    for (String line = reader.readLine(); line !=null; line = reader.readLine()){
      String[] split = line.split("[ \t]");
      String word = "\"" + split[0] + "\"";
      for (int i = 1; i < split.length; ++i){
        System.out.println(word + "\t\"" +split[i] + "\"");
      }
    }
  }
}
