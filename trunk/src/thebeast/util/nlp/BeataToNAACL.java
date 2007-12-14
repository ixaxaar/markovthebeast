package thebeast.util.nlp;

import thebeast.util.Pair;

import java.io.*;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 24-Nov-2007 Time: 19:29:23
 */
public class BeataToNAACL {


  private static void beata2berkeley(InputStream is, PrintStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      String[] alignments = line.split("[,]");
      for (String alignment : alignments) {
        String[] srcTgt = alignment.split("[.]");
        int src = Integer.parseInt(srcTgt[0]);
        int tgt = Integer.parseInt(srcTgt[1]);
        os.print((src+1) + "-" +  (tgt+1) + " ");
      }
      os.println();
    }
  }

  public static void main(String[] args) throws IOException {
    if (args[0].equals("beata2naacl"))
      beata2naacl();
    else if (args[0].equals("remove"))
      beataRemovePossible(new FileInputStream(args[1]),new FileInputStream(args[2]),System.out);
    else if (args[0].equals("beata2berkeley"))
      beata2berkeley(System.in,System.out);
  }

  private static void beataRemovePossible(InputStream alignments, InputStream possible, PrintStream out) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(alignments));
    BufferedReader possibleReader = new BufferedReader(new InputStreamReader(possible));
    String possibleLine;
    int sentenceNr = 1;
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      possibleLine = possibleReader.readLine();
      String[] split = line.split("[,]");
      HashSet<Pair<Integer,Integer>> result = new HashSet<Pair<Integer, Integer>>();
      for (String alignment : split) {
        String[] srcTgt = alignment.split("[.]");
        int src = Integer.parseInt(srcTgt[0]);
        int tgt = Integer.parseInt(srcTgt[1]);
        result.add(new Pair<Integer, Integer>(src+1,tgt+1));
      }
      String[] possibleSplit = possibleLine.split("[,]");
      if (!possibleLine.equals("")) for (String alignment : possibleSplit) {
        String[] srcTgt = alignment.split("[.]");
        int src = Integer.parseInt(srcTgt[0]);
        int tgt = Integer.parseInt(srcTgt[1]);
        result.remove(new Pair<Integer, Integer>(src+1,tgt+1));
      }
      for (Pair<Integer, Integer> pair : result)
        out.println(sentenceNr + " " + pair.arg1 + " " + pair.arg2);
      ++sentenceNr;
    }

  }

  private static void beata2naacl() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    int lineNr = 0;
    int sentenceNr = 1;
    for (String line = reader.readLine(); line != null; line = reader.readLine(), ++lineNr) {
      if (line.equals("")) {
        ++sentenceNr;
        //System.out.println();
        continue;
      }
      String[] alignments = line.split("[,]");
      for (String alignment : alignments) {
        String[] srcTgt = alignment.split("[.]");
        System.out.println(sentenceNr + " " + srcTgt[0] + " " + srcTgt[1]);
      }
      ++sentenceNr;
    }
  }
}

