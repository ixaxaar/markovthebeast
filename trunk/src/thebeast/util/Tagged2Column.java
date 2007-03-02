package thebeast.util;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 02-Mar-2007 Time: 15:47:34
 */
public class Tagged2Column {
  public static void tagged2column(InputStream in, OutputStream out, int prefix) throws IOException {
     BufferedReader reader = new BufferedReader(new InputStreamReader(in));
     PrintStream writer = new PrintStream(out);
     for (String line = reader.readLine(); line != null; line = reader.readLine()){
       String[] tokens = line.split("[\t ]");
       for (String token : tokens){
         String[] wordTag = token.split("[_]");
         writer.println(prefix == -1 || wordTag[1].length() <= prefix ? wordTag[1] : wordTag[1].substring(0,prefix));
       }
       writer.println();
     }
     writer.close();
   }

   public static void main(String[] args) throws IOException {
     int prefix = args.length == 0 ? -1 : Integer.valueOf(args[0]);
     tagged2column(System.in, System.out, prefix);
   }

}
