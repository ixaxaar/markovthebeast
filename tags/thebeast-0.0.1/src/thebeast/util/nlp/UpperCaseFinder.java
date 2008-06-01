package thebeast.util.nlp;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 01-Dec-2007 Time: 19:08:31
 */
public class UpperCaseFinder {

  public static void main(String[] args) throws IOException {
    String lowerCasedFileName = args[0];
    HashMap<String,String> mapping = new HashMap<String, String>();
    for (int i = 1; i < args.length; ++i){
      BufferedReader reader = new BufferedReader(new FileReader(args[i]));
      for (String line = reader.readLine(); line != null; line = reader.readLine()){
        String upperCase = line.trim();
        String lowerCase = upperCase.toLowerCase();
        mapping.put(lowerCase,upperCase);
      }
    }
    BufferedReader reader = new BufferedReader(new FileReader(lowerCasedFileName));
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      line = line.trim();
      String upperCase = mapping.get(line);
      System.out.println(upperCase != null ? upperCase : line);
    }
    
  }

}
