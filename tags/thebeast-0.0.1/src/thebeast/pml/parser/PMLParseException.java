package thebeast.pml.parser;

import java_cup.runtime.Symbol;

/**
 * @author Sebastian Riedel
 */
public class PMLParseException extends RuntimeException {

  private Symbol symbol;
  private int line, col;

  public PMLParseException(String string, Symbol symbol) {
    super(string);
    this.symbol = symbol;
    this.col = symbol.left;
    this.line = symbol.right;
  }

  public int getLine() {
    return line;
  }

  public int getCol() {
    return col;
  }
}
