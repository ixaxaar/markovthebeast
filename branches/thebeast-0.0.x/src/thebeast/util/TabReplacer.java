package thebeast.util;

import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 02-Mar-2007 Time: 16:53:40
 */
public class TabReplacer {
  public static void replace(InputStream original, InputStream replacement, OutputStream out,
                             int originalColumn, int replacementColumn) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(original));
    BufferedReader replacementReader = new BufferedReader(new InputStreamReader(replacement));
    PrintStream writer = new PrintStream(out);

    for (String originalLine = reader.readLine(); originalLine != null; originalLine = reader.readLine()){
      String replacementLine = replacementReader.readLine();
      if (!originalLine.trim().equals("")){
        String[] splitOriginal = originalLine.split("[\t ]");
        String[] splitReplacement = replacementLine.split("[\t ]");
        for (int i = 0; i < splitOriginal.length; ++i){
          if (i > 0) writer.print("\t");
          if (i == originalColumn) writer.print(splitReplacement[replacementColumn]);
          else writer.print(splitOriginal[i]);
        }
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
    int replacementCol = Integer.valueOf(args[3]);
    replace(new FileInputStream(originalFile),new FileInputStream(replacementFile), System.out,origCol, replacementCol);
  }
}
