package thebeast.util;

import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 06-Mar-2007 Time: 20:59:01
 */
public class Splitter {

  public static void main(String[] args) throws IOException {
    int size = Integer.valueOf(args[0]);
    File head = new File(args[1]);
    File tail = new File(args[2]);
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    PrintStream outHead = new PrintStream(head);
    PrintStream outTail = new PrintStream(tail);
    int sentence = 0;
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      if (line.trim().equals("")){
        if (sentence < size) outHead.println();
        else outTail.println();
        ++sentence;
      } else {
        if (sentence < size) outHead.println(line);
        else outTail.println(line);
      }
    }
    outHead.close();
    outTail.close();
  }
}
