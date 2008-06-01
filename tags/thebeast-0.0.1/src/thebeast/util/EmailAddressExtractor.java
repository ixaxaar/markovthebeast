package thebeast.util;

import java.io.*;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 15:27:52
 */
public class EmailAddressExtractor {

  public static void extract(InputStream is, OutputStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintStream out = new PrintStream(os);
    String line;

    LinkedList<String> all = new LinkedList<String>();

    while ((line = reader.readLine()) != null) {
      StringTokenizer tokenizer = new StringTokenizer(line);
      while (tokenizer.hasMoreTokens()){
        String token = tokenizer.nextToken();
        //out.println(token);
        if (token.contains("@")) {
          //out.print("->" + token);
          all.add(token);
        }
      }
    }
    System.err.println("total: " + all.size());
    int index = 0;
    HashSet<String> unique = new HashSet<String>(all);
    System.err.println("unique: " + unique.size());
    for (String email : unique){
      if (index++ > 0) out.print(", ");
      out.print(email);
    }
    out.close();
  }

  public static void main(String[] args) throws IOException {
    extract(System.in,System.out);
  }

}
