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

}
