package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public abstract class AbstractParserTermVisitor implements ParserTermVisitor{
  public void visitNamedConstant(ParserNamedConstant parserNamedConstant) {
    throw new IllegalArgumentException();
  }

  public void visitIntConstant(ParserIntConstant parserIntConstant) {
    throw new IllegalArgumentException();

  }

  public void visitParserAdd(ParserAdd parserAdd) {
    throw new IllegalArgumentException();

  }

  public void visitParserMinus(ParserMinus parserMinus) {
    throw new IllegalArgumentException();

  }

  public void visitDontCare(ParserDontCare parserDontCare) {
    throw new IllegalArgumentException();

  }

  public void visitFunctionApplication(ParserFunctionApplication parserFunctionApplication) {
    throw new IllegalArgumentException();

  }

  public void visitDoubleConstant(ParserDoubleConstant parserDoubleConstant) {
    throw new IllegalArgumentException();

  }

  public void visitVariable(ParserVariable parserVariable) {
    throw new IllegalArgumentException();

  }

  public void visitBins(ParserBins parserBins) {
    throw new IllegalArgumentException();

  }
}
