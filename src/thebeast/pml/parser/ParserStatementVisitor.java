package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Feb-2007 Time: 20:04:30
 */
public interface ParserStatementVisitor {
  void visitCreateType(ParserCreateType parserCreateType);

  void visitCreatePredicate(ParserCreatePredicate parserCreatePredicate);

  void visitCreateWeightFunction(ParserCreateWeightFunction parserCreateWeightFunction);

  void visitFactorFormula(ParserFactorFormula parserFactorFormula);

  void visitImport(ParserImport parserImport);

  void visitAddPredicateToModel(ParserAddPredicateToModel parserAddPredicateToModel);

  void visitInspect(ParserInspect parserInspect);

  void visitLoad(ParserLoad parserLoad);

  void visitPrint(ParserPrint parserPrint);

  void visitSolve(ParserSolve parserSolve);

  void visitGenerateTypes(ParserGenerateTypes parserGenerateTypes);

  void visitSaveTypes(ParserSaveTypes parserSaveTypes);

  void visitLoadCorpus(ParserLoadCorpus parserLoadCorpus);

  void visitLoadScores(ParserLoadScores parserLoadScores);

  void visitShift(ParserShift parserShift);

  void visitGreedy(ParserGreedy parserGreedy);

  void visitLoadWeights(ParserLoadWeights parserLoadWeights);

  void visitCollect(ParserCollect parserCollect);

  void visitPrintWeights(ParserPrintWeights parserPrintWeights);

  void visitLearn(ParserLearn parserLearn);

  void visitSet(ParserSet parserSet);

  void visitClear(ParserClear parserClear);

  void visitSaveCorpus(ParserSaveCorpus parserSaveCorpus);

  void visitSave(ParserSave parserSave);

  void visitTest(ParserTest parserTest);

  void visitCreateIndex(ParserCreateIndex parserCreateIndex);

}
