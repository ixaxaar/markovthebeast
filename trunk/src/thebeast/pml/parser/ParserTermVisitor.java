package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public interface ParserTermVisitor {
  void visitNamedConstant(ParserNamedConstant parserNamedConstant);

  void visitIntConstant(ParserIntConstant parserIntConstant);

  void visitParserAdd(ParserAdd parserAdd);

  void visitParserMinus(ParserMinus parserMinus);

  void visitDontCare(ParserDontCare parserDontCare);

  void visitFunctionApplication(ParserFunctionApplication parserFunctionApplication);

  void visitDoubleConstant(ParserDoubleConstant parserDoubleConstant);

  void visitVariable(ParserVariable parserVariable);

  void visitBins(ParserBins parserBins);

  void visitBoolConstant(ParserBoolConstant parserBoolConstant);

  void visitParserTimes(ParserTimes parserTimes);
}
