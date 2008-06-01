package thebeast.util;

import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 02-Mar-2007 Time: 16:53:40
 */
public class TabAppender {
  public static void append(InputStream original, InputStream replacement, OutputStream out,
                             int columnToAdd) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(original));
    BufferedReader replacementReader = new BufferedReader(new InputStreamReader(replacement));
    PrintStream writer = new PrintStream(out);

    for (String originalLine = reader.readLine(); originalLine != null; originalLine = reader.readLine()){
      String replacementLine = replacementReader.readLine();
      if (!originalLine.trim().equals("")){
        writer.print(originalLine);
        String[] splitReplacement = replacementLine.split("[\t ]");
        writer.print("\t" + splitReplacement[columnToAdd]);
        writer.println();
      } else
        writer.println();
    }
    writer.close();
  }

  public static void main(String[] args) throws IOException {
    File originalFile = new File(args[0]);
    File replacementFile = new File(args[1]);
    int origCol = Integer.valueOf(args[2]);
    append(new FileInputStream(originalFile),new FileInputStream(replacementFile), System.out,origCol);
  }
}
