package thebeast.pml.corpora;

import java.util.Set;
import java.util.HashSet;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public class InSet implements TokenProcessor {

  private boolean ignoreCase;

  public final String YES = "Yes";
  public final String NO = "No";

  private Set<String> set;

  public InSet(Set<String> set,boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
    this.set = set;
  }

  public String process(String token) {
    return set.contains(ignoreCase ? token.toLowerCase() : token) ? YES : NO;    
  }

  public static Set<String> loadSet(InputStream is, boolean lowercase, Set<String> minus) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    HashSet<String> set = new HashSet<String>();
    for (String line = reader.readLine(); line != null; line = reader.readLine()){
      String[] split = line.split("[ \t]");
      for (String string : split) set.add(lowercase ? string.toLowerCase() : string);
    }
    if (minus != null) set.removeAll(minus);
    return set;
  }
}
