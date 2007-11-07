package thebeast.util;

import java.util.Collection;

/**
 * @author Sebastian Riedel
 */
public class Util {

  public static String toStringWithDelimiters(Collection objects, String delim) {
    StringBuffer buffer = new StringBuffer();
    int index = 0;
    for (Object obj : objects) {
      if (index++ > 0) buffer.append(delim);
      buffer.append(obj);
    }
    return buffer.toString();
  }
  public static String toEscapedStringWithDelimiters(Collection objects, String delim) {
    StringBuffer buffer = new StringBuffer();
    int index = 0;
    for (Object obj : objects) {
      if (index++ > 0) buffer.append(delim);
      String string = obj.toString();
      if (string.startsWith("\"") && string.endsWith("\"")){
        string = "\"" + replaceQuotationMarks(string.substring(1,string.length()-1)) + "\"";
      }
      buffer.append(string);
    }
    return buffer.toString();
  }

  public static String replaceQuotationMarks(String s){
    if (s.contains("\"")){
      return s.replaceAll("[\"]", "''");
    }
    return s;
  }

  public static String toMemoryString(double bytes) {
    double display = bytes;
    if (display < 1024)
      return display + "b";
    display /= 1024.0;
    if (display < 1024)
      return String.format("%-1.1f",display) + "kb";
    display /= 1024.0;
    if (display < 1024)
      return String.format("%-1.1f",display) + "mb";
    display /= 1024.0;
    return String.format("%-1.1f",display) + "gb";

  }

  public static String toTimeString(double millis) {
    double display = millis;
    if (display < 1000)
      return display + "ms";
    display /= 1000.0;
    if (display < 60)
      return String.format("%-2.2f",display) + "s";
    display /= 60;
    if (display < 60)
      return String.format("%-2.2f",display) + "m";
    display /= 60;
    if (display < 24)
      return String.format("%-2.2f",display) + "h";
    return String.format("%-2.2f",display) + "d";

  }

  public static String unquote(String label) {
    return (label.startsWith("\"")) ? label.substring(1, label.length() - 1) : label;
  }

  public static int fakultaet(int n, int k){
    if (k == 1) return n;
    if (n == 0) return 0;
    return n * fakultaet(n-1,k-1);
  }

  public static int[][] nBalls(int n, int[] balls){
    int size = fakultaet(balls.length, n) / fakultaet(n,n);
    int[][] result = new int[size][n];
    int[] pointers = new int[n];
    for (int i = 0; i < n; ++i) pointers[i] = i;
    int ballIndex = n - 1;
    int dst = 0;
    outer:
    while (ballIndex >= 0) {
      if (pointers[ballIndex] == balls.length) {
        --ballIndex;
        continue;
      }
      if (ballIndex < n - 1) {        
        ++pointers[ballIndex];
        if (pointers[ballIndex] == balls.length) {
          --ballIndex;
          continue;
        }
        for (int i = 1; ballIndex + i < n; ++i){
          pointers[ballIndex+i] = pointers[ballIndex+i-1] + 1;
          if (pointers[ballIndex+i]>=balls.length) {
            --ballIndex;
            continue outer;
          }
        }
        ballIndex = n -1;
      } else {
        for (int i = 0; i < pointers.length; ++i)
          result[dst][i] = balls[pointers[i]];
        ++pointers[ballIndex];
        ++dst;

      }
    }
    return result;
  }


  public static String quote(String string) {
    return "\"" + string + "\"";
  }
}
