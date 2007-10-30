package thebeast.util;

import thebeast.util.nlp.Sentence;

import java.io.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 24-Sep-2007 Time: 14:40:53
 */
public class AlignmentExtractor {


  private static Model1 src2tgt;
  private static Model1 tgt2src;

  private static class Model1 {
    private HashMap<Pair<String, String>, Double> table = new HashMap<Pair<String, String>, Double>();

    public void load(InputStream in)
            throws IOException {

      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        String[] split = line.split("[ ]");
        table.put(new Pair<String, String>(split[0], split[1]), Double.parseDouble(split[2]));
      }
    }

    public double getProb(String s1, String s2) {
      Double result = table.get(new Pair<String, String>(s1, s2));
      return result == null ? 0.0 : result;
    }

  }

  private static class AlignedSentencePair {
    Sentence source = new Sentence();
    Sentence target = new Sentence();
    private HashSet<Integer> alignedSrcs = new HashSet<Integer>();
    private HashSet<Integer> alignedTgts = new HashSet<Integer>();

    private HashSet<Pair<Integer, Integer>> alignments = new HashSet<Pair<Integer, Integer>>();

    public void align(int src, int tgt) {
      alignments.add(new Pair<Integer, Integer>(src, tgt));
      alignedSrcs.add(src);
      alignedTgts.add(tgt);
    }

    public boolean isAligned(int src, int tgt) {
      return alignments.contains(new Pair<Integer, Integer>(src, tgt));
    }

    public void alignUnalignedTo(int to) {
      for (int i = 1; i < source.size(); ++i)
        if (!alignedSrcs.contains(i)) align(i, to);
      for (int i = 1; i < target.size(); ++i)
        if (!alignedTgts.contains(i)) align(to, i);
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

  private static void extractFromNAACL(InputStream src, InputStream tgt,
                                       InputStream align, PrintStream out, int offset) throws IOException {
    BufferedReader srcReader = new BufferedReader(new InputStreamReader(src));
    BufferedReader tgtReader = new BufferedReader(new InputStreamReader(tgt));
    BufferedReader alignReader = new BufferedReader(new InputStreamReader(align));

    String srcLine = srcReader.readLine();
    String tgtLine = tgtReader.readLine();
    String[] alignSplit = alignReader.readLine().split("[ ]");
    int sentenceNr = Integer.parseInt(alignSplit[0]);

    int lineNr = 0;
    while (srcLine != null) {
      AlignedSentencePair alignedSentencePair = new AlignedSentencePair();
      String[] srcSplit = srcLine.split("[ ]");
      for (String word : srcSplit) {
        alignedSentencePair.source.addToken(word);
      }
      String[] tgtSplit = tgtLine.split("[ ]");
      for (String word : tgtSplit) {
        alignedSentencePair.target.addToken(word);
      }
      while (sentenceNr == lineNr) {
        String line = alignReader.readLine();
        if (line == null) break;
        int srcNr = Integer.parseInt(alignSplit[1]);
        int tgtNr = Integer.parseInt(alignSplit[2]);
        alignedSentencePair.align(srcNr + offset, tgtNr + offset);
        alignSplit = line.split("[ ]");
        sentenceNr = Integer.parseInt(alignSplit[0]);
      }
      alignedSentencePair.alignUnalignedTo(0);

      srcLine = srcReader.readLine();
      tgtLine = tgtReader.readLine();
      ++lineNr;
      extractAndWrite(alignedSentencePair, out);
    }

  }


  private static void extractAndWrite(AlignedSentencePair pair, PrintStream out) {
    out.println(">>");
    out.println(">source");
    out.println("0\tNONE");
    for (int i = 0; i < pair.source.size(); ++i) {
      out.println(i + 1 + "\t" + pair.source.getQuotedAttribute(i, 0));
    }
    out.println();

    out.println(">target");
    out.println("0\tNONE");
    for (int i = 0; i < pair.target.size(); ++i) {
      out.println(i + 1 + "\t" + pair.target.getQuotedAttribute(i, 0));
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

    if (src2tgt != null) {
      out.println(">m1src2tgt");
      for (int src = 0; src < pair.source.size(); ++src) {
        for (int tgt = 0; tgt < pair.target.size(); ++tgt) {
          printFeature(out, src, tgt, bin(src2tgt.getProb(
                  pair.source.getAttribute(src, 0), pair.target.getAttribute(tgt, 0)), 0.0, 0.001, 0.01, 0.1, 0.5));
        }
      }
    }
    if (tgt2src != null) {
      out.println(">m1tgt2src");
      for (int src = 0; src < pair.source.size(); ++src) {
        for (int tgt = 0; tgt < pair.target.size(); ++tgt) {
          printFeature(out, src, tgt, bin(tgt2src.getProb(
                  pair.source.getAttribute(src, 0), pair.target.getAttribute(tgt, 0)), 0.0, 0.001, 0.01, 0.1, 0.5));
        }
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

  private static Map<Integer, String> loadVocab(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    HashMap<Integer, String> result = new HashMap<Integer, String>();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      String[] split = line.split("[ ]");
      result.put(Integer.parseInt(split[0]), split[1]);
    }
    return result;
  }


  public static void main(String[] args) throws IOException {
    String type = args[0];
    String stem = args[1];
    String dst = stem + ".atoms";
    File m1st = new File(stem + ".m1.src2tgt");
    File m1ts = new File(stem + ".m1.tgt2src");
    if (m1st.exists()) {
      System.err.println("Loading Model 1 src -> tgt");
      src2tgt = new Model1();
      src2tgt.load(new FileInputStream(m1st));
    }
    if (m1ts.exists()) {
      System.err.println("Loading Model 1 tgt -> src");
      tgt2src = new Model1();
      tgt2src.load(new FileInputStream(m1ts));
    }
    if (type.equalsIgnoreCase("gale")) {
      String src = stem + ".gale";
      extractFromGale(new FileInputStream(src), new PrintStream(dst));
    } else if (type.equalsIgnoreCase("gale")) {
      String src = stem + ".src";
      String tgt = stem + ".tgt";
      String align = stem + ".align";
      extractFromNAACL(new FileInputStream(src), new FileInputStream(tgt), new FileInputStream(align),
              new PrintStream(dst), -1);
    } else if (type.equalsIgnoreCase("phil")) {
      String src = stem + ".src";
      String tgt = stem + ".tgt";
      String align = stem + ".align";
      extractFromNAACL(new FileInputStream(src), new FileInputStream(tgt), new FileInputStream(align),
              new PrintStream(dst), 0);
    } else {
      System.out.println("Usage: [gale|naacl|phil] <stem> <dstfile>");

    }

  }


}
