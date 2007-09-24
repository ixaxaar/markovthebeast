package thebeast.util;

import thebeast.util.nlp.Sentence;

import java.io.*;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 24-Sep-2007 Time: 14:40:53
 */
public class AlignmentExtractor {


  private static class AlignedSentencePair {
    Sentence source = new Sentence();
    Sentence target = new Sentence();

    private HashSet<Pair<Integer, Integer>> alignments = new HashSet<Pair<Integer, Integer>>();

    public void align(int src, int tgt) {
      alignments.add(new Pair<Integer, Integer>(src, tgt));
    }

    public boolean isAligned(int src, int tgt) {
      return alignments.contains(new Pair<Integer, Integer>(src, tgt));
    }

  }


  private static void extractFromGale(InputStream in, PrintStream out) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    AlignedSentencePair alignedSentencePair = new AlignedSentencePair();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.startsWith("<seg")) {
        alignedSentencePair = new AlignedSentencePair();
      } else if (line.startsWith("<source>")) {
        String src = line.substring(8, line.length() - 9);
        String[] split = src.split("[\t ]");
        for (String word : split) alignedSentencePair.source.addToken(word);
      } else if (line.startsWith("<target>")) {
        String tgt = line.substring(8, line.length() - 9);
        String[] split = tgt.split("[\t ]");
        for (String word : split) alignedSentencePair.target.addToken(word);
      } else if (line.startsWith("<matrix>")) {
        line = reader.readLine();
        int row = 0;
        while (!line.startsWith("</matrix>")) {
          String[] split = line.split("[ ]");
          for (int col = 0; col < split.length; ++col) {
            if (split[col].equals("1")) alignedSentencePair.align(row, col);
          }
          line = reader.readLine();
          ++row;
        }
      } else if (line.startsWith("</seg")) {
        extractAndWrite(alignedSentencePair, out);
      }


    }
  }

  private static void extractAndWrite(AlignedSentencePair pair, PrintStream out) {
    out.println(">>");
    out.println(">source");
    for (int i = 0; i < pair.source.size(); ++i) {
      out.println(i + "\t" + pair.source.getQuotedAttribute(i,0));
    }
    out.println();

    out.println(">tgt");
    for (int i = 0; i < pair.target.size(); ++i) {
      out.println(i + "\t" + pair.target.getQuotedAttribute(i,0));
    }
    out.println();

    out.println(">align");
    for (Pair<Integer, Integer> alignment : pair.alignments) {
      out.print(alignment.arg1 + "\t" + alignment.arg2);
    }
    out.println();

    out.println();


  }


  public static void main(String[] args) throws IOException {
    String src = args[0];
    String tgt = args[1];
    extractFromGale(new FileInputStream(src), new PrintStream(tgt));
  }
  


}
