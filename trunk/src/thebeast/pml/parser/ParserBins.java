package thebeast.pml.parser;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class ParserBins extends ParserTerm {

  public final List<ParserTerm> bins;
  public final ParserTerm argument;


  public ParserBins(List<ParserTerm> bins, ParserTerm argument) {
    this.bins = bins;
    this.argument = argument;
  }

  public void acceptParserTermVisitor(ParserTermVisitor visitor) {
    visitor.visitBins(this);
  }
}
