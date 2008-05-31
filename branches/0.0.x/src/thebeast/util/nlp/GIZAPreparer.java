package thebeast.util.nlp;

import thebeast.util.Counter;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 26-Oct-2007 Time: 19:54:55
 */
public class GIZAPreparer {

  public static void main(String[] args) throws IOException {
    String sourceFile = args[0];
    String targetFile = args[1];
    String dstStem = args[2];
    HashMap<String,Integer> sourceVocab = new HashMap<String, Integer>();
    HashMap<String,Integer> targetVocab = new HashMap<String, Integer>();
    Counter<String> srcCounter = new Counter<String>();
    Counter<String> tgtCounter = new Counter<String>();
    BufferedReader src = new BufferedReader(new FileReader(sourceFile));
    BufferedReader tgt = new BufferedReader(new FileReader(targetFile));
    PrintStream sourceVocabFile = new PrintStream(dstStem + ".src.vocab");
    PrintStream targetVocabFile = new PrintStream(dstStem + ".tgt.vocab");
    PrintStream src2tgt = new PrintStream(dstStem + ".src2tgt.table");
    PrintStream tgt2src = new PrintStream(dstStem + ".tgt2src.table");
    String srcLine = src.readLine();
    String tgtLine = tgt.readLine();
    while (srcLine != null){
      src2tgt.println("1");
      tgt2src.println("1");
      StringBuffer srcBuffer = new StringBuffer();
      StringBuffer tgtBuffer = new StringBuffer();
      convertToIDs(srcLine, srcCounter, sourceVocab, srcBuffer);
      convertToIDs(tgtLine, tgtCounter, targetVocab, tgtBuffer);
      src2tgt.append(srcBuffer).append("\n");
      src2tgt.append(tgtBuffer).append("\n");
      tgt2src.append(tgtBuffer).append("\n");
      tgt2src.append(srcBuffer).append("\n");
      srcLine = src.readLine();
      tgtLine = tgt.readLine();
    }
    writeVocab(srcCounter, sourceVocabFile, sourceVocab);
    writeVocab(tgtCounter, targetVocabFile, targetVocab);
    sourceVocabFile.close();
    targetVocabFile.close();
    src2tgt.close();
    tgt2src.close();

  }

  private static void writeVocab(Counter<String> srcCounter, PrintStream sourceVocabFile, final HashMap<String, Integer> sourceVocab) {
    ArrayList<String> words = new ArrayList<String>(srcCounter.keySet());
    Collections.sort(words, new Comparator<String>() {
      public int compare(String o1, String o2) {
        return sourceVocab.get(o1) - sourceVocab.get(o2);
      }
    });
    for (String word : words){
      sourceVocabFile.println(sourceVocab.get(word) + " " + word + " " + srcCounter.get(word));
    }
  }

  private static void convertToIDs(String srcLine, Counter<String> srcCounter, HashMap<String, Integer> sourceVocab, StringBuffer srcBuffer) {
    String[] srcSplit = srcLine.trim().split("[ ]");
    for (String word : srcSplit) {
      srcCounter.increment(word,1);
      Integer id = sourceVocab.get(word);
      if (id == null){
        id = sourceVocab.size()+1;
        sourceVocab.put(word,id);
      }
      srcBuffer.append(id).append(" ");
    }
  }

}
