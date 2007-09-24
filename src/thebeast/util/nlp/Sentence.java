package thebeast.util.nlp;

import thebeast.util.Util;

import java.util.ArrayList;

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

  public void addToken(String ... attributes){
    ArrayList<String> l = new ArrayList<String>(attributes.length);
    for (String s : attributes) l.add(s);
    add(l);
  }

  public String getAttribute(int token, int index){
    return get(token).get(index);
  }

  public String getQuotedAttribute(int token, int index){
    return Util.quote(get(token).get(index));
  }

}
