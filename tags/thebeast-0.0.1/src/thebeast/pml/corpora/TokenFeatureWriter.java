package thebeast.pml.corpora;

import thebeast.pml.GroundAtom;
import thebeast.pml.term.IntConstant;
import thebeast.pml.term.Constant;

/**
 * @author Sebastian Riedel
 */
public class TokenFeatureWriter implements AtomWriter {

  private int tokenArg;
  private int featureArg;
  private int column;
  private TokenProcessor processor;

  public TokenFeatureWriter(int column, int tokenArg, int featureArg, TokenProcessor processor) {
    this.column = column;
    this.tokenArg = tokenArg;
    this.featureArg = featureArg;
    this.processor = processor;
  }


  public int getColumn() {
    return column;
  }

  public TokenFeatureWriter(int column, int tokenArg, int featureArg) {
    this(column,tokenArg,featureArg, new Itself());
  }

  public void write(GroundAtom atom, TabFormatCorpus.ColumnTable table) {
    IntConstant token = (IntConstant) atom.getArguments().get(tokenArg);
    Constant feature = atom.getArguments().get(featureArg);
    table.set(token.getInteger(), column, processor.process(feature.toString()));
  }
}
