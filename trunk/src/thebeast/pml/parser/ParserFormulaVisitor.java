package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public interface ParserFormulaVisitor {
  void visitAtom(ParserAtom parserAtom);

  void visitConjuction(ParserConjunction parserConjunction);

  void visitDisjunction(ParserDisjunction parserDisjunction);
    
  void visitImplies(ParserImplies parserImplies);

  void visitCardinalityConstraint(ParserCardinalityConstraint parserCardinalityConstraint);

  void visitComparison(ParserComparison parserComparison);

  void visitAcyclicityConstraint(ParserAcyclicityConstraint parserAcyclicityConstraint);

  void visitNot(ParserNot parserNot);

  void visitUndefinedWeight(ParserUndefinedWeight parserUndefinedWeight);
}
