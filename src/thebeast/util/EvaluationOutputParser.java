package thebeast.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 31-May-2007 Time: 18:12:33
 */
public class EvaluationOutputParser {

  public static void main(String[] args) throws IOException {
    PrintStream out = new PrintStream(args[0]);
    for (int i = 1; i < args.length; ++i){
      BufferedReader reader = new BufferedReader(new FileReader(args[i]));
      double f1 = 0;
      double score = 0;
      double timeInSolve = 0;
      double timeInUpdate = 0;
      double time = 0;
      long clauses = 0;
      int iterations = 0;
      String[] firstRow = reader.readLine().split("[\\.]");
      long size = Integer.parseInt(firstRow[1]);
      int level = -1;
      boolean inIteration = false;
      for (String line = reader.readLine(); line != null; line = reader.readLine()){
        line = line.trim();
        if (line.startsWith("Iter.")){
          reader.readLine();
          line = reader.readLine().trim();
          //System.out.println(line);
          StringTokenizer tokenizer = new StringTokenizer(line,"[\t ]",false);
          tokenizer.nextToken();
          f1 = Double.parseDouble(tokenizer.nextToken());
          score = Double.parseDouble(tokenizer.nextToken());
          line = reader.readLine().trim();
          tokenizer = new StringTokenizer(line,"[\t ]",false);
          tokenizer.nextToken();
          tokenizer.nextToken();
          tokenizer.nextToken();
          tokenizer.nextToken();
          clauses = Long.parseLong(tokenizer.nextToken());
          inIteration = true;
          level = 0;
        }  else if (line.startsWith("solve") && level == 0){
          StringTokenizer tokenizer = new StringTokenizer(line,"[\t ]",false);
          tokenizer.nextToken();
          String token = tokenizer.nextToken();
          time = Double.parseDouble(token.substring(0, token.length()-2));
          ++level;
          inIteration = false;
        } else if (line.startsWith("solve") && level == 1){
          StringTokenizer tokenizer = new StringTokenizer(line,"[\t ]",false);
          tokenizer.nextToken();
          String token = tokenizer.nextToken();
          timeInSolve = Double.parseDouble(token.substring(0, token.length()-2));
          ++level;
        } else if (line.startsWith("update")&& level == 2){
          StringTokenizer tokenizer = new StringTokenizer(line,"[\t ]",false);
          tokenizer.nextToken();
          String token = tokenizer.nextToken();
          timeInUpdate = Double.parseDouble(token.substring(0, token.length()-2));
          ++level;
        } else if (line.startsWith("root")){
          inIteration = false;
        }
        if (inIteration) {
          StringTokenizer tokenizer = new StringTokenizer(line,"[\t ]",false);
          iterations = Integer.parseInt(tokenizer.nextToken());
        }
      }
      out.printf("%-5d %-5d %-10.4f %-10.4f %-10.2f %-10.2f %-10.2f %-10d\n",
              iterations, size, f1, score, timeInSolve, timeInUpdate, time, clauses);
      System.out.println(args[i]);
    }
    out.close();

  }

}
