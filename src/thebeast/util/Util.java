package thebeast.util;

import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Formatter;
import java.io.*;

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

  public static String toMemoryString(long bytes) {
    double display = bytes;
    Formatter f = new Formatter();
    if (display < 1024)
      return display + "b";
    display /= 1024.0;
    if (display < 1024)
      return String.format("%-3.3f",display) + "kb";
    display /= 1024.0;
    if (display < 1024)
      return String.format("%-3.3f",display) + "mb";
    display /= 1024.0;
    return String.format("%-3.3f",display) + "gb";

  }


}
