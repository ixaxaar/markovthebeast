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
    double totalF1 = 0;
    double totalScore = 0;
    double totalTime = 0;
    long totalMemoryUsage = 0;
    int totalIterations = 0;
    long totalViolations = 0;
    for (int i = 0; i < args.length; ++i){
      //System.err.println(args[i]);
      BufferedReader reader = new BufferedReader(new FileReader(args[i]));
      double f1 = 0;
      double score = 0;
      double timeInSolve = 0;
      double timeInUpdate = 0;
      double time = 0;
      long memoryUsage=0;
      long clauses = 0;
      int iterations = 0;
      long violations = 0;
      String[] firstRow = reader.readLine().split("[\\.]");
      long size = 0;//Integer.parseInt(firstRow[1]);
      int level = -1;
      boolean inIteration = false;
      for (String line = reader.readLine(); line != null; line = reader.readLine()){
        line = line.trim();
        if (line.startsWith("Best Solution")){
          reader.readLine();
          reader.readLine();
          line = reader.readLine().trim();
          //System.out.println(line);
          StringTokenizer tokenizer = new StringTokenizer(line,"[\t ]",false);
          //tokenizer.nextToken();
          f1 = Double.parseDouble(tokenizer.nextToken());
          score = Double.parseDouble(tokenizer.nextToken());
          violations = Integer.parseInt(tokenizer.nextToken());
          line = reader.readLine().trim();
          //System.err.println(line);
//          tokenizer = new StringTokenizer(line,"[\t ]",false);
//          tokenizer.nextToken();
//          tokenizer.nextToken();
//          tokenizer.nextToken();
//          tokenizer.nextToken();
//          clauses = Long.parseLong(tokenizer.nextToken());
//          inIteration = true;
          level = 0;
        } else if (line.startsWith("Memory usage")){
          memoryUsage = Long.parseLong(line.substring(line.indexOf(":")+1).trim());
        } else if (line.startsWith("Iterations")){
          iterations = Integer.parseInt(line.substring(line.indexOf(":")+1).trim());
        } else if (line.startsWith("Time spent")){
          time = Integer.parseInt(line.substring(line.indexOf(":")+1).trim());
        } else if (line.startsWith("solve") && level == 0){
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
        } else if (line.equals("") && inIteration){
          inIteration = false;
        } else if (line.startsWith("root")){
          inIteration = false;
        }
        if (inIteration) {
          StringTokenizer tokenizer = new StringTokenizer(line,"[\t ]",false);
          //System.err.println(line);
          iterations = Integer.parseInt(tokenizer.nextToken());
        }
      }
      totalF1 += f1;
      totalScore += score;
      totalTime += time;
      totalViolations += violations;
      totalMemoryUsage += memoryUsage;
      totalIterations += iterations;
      System.out.printf("%-5d %-5d %-10.4f %10d %-10.4f %-10.2f %-10.2f %-10.2f %-10d %-10d\n",
              iterations, size, f1, memoryUsage, score, timeInSolve, timeInUpdate, time, clauses, violations);
//      System.out.println(iterations + ", " +  size + ", " +  f1 + ", " +  score + ", " +  timeInSolve
//              + ", " +  timeInUpdate + ", " +  time + ", " +  clauses + ", " +  violations);
      //System.out.println(args[i]);
    }
    double count = args.length;
    System.out.println("Count: " + args.length);
    System.out.printf("%-20s %10.4f\n", "Avg F1:", totalF1 / count);
    System.out.printf("%-20s %10.4f\n", "Avg Score:", totalScore / count);
    System.out.printf("%-20s %10.4f\n", "Avg Violations:", totalViolations / count);
    System.out.printf("%-20s %10.4f\n", "Avg Iterations:", totalIterations / count);
    System.out.printf("%-20s %10.4f\n", "Avg Memory use:", totalMemoryUsage / (count*1024*1024));
    System.out.printf("%-20s %10.4f\n", "Avg Time(ms):", totalTime / count);


  }

}
