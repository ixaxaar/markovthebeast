package thebeast.util.nlp;

import thebeast.util.Pair;

import java.util.HashSet;
import java.util.StringTokenizer;
import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 22-Nov-2007 Time: 18:39:35
 */
public class BilingualDictionary {

  private HashSet<Pair<String, String>> word2word = new HashSet<Pair<String, String>>();
  private HashSet<Pair<String, String>> char2word = new HashSet<Pair<String, String>>();
  private HashSet<Pair<String, String>> word2char = new HashSet<Pair<String, String>>();
  private HashSet<Pair<String, String>> char2char = new HashSet<Pair<String, String>>();
  private HashSet<String> stopwords = new HashSet<String>();


  public BilingualDictionary() {
    stopwords.add("to");
    stopwords.add("of");
    stopwords.add("the");
    stopwords.add("a");
//    stopwords.add("on");
//    stopwords.add("from");
//    stopwords.add("with");
//    stopwords.add("over");
//    stopwords.add("under");
  }

  public void loadCEDict(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      String[] split = line.split("[/]");
      String[] chinese = split[0].split("[ ]");
      String tradional = chinese[0];
      String simplified = chinese[1];
      for (int index = 1; index < split.length; ++index) {
        String english = split[index];
        StringTokenizer brackets = new StringTokenizer(english, "()", true);
        String token;
        boolean inBrackets = false;
        while (brackets.hasMoreTokens()) {
          token = brackets.nextToken().trim();
          if (token.equals("(")) {
            inBrackets = true;
          } else if (token.equals(")")) {
            inBrackets = false;
          } else {
            if (!inBrackets) {
              String[] words = token.split("[ ]");
              word2word.add(new Pair<String, String>(simplified, token));
              for (String word : words) {
                if (!stopwords.contains(word)) {
                  word2char.add(new Pair<String, String>(simplified, word));
                  for (char ch : simplified.toCharArray()) {
                    char2char.add(new Pair<String, String>(String.valueOf(ch), word));
                  }
                }
              }
              for (char ch : simplified.toCharArray()) {
                char2word.add(new Pair<String, String>(String.valueOf(ch), token));
              }

            }
          }
        }
      }

    }
  }

  public void loadDictCC(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      if (line.startsWith("#") || line.equals("")) continue;
      String[] de_en = line.split("::");
      String de = de_en[0];
      String en = de_en[1];
      String clean_de = clean(de);
      String clean_en = clean(en);
      String[] de_words = clean_de.split("[ ]");
      String[] en_words = clean_en.split("[ ]");
      word2word.add(new Pair<String, String>(clean_de,clean_en));
      for (String word: en_words) word2char.add(new Pair<String, String>(clean_de, word));
      for (String word: de_words) char2word.add(new Pair<String, String>(word, clean_en));
      for (String w1 : de_words)
        for (String w2 : en_words)
          char2char.add(new Pair<String, String>(w1,w2));

    }
  }

  private String clean(String de) {
    StringTokenizer brackets = new StringTokenizer(de, "(){}[]\"", true);
    boolean inBrackets = false;
    String token;
    StringBuffer result = new StringBuffer();
    while (brackets.hasMoreTokens()) {
      token = brackets.nextToken().trim();
      if (token.equals("(") || token.equals("{") || token.equals("[") || token.equals("\"")) {
        inBrackets = true;
      } else if (token.equals(")") || token.equals("}") || token.equals("]") || token.equals("\"")) {
        inBrackets = false;
      } else {
        if (!inBrackets) result.append(token.trim());
      }
    }
    return result.toString().toLowerCase();
  }

  public boolean containsChar2Char(String ch1, String ch2) {
    return char2char.contains(new Pair<String, String>(ch1, ch2));
  }

  public boolean containsWord2Char(String ch1, String ch2) {
    return word2char.contains(new Pair<String, String>(ch1, ch2));
  }

  public boolean containsChar2Word(String ch1, String ch2) {
    return char2word.contains(new Pair<String, String>(ch1, ch2));
  }

  public boolean containsWord2Word(String ch1, String ch2) {
    return word2word.contains(new Pair<String, String>(ch1, ch2));
  }

  public static void main(String[] args) throws IOException {
    String test = "ab ab [hallo]/(whatever) kl mn (shit)/";
    BilingualDictionary bilingualDictionary = new BilingualDictionary();
    bilingualDictionary.loadCEDict(new ByteArrayInputStream(test.getBytes()));
    System.out.println(bilingualDictionary.containsChar2Char("a", "kl"));
    System.out.println(bilingualDictionary.containsWord2Word("ab", "kl mn"));
    System.out.println(bilingualDictionary.containsWord2Char("ab", "kl"));
    System.out.println(bilingualDictionary.containsChar2Word("a", "kl mn"));

    String dictCC = "(alte) Schabracke {f} [abwertend] [ugs.] :: (old) unattractive woman";
    BilingualDictionary de_en = new BilingualDictionary();
    de_en.loadDictCC(new ByteArrayInputStream(dictCC.getBytes()));
    System.out.println(de_en.containsWord2Word("schabracke","unattractive woman"));
    System.out.println(de_en.containsChar2Char("schabracke","woman"));
    System.out.println(de_en.containsChar2Char("schabracke","old"));



  }

}
