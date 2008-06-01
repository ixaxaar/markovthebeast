package thebeast.pml.corpora;

import thebeast.pml.GroundAtom;
import thebeast.pml.term.IntConstant;

/**
 * @author Sebastian Riedel
 */
public  class ConstantWriter implements AtomWriter {

  private int tokenArg;
  private int column;
  private String constant;


  public ConstantWriter(int column, int tokenArg, String constant) {
    this.column = column;
    this.tokenArg = tokenArg;
    this.constant = constant;
  }


  public int getColumn() {
    return column;
  }

  public void write(GroundAtom atom, TabFormatCorpus.ColumnTable table) {
    IntConstant token = (IntConstant) atom.getArguments().get(tokenArg);
    table.set(token.getInteger(),column,constant);
  }
}
