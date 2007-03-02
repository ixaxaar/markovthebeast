package thebeast.util;

import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import java.io.*;

/**
 * @author Sebastian Riedel
 */
public class Util {

  public static String toStringWithDelimiters(Collection objects, String delim){
    StringBuffer buffer = new StringBuffer();
    int index = 0;
    for (Object obj : objects){
      if (index ++ > 0) buffer.append(delim);
      buffer.append(obj);
    }
    return buffer.toString();
  }



}
