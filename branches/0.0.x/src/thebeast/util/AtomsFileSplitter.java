package thebeast.util;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 06-Mar-2007 Time: 20:59:01
 */
public class AtomsFileSplitter {

  public static void main(String[] args) throws IOException {
    int size = Integer.valueOf(args[0]);
    File head = new File(args[1]);
    File tail = new File(args[2]);
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    PrintStream outHead = new PrintStream(head);
    PrintStream outTail = new PrintStream(tail);
    int sentence = 0;
    if (size >= 0) {
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (line.trim().startsWith(">>")) {
          ++sentence;
          if (sentence <= size) outHead.println(line);
          else outTail.println(line);
        } else {
          if (sentence <= size) outHead.println(line);
          else outTail.println(line);
        }
      }
    } else {
      LinkedList<String> sentences = new LinkedList<String>();
      StringBuffer buffer = new StringBuffer();
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (line.trim().equals("")) {
          if (buffer.length()>0)
            sentences.add(buffer.toString());
          buffer.setLength(0);
        } else {
          buffer.append(line).append("\n");
        }
      }
      for (String sentenceString : sentences.subList(0, sentences.size() + size)){
        outTail.println(sentenceString);
      }
      for (String sentenceString : sentences.subList(sentences.size() + size,sentences.size())){
        outHead.println(sentenceString);
      }
    }
    outHead.close();
    outTail.close();
  }
}
