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
        String[] split = src.split("[ ]");
        for (String word : split) alignedSentencePair.source.addToken(word);
      } else if (line.startsWith("<translation>")) {
        String tgt = line.substring(13, line.length() - 14);
        String[] split = tgt.split("[\t ]");
        for (String word : split) alignedSentencePair.target.addToken(word);
      } else if (line.startsWith("<matrix>")) {
        //the first row contains not translated target words
        //reader.readLine();
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
    out.println("0\tNONE");
    for (int i = 0; i < pair.source.size(); ++i) {
      out.println(i+1 + "\t" + pair.source.getQuotedAttribute(i, 0));
    }
    out.println();

    out.println(">target");
    out.println("0\tNONE");
    for (int i = 0; i < pair.target.size(); ++i) {
      out.println(i+1 + "\t" + pair.target.getQuotedAttribute(i, 0));
    }
    out.println();

    out.println(">align");
    for (Pair<Integer, Integer> alignment : pair.alignments) {
      out.println(alignment.arg1 + "\t" + alignment.arg2);
    }
    out.println();

    out.println(">reldist");

    for (int src = 0; src < pair.source.size(); ++src) {
      double relsrc = src / (double) pair.source.size();
      for (int tgt = 0; tgt < pair.target.size(); ++tgt) {
        double reltgt = tgt / (double) pair.target.size();
        printFeature(out, src, tgt, bin(relsrc - reltgt, 0.0, 0.5, 1.0));
        //printFeature(out, tgt, src, bin(reltgt - relsrc, 0.0, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0));
      }
    }

    out.println();


  }

  private static double bin(double value, double... steps) {
    double toCompare = value > 0 ? value : -value;
    for (double step : steps)
      if (toCompare < step) return value > 0 ? step : -step;

    return value;
  }

  private static void printBinaryFeature(PrintStream out, String name, int src, int tgt, Object value) {
    out.println(name + "\t" + src + "\t" + tgt + "\t\"" + value + "\"");
  }

  private static void printFeature(PrintStream out, int src, int tgt, Object value) {
    out.println(src + "\t" + tgt + "\t\"" + value + "\"");
  }


  public static void main(String[] args) throws IOException {
    String src = args[0];
    String tgt = args[1];
    extractFromGale(new FileInputStream(src), new PrintStream(tgt));
  }


}
