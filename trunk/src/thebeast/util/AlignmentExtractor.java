package thebeast.util;

import gnu.trove.TObjectDoubleHashMap;
import gnu.trove.TObjectIntHashMap;
import thebeast.util.nlp.Sentence;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 24-Sep-2007 Time: 14:40:53
 */
public class AlignmentExtractor {

  private static Model1 src2tgt;
  private static Model1 tgt2src;
  private static Counter<String> srcCounts, tgtCounts;
  private static int MAX_RANK = 10;
  private static double[] m1bins = new double[]{0.000001, 0.00001, 0.0001, 0.001,
          0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1, 0.2, 0.3, 0.4, 0.5, 1.0};
//  private static PrintStream c45srcRank;
//  private static PrintStream c45tgtRank;
//
//  static {
//    try {
//      c45srcRank = new PrintStream("m1src.data");
//      c45tgtRank = new PrintStream("m1tgt.data");
//      PrintStream out = new PrintStream("m1src.names");
//      for (int i = 0; i < MAX_RANK; ++i) {
//        if (i > 0) out.print(", ");
//        out.print("Rank" + i);
//      }
//      out.println(".");
//      out.println("prob: continuous");
//      out.println();
//      out.close();
//
//      out = new PrintStream("m1tgt.names");
//      for (int i = 0; i < MAX_RANK; ++i) {
//        if (i > 0) out.print(", ");
//        out.print("Rank" + i);
//      }
//      out.println(".");
//      out.println("prob: continuous");
//      out.println();
//      out.close();
//
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    }
//  }

  private static class Model1 {

    private static class IndexPair implements Serializable {
      int index1, index2;

      public IndexPair(int index1, int index2) {
        this.index1 = index1;
        this.index2 = index2;
      }

      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexPair indexPair = (IndexPair) o;

        return index1 == indexPair.index1 && index2 == indexPair.index2;

      }

      public int hashCode() {
        int result;
        result = index1;
        result = 31 * result + index2;
        return result;
      }
    }

    private TObjectIntHashMap<String> srcVocab = new TObjectIntHashMap<String>();
    private TObjectIntHashMap<String> tgtVocab = new TObjectIntHashMap<String>();

    private TObjectDoubleHashMap<IndexPair> probs = new TObjectDoubleHashMap<IndexPair>();

    private double threshold = 0.00001;

    public void loadFromStrings(InputStream in)
            throws IOException {

      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int lineNr = 0;
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        ++lineNr;
        if (lineNr % 10000 == 0) System.err.print(".");
        if (lineNr % 1000000 == 0) System.err.println(" " + lineNr + "(" + probs.size() + ")");
        String[] split = line.split("[ ]");
        double prob = Double.parseDouble(split[2]);
        if (prob < threshold) continue;
        setProb(split[0].trim(), split[1].trim(), prob);
      }
    }

    public void setProb(String s1, String s2, double prob) {
      int id1 = getSourceId(s1);
      int id2 = getTargetId(s2);
      probs.put(new IndexPair(id1, id2), prob);
    }

    public int getSourceId(String s1) {
      if (srcVocab.containsKey(s1)) {
        return srcVocab.get(s1);
      }
      int id = srcVocab.size();
      srcVocab.put(s1, id);
      return id;
    }

    public int getTargetId(String s1) {
      if (tgtVocab.containsKey(s1)) {
        return tgtVocab.get(s1);
      }
      int id = tgtVocab.size();
      tgtVocab.put(s1, id);
      return id;
    }

    public double getProb(String s1, String s2) {
      IndexPair pair = new IndexPair(getSourceId(s1), getTargetId(s2));
      if (!probs.containsKey(pair)) return 0;
      //probs.writeExternal();
      return probs.get(pair);
    }

    public double getProb(int srcId, int tgtId) {
      IndexPair pair = new IndexPair(srcId, tgtId);
      if (!probs.containsKey(pair)) return 0;
      return probs.get(pair);
    }

    public void loadSourceVocab(InputStream is) throws IOException {
      loadVocab(is, srcVocab);
    }

    public void loadTargetVocab(InputStream is) throws IOException {
      loadVocab(is, tgtVocab);
    }

    private static void loadVocab(InputStream is, TObjectIntHashMap<String> result) throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        String[] split = line.split("[ ]");
        result.put(split[1], Integer.parseInt(split[0]));
      }
    }

    public void writeBinary(OutputStream os) throws IOException {
      ObjectOutput oo = new ObjectOutputStream(os);
      srcVocab.writeExternal(oo);
      tgtVocab.writeExternal(oo);
      probs.writeExternal(oo);
    }

    public void readBinary(InputStream is) throws IOException, ClassNotFoundException {
      ObjectInput io = new ObjectInputStream(is);
      srcVocab.readExternal(io);
      tgtVocab.readExternal(io);
      probs.readExternal(io);
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
            if (split[col].equals("1")) alignedSentencePair.align(col, row);
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

    int lineNr = 1;
    while (srcLine != null) {
      AlignedSentencePair alignedSentencePair = new AlignedSentencePair();
      String[] srcSplit = srcLine.split("[ ]");
      //alignedSentencePair.source.addToken("NULL");
      for (String word : srcSplit) {
        alignedSentencePair.source.addToken(word);
      }
      String[] tgtSplit = tgtLine.split("[ ]");
      //alignedSentencePair.target.addToken("NULL");
      for (String word : tgtSplit) {
        alignedSentencePair.target.addToken(word);
      }
      while (sentenceNr == lineNr) {
        String line = alignReader.readLine();
        if (line == null) break;
        int srcNr = Integer.parseInt(alignSplit[2]);
        int tgtNr = Integer.parseInt(alignSplit[1]);
        alignedSentencePair.align(srcNr + offset, tgtNr + offset);
        alignSplit = line.split("[ ]");
        sentenceNr = Integer.parseInt(alignSplit[0]);
      }
      //alignedSentencePair.alignUnalignedTo(0);

      srcLine = srcReader.readLine();
      tgtLine = tgtReader.readLine();
      ++lineNr;
      if (lineNr % 10 == 0) System.err.print(".");
      if (lineNr % 1000 == 0) System.err.println(" " + lineNr);
      extractAndWrite(alignedSentencePair, out);
    }

  }


  private static void extractAndWrite(AlignedSentencePair pair, PrintStream out) {
    out.println(">>");
    out.println(">source");
    //out.println("0\tNULL");
    for (int i = 0; i < pair.source.size(); ++i) {
      out.println(i + "\t" + pair.source.getQuotedAttribute(i, 0));
    }
    out.println();

    out.println(">target");
    //out.println("0\tNULL");
    for (int i = 0; i < pair.target.size(); ++i) {
      out.println(i + "\t" + pair.target.getQuotedAttribute(i, 0));
    }
    out.println();

    out.println(">srcchar");
    out.println(pair.source.toCharacterRelation(0));

    out.println(">tgtchar");
    out.println(pair.target.toCharacterRelation(0));

    Counter<Integer> srcFertility = new Counter<Integer>();
    Counter<Integer> tgtFertility = new Counter<Integer>();

    out.println(">align");
    for (Pair<Integer, Integer> alignment : pair.alignments) {
      out.println(alignment.arg1 + "\t" + alignment.arg2);
      srcFertility.increment(alignment.arg1, 1);
      tgtFertility.increment(alignment.arg2, 1);
    }
    out.println();

    out.println(">srcfert");
    for (int src = 0; src < pair.source.size(); ++src) {
      out.println(src + "\t" + srcFertility.get(src));
    }
    out.println();

    out.println(">tgtfert");
    for (int tgt = 0; tgt < pair.target.size(); ++tgt) {
      out.println(tgt + "\t" + tgtFertility.get(tgt));
    }
    out.println();

    out.println(">reldist");
    for (int src = 0; src < pair.source.size(); ++src) {
      double relsrc = src / (double) pair.source.size();
      for (int tgt = 0; tgt < pair.target.size(); ++tgt) {
        double reltgt = tgt / (double) pair.target.size();
        printQuotedFeature(out, src, tgt, bin(relsrc - reltgt,
                0.02, 0.05, 0.1, 0.25, 0.5, 0.75, 1.0));
      }
    }
    out.println();

    if (src2tgt != null) {
      out.println(">m1src2tgt");
      for (int src = 0; src < pair.source.size(); ++src) {
        for (int tgt = 0; tgt < pair.target.size(); ++tgt) {
          printQuotedFeature(out, src, tgt, bin(src2tgt.getProb(
                  pair.target.getAttribute(tgt, 0), pair.source.getAttribute(src, 0)),
                  m1bins));
        }
      }
      out.println();

      out.println(">m1srcnull");
      for (int src = 0; src < pair.source.size(); ++src) {
        printSingleQuotedFeature(out, src, bin(src2tgt.getProb(
                pair.source.getAttribute(src, 0), "NULL"),
                m1bins));
      }
      out.println();

      out.println(">srchighestm1");
      printRankedTranslations(pair.source, pair.target, src2tgt, out, true);
      out.println();

      out.println(">srcm1ranks");
      printRanksAndProbs(pair.source, pair.target, src2tgt, out);
      out.println();

    }
    if (tgt2src != null) {
      out.println(">m1tgt2src");
      for (int src = 0; src < pair.source.size(); ++src) {
        for (int tgt = 0; tgt < pair.target.size(); ++tgt) {
          printQuotedFeature(out, src, tgt, bin(tgt2src.getProb(
                  pair.source.getAttribute(src, 0), pair.target.getAttribute(tgt, 0)),
                  m1bins));
        }
      }
      out.println();

      out.println(">m1tgtnull");
      for (int tgt = 0; tgt < pair.target.size(); ++tgt) {
        printSingleQuotedFeature(out, tgt, bin(src2tgt.getProb(
                pair.target.getAttribute(tgt, 0), "NULL"),
                m1bins));
      }
      out.println();


      out.println(">tgthighestm1");
      printRankedTranslations(pair.target, pair.source, tgt2src, out, false);
      out.println();

      out.println(">tgtm1ranks");
      printRanksAndProbs(pair.target, pair.source, tgt2src, out);
      out.println();


    }

    out.println(">srccount");
    for (int i = 0; i < pair.source.size(); ++i) {
      out.println(i + 1 + "\t" + srcCounts.get(pair.source.getAttribute(i, 0)));
    }
    out.println();

    out.println(">tgtcount");
    for (int i = 0; i < pair.target.size(); ++i) {
      out.println(i + 1 + "\t" + tgtCounts.get(pair.target.getAttribute(i, 0)));
    }
    out.println();


  }

  private static void printRankedTranslations(Sentence source, Sentence target,
                                              Model1 model1, PrintStream out,
                                              boolean srcFirst) {
    for (int src = 0; src < source.size(); ++src) {
      ArrayList<Pair<Integer, Double>> ranked = new ArrayList<Pair<Integer, Double>>(target.size());
      for (int tgt = 0; tgt < target.size(); ++tgt) {
        ranked.add(new Pair<Integer, Double>(tgt, model1.getProb(
                target.getAttribute(tgt, 0), source.getAttribute(src, 0))));
      }
      Collections.sort(ranked, new Comparator<Pair<Integer, Double>>() {
        public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
          return o1.arg2 > o2.arg2 ? -1 : o1.arg2 < o2.arg2 ? 1 : 0;
        }
      });
      for (int i = 0; i < target.size() && i < MAX_RANK; ++i) {
        printFeature(out, srcFirst ? src : ranked.get(i).arg1, srcFirst ? ranked.get(i).arg1 : src, i);
      }
    }
  }

  private static void printRanksAndProbs(Sentence source, Sentence target,
                                         Model1 model1, PrintStream out) {
    for (int src = 0; src < source.size(); ++src) {
      ArrayList<Pair<Integer, Double>> ranked = new ArrayList<Pair<Integer, Double>>(target.size());
      for (int tgt = 0; tgt < target.size(); ++tgt) {
        ranked.add(new Pair<Integer, Double>(tgt, model1.getProb(
                target.getAttribute(tgt, 0), source.getAttribute(src, 0))));
      }
      Collections.sort(ranked, new Comparator<Pair<Integer, Double>>() {
        public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
          return o1.arg2 > o2.arg2 ? -1 : o1.arg2 < o2.arg2 ? 1 : 0;
        }
      });
      for (int i = 0; i < target.size() && i < MAX_RANK; ++i)
        printQuotedFeature(out, src, i, bin(ranked.get(i).arg2, m1bins));
    }
  }


  private static double bin(double value, double... steps) {
    double toCompare = value > 0 ? value : -value;
    for (double step : steps)
      if (toCompare < step) return value >= 0 ? step : -step;

    return value;
  }

  private static void printBinaryFeature(PrintStream out, String name, int src, int tgt, Object value) {
    out.println(name + "\t" + src + "\t" + tgt + "\t\"" + value + "\"");
  }

  private static void printQuotedFeature(PrintStream out, int src, int tgt, Object value) {
    out.println(src + "\t" + tgt + "\t\"" + value + "\"");
  }

  private static void printSingleQuotedFeature(PrintStream out, int src, Object value) {
    out.println(src + "\t\"" + value + "\"");
  }


  private static void printFeature(PrintStream out, int src, int tgt, Object value) {
    out.println(src + "\t" + tgt + "\t" + value);
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

  private static Counter<String> getCounts(InputStream is) throws IOException {
    Counter<String> result = new Counter<String>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      String[] split = line.split("[ ]");
      for (String word : split) result.increment(word, 1);
    }
    return result;

  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    String type = args[0];
    String stem = args[1];
    String dst = stem + ".atoms";
    File m1st = new File(stem + ".m1.src2tgt");
    File m1stbin = new File(stem + ".m1.src2tgt.bin");
    File m1ts = new File(stem + ".m1.tgt2src");
    File m1tsbin = new File(stem + ".m1.tgt2src.bin");
    if (m1stbin.exists()) {
      System.err.println("Loading Model 1 src -> tgt");
      src2tgt = new Model1();
      src2tgt.readBinary(new FileInputStream(m1stbin));
      System.err.println();
    } else if (m1st.exists()) {
      System.err.println("Loading Model 1 src -> tgt");
      src2tgt = new Model1();
      src2tgt.loadFromStrings(new FileInputStream(m1st));
      System.err.println();
      //System.err.println("Saving");
      //src2tgt.writeBinary(new FileOutputStream(m1stbin));
      //System.err.println();
    }
    if (m1tsbin.exists()) {
      System.err.println("Loading Model 1 tgt -> src");
      tgt2src = new Model1();
      tgt2src.readBinary(new FileInputStream(m1tsbin));
      System.err.println();
    } else if (m1ts.exists()) {
      System.err.println("Loading Model 1 tgt -> src");
      tgt2src = new Model1();
      tgt2src.loadFromStrings(new FileInputStream(m1ts));
      System.err.println();
      //System.err.println("Saving");
      //tgt2src.writeBinary(new FileOutputStream(m1tsbin));
      //System.err.println();
    }
    if (type.equalsIgnoreCase("gale")) {
      String src = stem + ".gale";
      extractFromGale(new FileInputStream(src), new PrintStream(dst));
    } else if (type.equalsIgnoreCase("naacl")) {
      String src = stem + ".src";
      String tgt = stem + ".tgt";
      String align = stem + ".align";
      extractFromNAACL(new FileInputStream(src), new FileInputStream(tgt), new FileInputStream(align),
              new PrintStream(dst), 0);
    } else if (type.equalsIgnoreCase("phil")) {
      String src = stem + ".src";
      String tgt = stem + ".tgt";
      String align = stem + ".align";
      System.err.println("Count words");
      srcCounts = getCounts(new FileInputStream(src));
      tgtCounts = getCounts(new FileInputStream(tgt));
      System.err.println("Extract alignments");
      extractFromNAACL(new FileInputStream(src), new FileInputStream(tgt), new FileInputStream(align),
              new PrintStream(dst), 0);
      System.err.println();
    } else {
      System.out.println("Usage: [gale|naacl|phil] <stem> <dstfile>");
      System.out.println("Files:");
      System.out.printf("%20s%50s\n", "<stem>.src", "tokenized source sentences");
      System.out.printf("%20s%50s\n", "<stem>.tgt", "tokenized target sentences");
      System.out.printf("%20s%50s\n", "<stem>.align", "naacl format alignments");
      System.out.printf("%20s%50s\n", "<stem>.m1.src2tgt", "model 1 source to target table");
      System.out.printf("%20s%50s\n", "<stem>.m1.src2tgt", "model 1 target to source table");
    }

  }


}
