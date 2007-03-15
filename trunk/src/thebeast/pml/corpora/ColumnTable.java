package thebeast.pml.corpora;

import java.util.HashMap;
import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class ColumnTable {
  private HashMap<Integer, HashMap<Integer, String>> rows = new HashMap<Integer, HashMap<Integer, String>>();
  private HashMap<Integer,Integer> maxStringLength = new HashMap<Integer, Integer>();

  private int columns = 0;

  public void add(int line, int column, String value) {
    if (column >= columns)
      columns = column + 1;
    HashMap<Integer, String> row = rows.get(line);
    if (row == null) {
      row = new HashMap<Integer, String>();
      rows.put(line, row);
    }
    String old = row.get(column);
    row.put(column, old == null ? value : old + "_" + value);

    Integer maxSize = maxStringLength.get(column);
    if (maxSize == null || maxSize < value.length()){
      maxStringLength.put(column, value.length());
    }
  }

  public void write(PrintStream os, int firstLine, boolean withLineNrs) {

    for (int i = firstLine; i < rows.size(); ++i) {
      HashMap<Integer, String> row = rows.get(i);
      if (withLineNrs)
        os.printf("%-3d", i);
      for (int j = 0; j < columns; ++j) {
        if (j > 0) os.print("\t");
        String value = row.get(j);
        os.printf("%-"+ (maxStringLength.get(j) + 2) + "s", value != null ? value : "_");
      }
      os.println();
    }
    os.println();
  }
}
