package thebeast.util.nlp;

import thebeast.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.IOException;

public class Sentence extends ArrayList<ArrayList<String>> {

  public Sentence(int initialCapacity) {
    super(initialCapacity);
  }

  public Sentence() {
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < size(); ++i)
      result.append(get(i).get(0)).append(" ");
    return result.toString();
  }

  public void addToken(String... attributes) {
    ArrayList<String> l = new ArrayList<String>(attributes.length);
    for (String s : attributes) l.add(s);
    add(l);
  }

  public String getAttribute(int token, int index) {
    return get(token).get(index);
  }

  public String getQuotedAttribute(int token, int index) {
    return Util.quote(get(token).get(index));
  }

  public String toCharacterRelation(int index) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < size(); ++i) {
      int charIndex = 0;
      for (char ch : getAttribute(i, index).toCharArray()) {
        result.append(i).append(" ").append(charIndex++).append(" \"").append(ch).append("\"\n");
      }
    }
    return result.toString();
  }

  public String toListString() {
    return super.toString();
  }

  public void addAttributesFrom(Sentence s, int fromAttribute, int toAttribute) {
    int index = 0;
    try {
      for (List<String> token : this) token.add(toAttribute, s.get(index++).get(fromAttribute));
    } catch (RuntimeException e) {
      //throw e;
      System.out.println(super.toString());
      System.out.println(s.toListString());
    }
  }

  public void parseTabs(BufferedReader reader, int from) throws IOException {
    int token = from;
    for (String line = reader.readLine(); line != null && !line.trim().equals(""); line = reader.readLine()) {
      StringTokenizer tokenizer = new StringTokenizer(line, " \t", false);
      ArrayList<String> atts;
      if (token < size())
        atts = get(token);
      else {
        atts = new ArrayList<String>();
        add(atts);
      }
      String att;
      while (tokenizer.hasMoreTokens()) {
        att = tokenizer.nextToken();
        atts.add(att);
      }
      ++token;
    }
  }

}
